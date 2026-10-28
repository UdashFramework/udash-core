package io.udash
package rest

import com.avsystem.commons.*
import com.avsystem.commons.annotation.{bincompat, explicitGenerics}
import com.typesafe.scalalogging.{LazyLogging, Logger as ScalaLogger}
import io.udash.rest.RestServlet.*
import io.udash.rest.raw.*
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.Ack.{Continue, Stop}
import monix.execution.cancelables.{CompositeCancelable, MultiAssignCancelable, SingleAssignCancelable}
import monix.execution.{Ack, Cancelable, Scheduler}
import monix.reactive.observers.Subscriber
import monix.reactive.{Consumer, Observable}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{ByteArrayOutputStream, EOFException, IOException}
import java.nio.channels.ClosedChannelException
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncEvent, AsyncListener}
import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.control.{NoStackTrace, NonFatal}

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds
  final val DefaultMaxPayloadSize = 16 * 1024 * 1024L // 16MB
  final val CookieHeader = "Cookie"
  final val DefaultStreamingBatchSize = 100
  final val DefaultCancelOnDisconnect = false
  // Recorded on a request its client abandoned, as nginx does, so that it is not logged as a success
  final val ClientClosedRequestStatus = 499
  private final val BufferSize = 8192

  // Walks the cause chain like Jetty's QuietException.isQuiet, so this does not depend on how the container wraps
  // the failure of a reset stream or a dropped connection
  @tailrec private def isClientGone(t: Throwable): Boolean = t match {
    case null => false
    case _: EOFException | _: ClosedChannelException => true
    case _ => isClientGone(t.getCause)
  }

  // Raised only by a write into the container, so that a client going away mid-write is told apart from an
  // EOFException raised by the handler or its stream, which is a server failure and must stay a 500
  private final class ClientGoneException(cause: Throwable) extends IOException(cause)

  private def write(op: => Unit): Unit =
    try op
    catch { case NonFatal(e) if isClientGone(e) => throw new ClientGoneException(e) }

  private def timedOutMessage(timeout: FiniteDuration): String = s"server operation timed out after $timeout"

  private[rest] final class IdleTimeoutException(val timeout: FiniteDuration)
    extends Exception(timedOutMessage(timeout)) with NoStackTrace

  private final val IdleTicks = 4

  // Fails with IdleTimeoutException a source that emitted nothing for `timeout`, as Monix's timeoutOnSlowUpstream does,
  // but counts ticks instead of reading the clock for every element, which does not scale across threads on some
  // platforms. A silence is cut after `timeout` plus at most one tick. Time spent in `downstream.onNext` counts as
  // activity, so writes to a slow client do not trip it.
  private[rest] final class IdleBoundedObservable[A](source: Observable[A], timeout: FiniteDuration)
    extends Observable[A] {

    def unsafeSubscribeFn(downstream: Subscriber[A]): Cancelable = {
      val ticker = MultiAssignCancelable()
      val main = SingleAssignCancelable()
      main := source.unsafeSubscribeFn(new Subscriber[A] with Runnable { self =>
        implicit val scheduler: Scheduler = downstream.scheduler

        private val tick = timeout / IdleTicks
        // guarded by `lock`, which also keeps the timer's onError from overlapping an onNext
        private val lock = new ReentrantLock
        private var active = false
        private var processing = false
        private var quietTicks = 0
        private var isDone = false

        private def locked[T](code: => T): T = {
          lock.lock()
          try code
          finally lock.unlock()
        }

        scheduleTick()

        private def scheduleTick(): Unit =
          ticker := scheduler.scheduleOnce(tick.length, tick.unit, self)

        // A held lock means an element is being handed downstream, i.e. activity. Waiting for it would hold this
        // thread for as long as a write to a slow client.
        def run(): Unit =
          if (!lock.tryLock()) scheduleTick()
          else
            try {
              if (!isDone) {
                if (active || processing) {
                  active = false
                  quietTicks = 0
                } else quietTicks += 1

                if (quietTicks < IdleTicks) scheduleTick()
                else {
                  isDone = true
                  try downstream.onError(new IdleTimeoutException(timeout))
                  finally main.cancel()
                }
              }
            } finally lock.unlock()

        def onNext(elem: A): Future[Ack] = {
          def finishProcessing(): Ack = {
            active = true
            processing = false
            Continue
          }

          locked {
            if (isDone) Stop
            else {
              processing = true
              downstream.onNext(elem) match {
                case Continue => finishProcessing()
                case Stop =>
                  ticker.cancel()
                  Stop
                case async =>
                  async.flatMap {
                    case Continue => locked(finishProcessing())
                    case Stop =>
                      ticker.cancel()
                      Stop
                  }
              }
            }
          }
        }

        def onError(ex: Throwable): Unit = locked {
          if (!isDone) {
            isDone = true
            try downstream.onError(ex)
            finally ticker.cancel()
          }
        }

        def onComplete(): Unit = locked {
          if (!isDone) {
            isDone = true
            try downstream.onComplete()
            finally ticker.cancel()
          }
        }
      })
      CompositeCancelable(main, ticker)
    }
  }

  // As for the container's own async timeout, which counts whole milliseconds and sets no bound at 0
  private[rest] def handleTimeoutBound(handleTimeout: FiniteDuration): Opt[FiniteDuration] =
    handleTimeout.opt.filter(_.toMillis > 0)

  /** Wraps an implementation of some REST API trait into a Java Servlet.
    *
    * @param apiImpl
    *   implementation of some REST API trait
    * @param handleTimeout
    *   bound for calls without [[io.udash.rest.timeout timeout]], see [[RestServlet]]
    * @param maxPayloadSize
    *   maximum acceptable incoming payload size, in bytes; if exceeded, `413 Payload Too Large` response will be sent
    *   back
    * @param defaultStreamingBatchSize
    *   default batch when streaming [[StreamedBody.JsonList]]
    * @param cancelOnDisconnect
    *   whether to cancel the handler `Task` when the container reports that the client went away; see
    *   [[RestServlet.cancelOnDisconnect]]
    * @param timer
    *   runs the timeouts of calls, see [[RestServlet]]
    */
  @explicitGenerics
  def apply[RestApi: RawRest.AsRawRpc: RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration = DefaultHandleTimeout,
    maxPayloadSize: Long = DefaultMaxPayloadSize,
    defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
    cancelOnDisconnect: Boolean = DefaultCancelOnDisconnect,
    timer: OptArg[Scheduler] = OptArg.Empty,
  )(implicit scheduler: Scheduler
  ): RestServlet =
    new RestServlet(
      handleRequest = RawRest.asHandleRequestWithStreaming[RestApi](apiImpl),
      handleTimeout = handleTimeout,
      maxPayloadSize = maxPayloadSize,
      defaultStreamingBatchSize = defaultStreamingBatchSize,
      cancelOnDisconnect = cancelOnDisconnect,
      timer = timer,
    )

  @bincompat private[rest] def apply[RestApi: RawRest.AsRawRpc: RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
    cancelOnDisconnect: Boolean,
  )(implicit scheduler: Scheduler
  ): RestServlet = apply[RestApi](
    apiImpl,
    handleTimeout = handleTimeout,
    maxPayloadSize = maxPayloadSize,
    defaultStreamingBatchSize = defaultStreamingBatchSize,
    cancelOnDisconnect = cancelOnDisconnect,
    timer = OptArg.Empty,
  )

  @bincompat private[rest] def apply[RestApi: RawRest.AsRawRpc: RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
  )(implicit scheduler: Scheduler
  ): RestServlet = apply[RestApi](
    apiImpl,
    handleTimeout = handleTimeout,
    maxPayloadSize = maxPayloadSize,
    defaultStreamingBatchSize = DefaultStreamingBatchSize,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
    timer = OptArg.Empty,
  )

  @bincompat
  def apply[RestApi: RawRest.AsRawRpc: RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
  )(implicit scheduler: Scheduler
  ): RestServlet = apply[RestApi](
    apiImpl,
    handleTimeout = handleTimeout,
    maxPayloadSize = maxPayloadSize,
    defaultStreamingBatchSize = defaultStreamingBatchSize,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
    timer = OptArg.Empty,
  )
}

/** @param handleRequest
  *   the handler of REST requests. Only a handler that resolves calls, i.e. one made by `RawRest.asHandleRequest*` or
  *   `RawRest.withTimeouts`, or one of these wrapped with `RawRest.wrapHandleRequest`, has its calls bound by their
  *   [[io.udash.rest.timeout timeout]]; any other gets `handleTimeout` for all of them.
  * @param handleTimeout
  *   bound for calls without [[io.udash.rest.timeout timeout]]: a call that has not answered within it gets a 500 and
  *   is cancelled, and a streamed response ends once it has emitted nothing for that long. `timer` runs the former,
  *   `scheduler` the latter. Neither counts time spent writing to the client, which the container bounds, e.g. by
  *   Jetty's connector idle timeout. One under a millisecond sets no bound, as for the container's own async timeout.
  * @param cancelOnDisconnect
  *   cancel the handler `Task` when the container reports that the client went away, instead of letting it run until it
  *   completes or times out. Other asynchronous failures never cancel it. Off by default: an ordinary client abort
  *   (browser navigation, a client-side timeout, a load balancer reset) would otherwise interrupt a side-effecting
  *   handler mid-flight, so enable it once handlers are known to be cancellation-safe. Note that detection is transport
  *   dependent: `AsyncListener.onError` is only raised for protocols where the client's abort reaches the server on a
  *   connection it is already reading, e.g. HTTP/2 `RST_STREAM`. An aborted HTTP/1.1 request is not reported while the
  *   async cycle is idle, so it is still bounded by the call's timeout. Neither is an abort arriving before the
  *   container's dispatch of the request has returned, i.e. while `service` or an enclosing filter is still running:
  *   the handler then runs until it finishes or a write fails, bounded by the call's timeout. Independently of this
  *   setting, nothing is written into a request the container has already completed, so a streamed response is
  *   abandoned at the next chunk.
  * @param timer
  *   runs the timeouts of calls, `scheduler` if empty. A call that times out while `scheduler` has no free thread gets
  *   its 500 only once one frees up, so pass a dedicated one if handlers may block all of its threads, e.g.
  *   `Scheduler(executor)` over a `ScheduledThreadPoolExecutor` with `setRemoveOnCancelPolicy(true)`. Without that
  *   policy, as in `Scheduler.fixedPool` and `Scheduler.singleThread`, the timeout of every call answered in time stays
  *   queued until it would have fired. That also holds for `scheduler` when it is used instead of a `timer`;
  *   `Scheduler.global` does not retain them.
  */
class RestServlet(
  handleRequest: RawRest.HandleRequestWithStreaming,
  handleTimeout: FiniteDuration = DefaultHandleTimeout,
  maxPayloadSize: Long = DefaultMaxPayloadSize,
  defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
  customLogger: OptArg[Logger] = OptArg.Empty,
  cancelOnDisconnect: Boolean = DefaultCancelOnDisconnect,
  timer: OptArg[Scheduler] = OptArg.Empty,
)(implicit scheduler: Scheduler
) extends HttpServlet
    with LazyLogging {

  import RestServlet.*

  @bincompat private[rest] def this(
    handleRequest: RawRest.HandleRequestWithStreaming,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
    customLogger: OptArg[Logger],
    cancelOnDisconnect: Boolean,
  )(implicit scheduler: Scheduler
  ) = this(
    handleRequest,
    handleTimeout,
    maxPayloadSize,
    defaultStreamingBatchSize,
    customLogger,
    cancelOnDisconnect,
    timer = OptArg.Empty,
  )

  @bincompat private[rest] def this(
    handleRequest: RawRest.HandleRequestWithStreaming,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
    customLogger: OptArg[Logger],
  )(implicit scheduler: Scheduler
  ) = this(
    handleRequest,
    handleTimeout,
    maxPayloadSize,
    defaultStreamingBatchSize,
    customLogger,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
    timer = OptArg.Empty,
  )

  override protected lazy val logger: ScalaLogger =
    ScalaLogger(customLogger.getOrElse(LoggerFactory.getLogger(getClass.getName)))

  private val defaultTimeout: Opt[FiniteDuration] = handleTimeoutBound(handleTimeout)

  private val resolve: RestRequest => (Opt[FiniteDuration], Task[AbstractRestResponse]) = handleRequest match {
    case resolving: RawRest.ResolvingHandleRequest[AbstractRestResponse] => resolving.resolve
    case _ =>
      // debug, as a servlet built per request would log it for each
      logger.debug(
        "RestServlet's handler does not resolve calls, so @timeout does not apply to them. " +
          "Apply RawRest.withTimeouts to it, or wrap the RawRest.asHandleRequest* handler itself with " +
          "RawRest.wrapHandleRequest instead of a function, for @timeout to apply."
      )
      request => (Opt.Empty, Task.defer(handleRequest(request)))
  }

  private val timeoutScheduler: Scheduler = timer.getOrElse(scheduler)

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val asyncContext = request.startAsync()
    // Set once the response no longer belongs to this servlet, whether completed here or by the container; past
    // that point Jetty may have recycled the request for the next one on the connection (https://stackoverflow.com/a/27744537)
    val completed = new AtomicBoolean(false)
    // Claimed by whichever writes the response head: the handler's response, or the timer with its 500
    val headClaimed = new AtomicBoolean(false)
    val cancelable = SingleAssignCancelable()
    val completion = new ReentrantLock

    def locked[T](code: => T): T = {
      completion.lock()
      try code
      finally completion.unlock()
    }

    // Returns whether this call claimed the completion. Serialized so that onError returns only once a completion
    // in progress has called complete(); the container would otherwise start its error dispatch over that response.
    def completeWith(code: => Unit): Boolean = locked(completeLocked(code))

    def completeLocked(code: => Unit): Boolean = {
      val claimed = !completed.getAndSet(true)
      if (claimed) {
        try code
        catch {
          case e @ (_: IOException | _: IllegalStateException) => logger.warn("Failed to write REST response", e)
          case NonFatal(e) =>
            logger.error("Failed to write REST response", e)
            if (!response.isCommitted) replaceWithFailure(response, e)
        } finally {
          try asyncContext.complete()
          catch {
            // the container is completing the cycle itself: it recycled the request or is timing it out
            case e: IllegalStateException => logger.debug("REST request completion was rejected by the container", e)
          }
        }
      }
      claimed
    }

    // The call is bounded by the timer below, so the container must not time it out
    asyncContext.setTimeout(0)
    // Registered before the handler starts: a fast handler could otherwise complete the cycle first, and a
    // disconnect arriving before registration would reach no listener.
    asyncContext.addListener(new AsyncListener {
      def onComplete(event: AsyncEvent): Unit = completed.set(true)
      // only a container that ignored setTimeout(0) gets here, and the response is then its own
      def onTimeout(event: AsyncEvent): Unit = completed.set(true)
      // onError and the timer complete before they cancel: a write failing in between then still reaches the task's
      // error path instead of the scheduler's uncaught reporter.
      def onError(event: AsyncEvent): Unit = {
        val failure = event.getThrowable
        if (isClientGone(failure)) {
          // The client is gone, so completing here keeps the container from running its default error dispatch
          // (a 500 through the error handler) for what is not an application failure.
          logger.debug("REST request was aborted by the client", failure)
          completeWith(response.setStatus(ClientClosedRequestStatus))
          if (cancelOnDisconnect) cancelable.cancel()
        } else {
          // Any other async failure is left to the container's error dispatch; only stop writing here.
          logger.warn("REST request failed asynchronously", failure)
          locked(completed.set(true))
        }
      }
      def onStartAsync(event: AsyncEvent): Unit = ()
    })

    // readRequest must execute in Jetty thread but we want exceptions to be handled uniformly, hence the Try
    val (timeout, handle) = Try {
      val (callTimeout, response) = resolve(readRequest(request))
      (callTimeout.fold(defaultTimeout)(handleTimeoutBound), response)
    }.fold(e => (defaultTimeout, Task.raiseError(e)), identity)

    def timeOut(timeout: FiniteDuration): Runnable = () =>
      try {
        if (headClaimed.compareAndSet(false, true)) {
          // every holder of the lock has completed the request or is completing it, and waiting for one would delay
          // the other timeouts
          val answered = completion.tryLock() && {
            try completeLocked(writeFailure(response, timedOutMessage(timeout).opt))
            finally completion.unlock()
          }
          if (answered) logger.warn(s"REST request timed out: no response within $timeout")
          // Monix runs cancellation finalizers on the calling thread, which must stay free for other timeouts
          scheduler.execute(() => cancelable.cancel())
        }
      } catch {
        case NonFatal(e) => logger.error("Failed to time out REST request", e)
      }
    // A cancelled timer may stay queued until it is due, e.g. on Scheduler.singleThread, so it reaches the request
    // only through this reference, which claimHead clears
    val pendingTimeOut = new AtomicReference[Runnable]
    val timeoutTimer = timeout.map { t =>
      pendingTimeOut.set(timeOut(t))
      timeoutScheduler
        .scheduleOnce(t.toMillis, TimeUnit.MILLISECONDS, () => pendingTimeOut.getAndSet(null).opt.foreach(_.run()))
    }
    // Cancels only a timer that has not claimed the head: Monix may interrupt a running task it cancels, which would
    // cut the timer's 500 short
    def claimHead(): Boolean = headClaimed.compareAndSet(false, true) && {
      pendingTimeOut.set(null)
      timeoutTimer.foreach(_.cancel())
      true
    }

    cancelable :=
      (for {
        restResponse <- handle
        claimed <- Task(claimHead())
        _ <- Task.when(claimed)(
          unlessCompleted(completed)(setResponseHeaders(response, restResponse.code, restResponse.headers))
        )
        // the timer has answered, possibly before completed is set, so the response is only drained
        _ <- writeResponseBody(response, restResponse, if (claimed) completed else new AtomicBoolean(true), timeout)
      } yield claimed).executeAsync
        .guaranteeCase { _ =>
          // Outside executeAsync, so that it also runs for a task cancelled before it started. A timer that fires after
          // the handler's outcome must not wait on completeWith while that outcome is written.
          Task(claimHead()).void
        }
        .runAsync {
          case Right(false) =>
          // the timer claimed the head, and it completes the request itself
          case Right(true) =>
            completeWith(())
          case Left(e: HttpErrorException) =>
            completeWith(writeResponse(response, e.toResponse))
          case Left(e: ClientGoneException) =>
            logger.warn("Request was cancelled by the client during REST response", e.getCause)
            completeWith(())
          case Left(e: IdleTimeoutException) =>
            if (completeWith(writeFailure(response, e.getMessage.opt)))
              logger.warn(
                s"REST request timed out: its stream emitted nothing for ${e.timeout} before the response was sent"
              )
          case Left(e) if completed.get =>
            logger
              .warn("REST handler failed after the request had already been completed by a timeout or a disconnect", e)
          case Left(e) =>
            logger.error("Failed to handle REST request", e)
            completeWith(writeFailure(response, e.getMessage.opt))
        }
  }

  // A handler may outlive its request, e.g. when uncancelable or blocking, and the response object is then already
  // recycled for the next request on the connection
  private def unlessCompleted(completed: AtomicBoolean)(write: => Unit): Task[Unit] =
    Task(if (!completed.get) write)

  // A write the client cut short or one failing after completion ends the stream here: escaping, it can race a
  // cancellation. Checks completed right before writing, as the request may be completed while the chunk waits.
  private def writeStep(completed: AtomicBoolean)(op: => Unit): Task[Boolean] =
    Task.eval {
      try
        !completed.get && {
          write(op)
          true
        }
      catch {
        case NonFatal(e) if e.isInstanceOf[ClientGoneException] || completed.get =>
          logAbandonedStream(e)
          false
      }
    }

  private def logAbandonedStream(e: Throwable): Unit = e match {
    case e: ClientGoneException =>
      logger.warn("Request was cancelled by the client during streaming REST response", e.getCause)
    case e: IdleTimeoutException => logger.debug("Streaming REST response went idle while being drained", e)
    case _ => logger.warn("Streaming REST response failed after the request had already been completed", e)
  }

  private def setResponseHeaders(response: HttpServletResponse, code: Int, headers: IMapping[PlainValue]): Unit = {
    response.setStatus(code)
    headers.entries.foreach { case (name, PlainValue(value)) =>
      response.addHeader(name, value)
    }
  }

  private def writeNonEmptyBody(response: HttpServletResponse, body: HttpBody.NonEmpty): Unit = {
    val bytes = body.bytes
    response.setContentType(body.contentType)
    response.setContentLength(bytes.length)
    write(response.getOutputStream.write(bytes))
  }

  // Nothing has reached the client yet, so it gets a 500 rather than the default 200 with whatever was staged
  private def replaceWithFailure(response: HttpServletResponse, cause: Throwable): Unit =
    try {
      response.reset()
      writeFailure(response, cause.getMessage.opt)
    } catch {
      case NonFatal(e) => logger.warn("Failed to write REST failure response", e)
    }

  // Undoes only what this servlet staged for a stream's head, keeping the headers that filters set before it ran
  private def discardStreamHead(response: HttpServletResponse, headers: IMapping[PlainValue]): Unit = {
    response.resetBuffer()
    response.setContentType(null)
    response.setHeader("Content-Length", null)
    headers.entries.groupBy(_._1.toLowerCase(Locale.ROOT)).foreach { case (_, staged) =>
      val name = staged.head._1
      // setHeader clears every value of the name, and setResponseHeaders appended the staged ones after a filter's
      val kept = response.getHeaders(name).asScala.toList.dropRight(staged.size)
      response.setHeader(name, null)
      kept.foreach(response.addHeader(name, _))
    }
  }

  private def closeFailedStream(response: HttpServletResponse): Unit =
    try response.getOutputStream.close()
    catch {
      case NonFatal(e) => logger.warn("Failed to close the failed streaming REST response", e)
    }

  // `writeEnd` follows the last chunk unless the stream failed or a write was abandoned
  private def writeChunks[A](
    chunks: Observable[A],
    completed: AtomicBoolean,
    writeEnd: => Unit = (),
  )(
    writeChunk: A => Unit
  ): Task[Unit] =
    chunks
      .mapEval(chunk => writeStep(completed)(writeChunk(chunk)))
      // Once the request is completed, a disconnect may have cancelled the task, whose consumer would pass a failure to
      // the uncaught reporter, e.g. that of a source closed by the cancellation
      .onErrorRecoverWith {
        case e if completed.get =>
          logAbandonedStream(e)
          Observable.now(false)
      }
      .forallL(identity)
      .flatMap(allWritten => Task.when(allWritten)(writeStep(completed)(writeEnd).void))

  private def writeNonEmptyStreamedBody(
    response: HttpServletResponse,
    stream: StreamedRestResponse,
    responseBody: StreamedBody.NonEmpty,
    completed: AtomicBoolean,
    timeout: Opt[FiniteDuration],
  ): Task[Unit] = {
    def bounded[A](source: Observable[A]): Observable[A] = timeout.fold(source)(new IdleBoundedObservable(source, _))
    // The Content-Length header is intentionally omitted for streams.
    // This signals to the client that the response body size is not predetermined and will be streamed.
    // Clients implementing the streaming part of the REST interface contract MUST be prepared
    // to handle responses without Content-Length by reading data incrementally until the stream completes.
    // A stream is subscribed even once the request is completed, so that its finalizers run; its write step then
    // stops it at the first element.
    responseBody match {
      case single: StreamedBody.Single =>
        unlessCompleted(completed)(writeNonEmptyBody(response, single.body))
      case binary: StreamedBody.RawBinary =>
        unlessCompleted(completed)(response.setContentType(binary.contentType)) >>
          writeChunks(bounded(binary.content), completed) { chunk =>
            response.getOutputStream.write(chunk)
            response.getOutputStream.flush()
          }
      case jsonList: StreamedBody.JsonList =>
        unlessCompleted(completed)(response.setContentType(jsonList.contentType)) >> writeChunks(
          bounded(jsonList.elements)
            .bufferTumbling(jsonList.customBatchSize.getOrElse(defaultStreamingBatchSize))
            .switchIfEmpty(Observable(Seq.empty))
            .zipWithIndex,
          completed,
          writeEnd = response.getOutputStream.write("]".getBytes(jsonList.charset)),
        ) { case (batch, idx) =>
          val firstBatch = idx == 0
          if (firstBatch) {
            response.getOutputStream.write("[".getBytes(jsonList.charset))
            batch.iterator.zipWithIndex.foreach { case (e, idx) =>
              if (idx != 0) {
                response.getOutputStream.write(",".getBytes(jsonList.charset))
              }
              response.getOutputStream.write(e.value.getBytes(jsonList.charset))
            }
          } else
            batch.foreach { e =>
              response.getOutputStream.write(",".getBytes(jsonList.charset))
              response.getOutputStream.write(e.value.getBytes(jsonList.charset))
            }
          response.getOutputStream.flush()
        }
    }
  }.onErrorHandleWith {
    case e: ClientGoneException => Task(logAbandonedStream(e))
    // the output stream may already belong to the next request on the connection, so it must not be closed
    case ex if completed.get => Task(logAbandonedStream(ex))
    case ex if !response.isCommitted =>
      // nothing has reached the client, so the failure is answered like the handler's own
      unlessCompleted(completed)(discardStreamHead(response, stream.headers)) >> Task.raiseError(ex)
    case e: IdleTimeoutException =>
      Task {
        // the servlet API offers no way to abort a committed response, so a streamed one ends as if it had finished
        logger.warn(
          s"Streaming REST response was cut short, so the client got it incomplete: its stream emitted " +
            s"nothing for ${e.timeout}. Give the method a longer @timeout if the stream may pause for longer."
        )
        closeFailedStream(response)
      }
    case ex =>
      Task {
        // When an error occurs during streaming, we immediately close the connection rather than
        // attempting to send an error response. This is intentional because:
        // The client has likely already received and started processing partial data
        // for structured formats (like JSON arrays), the stream is now in an invalid state
        logger.error("Failure during streaming REST response", ex)
        closeFailedStream(response)
      }
  }

  private def writeResponseBody(
    response: HttpServletResponse,
    restResponse: AbstractRestResponse,
    completed: AtomicBoolean,
    timeout: Opt[FiniteDuration],
  ): Task[Unit] =
    restResponse match {
      case resp: RestResponse =>
        resp.body match {
          case HttpBody.Empty => Task.unit
          case neBody: HttpBody.NonEmpty => unlessCompleted(completed)(writeNonEmptyBody(response, neBody))
        }
      case stream: StreamedRestResponse =>
        stream.body match {
          case StreamedBody.Empty => Task.unit
          case neBody: StreamedBody.NonEmpty => writeNonEmptyStreamedBody(response, stream, neBody, completed, timeout)
        }
    }

  private def writeResponse(response: HttpServletResponse, restResponse: RestResponse): Unit = {
    setResponseHeaders(response, restResponse.code, restResponse.headers)
    restResponse.body match {
      case HttpBody.Empty =>
      case neBody: HttpBody.NonEmpty => writeNonEmptyBody(response, neBody)
    }
  }

  // Not through getWriter, which throws once a stream's head has taken the output stream
  private def writeFailure(response: HttpServletResponse, message: Opt[String]): Unit = {
    response.setStatus(500)
    message.foreach(msg => writeNonEmptyBody(response, HttpBody.Textual(msg, HttpBody.PlainType, HttpBody.Utf8Charset)))
  }

  private def readParameters(request: HttpServletRequest): RestParameters = {
    // can't use request.getPathInfo because it decodes the URL before we can split it
    val pathPrefix = request.getContextPath.orEmpty + request.getServletPath.orEmpty
    val path = PlainValue.decodePath(request.getRequestURI.stripPrefix(pathPrefix))

    val query = request.getQueryString.opt.map(PlainValue.decodeQuery).getOrElse(Mapping.empty)

    val headersBuilder = IMapping.newBuilder[PlainValue]
    request.getHeaderNames.asScala.foreach { headerName =>
      if (!headerName.equalsIgnoreCase(CookieHeader)) { // cookies are separate, don't include them into header params
        headersBuilder += headerName -> PlainValue(request.getHeader(headerName))
      }
    }
    val headers = headersBuilder.result()

    val cookiesBuilder = Mapping.newBuilder[PlainValue]
    request.getCookies.opt.getOrElse(Array.empty).foreach { cookie =>
      val cookieName = URLEncoder.decode(cookie.getName, plusAsSpace = true)
      val cookieValue = URLEncoder.decode(cookie.getValue, plusAsSpace = true)
      cookiesBuilder += cookieName -> PlainValue(cookieValue)
    }
    val cookies = cookiesBuilder.result()

    RestParameters(path, headers, query, cookies)
  }

  private def readBody(request: HttpServletRequest): HttpBody = {
    val contentLength = request.getContentLengthLong.opt.filter(_ != -1)
    contentLength.filter(_ > maxPayloadSize).foreach { length =>
      throw HttpErrorException.plain(413, s"Payload is larger than maximum $maxPayloadSize bytes ($length)")
    }

    request.getContentType.opt.fold(HttpBody.empty) { contentType =>
      val mediaType = HttpBody.mediaTypeOf(contentType)
      HttpBody.charsetOf(contentType) match {
        // if Content-Length is undefined, always read as binary in order to validate maximum length
        case Opt(charset) if contentLength.isDefined =>
          val bodyReader = request.getReader
          val bodyBuilder = new JStringBuilder
          val cbuf = new Array[Char](BufferSize)
          @tailrec def readLoop(): Unit = bodyReader.read(cbuf) match {
            case -1 =>
            case len =>
              bodyBuilder.append(cbuf, 0, len)
              readLoop()
          }
          readLoop()
          HttpBody.textual(bodyBuilder.toString, mediaType, charset)

        case _ =>
          val bodyIs = request.getInputStream
          val bodyOs = new ByteArrayOutputStream
          val bbuf = new Array[Byte](BufferSize)
          @tailrec def readLoop(): Unit = bodyIs.read(bbuf) match {
            case -1 =>
            case len =>
              bodyOs.write(bbuf, 0, len)
              if (bodyOs.size > maxPayloadSize) {
                throw HttpErrorException.plain(413, s"Payload is larger than maximum $maxPayloadSize bytes")
              }
              readLoop()
          }
          readLoop()
          HttpBody.binary(bodyOs.toByteArray, contentType)
      }
    }
  }

  private def readRequest(request: HttpServletRequest): RestRequest = {
    val method = HttpMethod.byName(request.getMethod)
    val parameters = readParameters(request)
    val body = readBody(request)
    RestRequest(method, parameters, body)
  }
}
