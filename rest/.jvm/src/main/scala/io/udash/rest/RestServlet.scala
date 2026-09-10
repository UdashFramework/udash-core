package io.udash
package rest

import com.avsystem.commons.*
import com.avsystem.commons.annotation.{bincompat, explicitGenerics}
import com.typesafe.scalalogging.{LazyLogging, Logger as ScalaLogger}
import io.udash.rest.RestServlet.*
import io.udash.rest.raw.*
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.cancelables.SingleAssignCancelable
import monix.reactive.{Consumer, Observable}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{ByteArrayOutputStream, EOFException}
import java.util.concurrent.atomic.AtomicBoolean
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncEvent, AsyncListener}
import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.util.control.NonFatal

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds
  final val DefaultMaxPayloadSize = 16 * 1024 * 1024L // 16MB
  final val CookieHeader = "Cookie"
  final val DefaultStreamingBatchSize = 100
  final val DefaultCancelOnDisconnect = false
  private final val BufferSize = 8192

  /**
   * Wraps an implementation of some REST API trait into a Java Servlet.
   *
   * @param apiImpl                   implementation of some REST API trait
   * @param handleTimeout             maximum time the servlet will wait for results returned by REST API implementation
   * @param maxPayloadSize            maximum acceptable incoming payload size, in bytes;
   *                                  if exceeded, `413 Payload Too Large` response will be sent back
   * @param defaultStreamingBatchSize default batch when streaming [[StreamedBody.JsonList]]
   * @param cancelOnDisconnect        whether to cancel the handler `Task` when the container reports that the
   *                                  request failed asynchronously, most importantly because the client is gone;
   *                                  see [[RestServlet.cancelOnDisconnect]]
   */
  @explicitGenerics def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration = DefaultHandleTimeout,
    maxPayloadSize: Long = DefaultMaxPayloadSize,
    defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
    cancelOnDisconnect: Boolean = DefaultCancelOnDisconnect,
  )(implicit
    scheduler: Scheduler
  ): RestServlet =
    new RestServlet(
      handleRequest = RawRest.asHandleRequestWithStreaming[RestApi](apiImpl),
      handleTimeout = handleTimeout,
      maxPayloadSize = maxPayloadSize,
      defaultStreamingBatchSize = defaultStreamingBatchSize,
      cancelOnDisconnect = cancelOnDisconnect,
    )

  @bincompat private[rest] def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration ,
    maxPayloadSize: Long,
  )(implicit
    scheduler: Scheduler
  ): RestServlet = apply[RestApi](
    apiImpl,
    handleTimeout = handleTimeout,
    maxPayloadSize = maxPayloadSize,
    defaultStreamingBatchSize = DefaultStreamingBatchSize,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
  )

  @bincompat private[rest] def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
  )(implicit
    scheduler: Scheduler
  ): RestServlet = apply[RestApi](
    apiImpl,
    handleTimeout = handleTimeout,
    maxPayloadSize = maxPayloadSize,
    defaultStreamingBatchSize = defaultStreamingBatchSize,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
  )
}

/**
 * @param cancelOnDisconnect cancel the handler `Task` when the container reports that the request failed
 *                           asynchronously, most importantly because the client went away, instead of letting it
 *                           run to completion. Off by default: an ordinary client abort (browser navigation, a
 *                           client-side timeout, a load balancer reset) would otherwise interrupt a side-effecting
 *                           handler mid-flight, so enable it once handlers are known to be cancellation-safe. Note
 *                           that detection is transport dependent: `AsyncListener.onError` is only raised for
 *                           protocols where the client's abort reaches the server on a connection it is already
 *                           reading, e.g. HTTP/2 `RST_STREAM`. An aborted HTTP/1.1 request is not reported while
 *                           the async cycle is idle, so it is still only bounded by `handleTimeout`. Independently
 *                           of this setting, nothing is written into a request the container has already
 *                           completed, so a streamed response is abandoned at the next chunk.
 */
class RestServlet(
  handleRequest: RawRest.HandleRequestWithStreaming,
  handleTimeout: FiniteDuration = DefaultHandleTimeout,
  maxPayloadSize: Long = DefaultMaxPayloadSize,
  defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
  customLogger: OptArg[Logger] = OptArg.Empty,
  cancelOnDisconnect: Boolean = DefaultCancelOnDisconnect,
)(implicit
  scheduler: Scheduler
) extends HttpServlet with LazyLogging {

  import RestServlet.*

  @bincompat private[rest] def this(
    handleRequest: RawRest.HandleRequestWithStreaming,
    handleTimeout: FiniteDuration,
    maxPayloadSize: Long,
    defaultStreamingBatchSize: Int,
    customLogger: OptArg[Logger],
  )(implicit
    scheduler: Scheduler
  ) = this(
    handleRequest,
    handleTimeout,
    maxPayloadSize,
    defaultStreamingBatchSize,
    customLogger,
    cancelOnDisconnect = DefaultCancelOnDisconnect,
  )

  override protected lazy val logger: ScalaLogger =
    ScalaLogger(customLogger.getOrElse(LoggerFactory.getLogger(getClass.getName)))

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val asyncContext = request.startAsync()
    // Set once the response no longer belongs to this servlet, whether completed here or by the container; past
    // that point Jetty may have recycled the request for the next one on the connection (https://stackoverflow.com/a/27744537)
    val completed = new AtomicBoolean(false)
    val cancelable = SingleAssignCancelable()

    def completeWith(code: => Unit): Unit =
      if (!completed.getAndSet(true)) {
        try code
        catch {
          case NonFatal(e) => logger.warn("Failed to write REST response", e)
        } finally {
          try asyncContext.complete()
          catch {
            // the container completed the cycle concurrently, e.g. the client went away mid-write
            case e: IllegalStateException => logger.debug("REST request was already completed by the container", e)
          }
        }
      }

    // Registered before the handler starts: a fast handler could otherwise complete the cycle first, and a
    // disconnect arriving before registration would reach no listener.
    asyncContext.setTimeout(handleTimeout.toMillis)
    asyncContext.addListener(new AsyncListener {
      def onComplete(event: AsyncEvent): Unit = completed.set(true)
      def onTimeout(event: AsyncEvent): Unit = {
        cancelable.cancel()
        completeWith(writeFailure(response, s"server operation timed out after $handleTimeout".opt))
      }
      def onError(event: AsyncEvent): Unit = {
        if (cancelOnDisconnect) cancelable.cancel()
        event.getThrowable match {
          case e: EOFException =>
            // The client is gone, so completing here keeps the container from running its default error dispatch
            // (a 500 through the error handler) for what is not an application failure.
            logger.debug("REST request was aborted by the client", e)
            completeWith(())
          case e =>
            // Any other async failure is left to the container's error dispatch; only stop writing here.
            logger.warn("REST request failed asynchronously", e)
            completed.set(true)
        }
      }
      def onStartAsync(event: AsyncEvent): Unit = ()
    })

    // readRequest must execute in Jetty thread but we want exceptions to be handled uniformly, hence the Try
    val udashRequest = Try(readRequest(request))
    cancelable := (for {
      restRequest <- Task.fromTry(udashRequest)
      restResponse <- handleRequest(restRequest)
      _ <- unlessCompleted(completed)(setResponseHeaders(response, restResponse.code, restResponse.headers))
      _ <- writeResponseBody(response, restResponse, completed)
    } yield ()).executeAsync.runAsync {
      case Right(_) =>
        completeWith(())
      case Left(e: HttpErrorException) =>
        completeWith(writeResponse(response, e.toResponse))
      case Left(e: EOFException) =>
        logger.warn("Request was cancelled by the client during REST response", e)
        completeWith(())
      case Left(e) if completed.get =>
        logger.warn("REST handler failed after the request had already been completed by a timeout or a disconnect", e)
      case Left(e) =>
        logger.error("Failed to handle REST request", e)
        completeWith(writeFailure(response, e.getMessage.opt))
    }
  }

  // A handler may outlive its request, e.g. when uncancelable or blocking, and the response object is then already
  // recycled for the next request on the connection
  private def unlessCompleted(completed: AtomicBoolean)(write: => Unit): Task[Unit] =
    Task(if (!completed.get) write)

  private def setResponseHeaders(response: HttpServletResponse, code: Int, headers: IMapping[PlainValue]): Unit = {
    response.setStatus(code)
    headers.entries.foreach {
      case (name, PlainValue(value)) => response.addHeader(name, value)
    }
  }

  private def writeNonEmptyBody(response: HttpServletResponse, body: HttpBody.NonEmpty): Unit = {
    val bytes = body.bytes
    response.setContentType(body.contentType)
    response.setContentLength(bytes.length)
    response.getOutputStream.write(bytes)
  }

  private def writeNonEmptyStreamedBody(
    response: HttpServletResponse,
    responseBody: StreamedBody.NonEmpty,
    completed: AtomicBoolean,
  ): Task[Unit] = Task.defer {
    // The Content-Length header is intentionally omitted for streams.
    // This signals to the client that the response body size is not predetermined and will be streamed.
    // Clients implementing the streaming part of the REST interface contract MUST be prepared
    // to handle responses without Content-Length by reading data incrementally until the stream completes.
    if (completed.get) Task.unit
    else responseBody match {
      case single: StreamedBody.Single =>
        Task.eval(writeNonEmptyBody(response, single.body))
      case binary: StreamedBody.RawBinary =>
        response.setContentType(binary.contentType)
        binary.content
          .takeWhile(_ => !completed.get)
          .foreachL { chunk =>
            response.getOutputStream.write(chunk)
            response.getOutputStream.flush()
          }
      case jsonList: StreamedBody.JsonList =>
        response.setContentType(jsonList.contentType)
        jsonList.elements
          .bufferTumbling(jsonList.customBatchSize.getOrElse(defaultStreamingBatchSize))
          .switchIfEmpty(Observable(Seq.empty))
          .zipWithIndex
          .takeWhile(_ => !completed.get)
          .foreachL { case (batch, idx) =>
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
          .flatMap(_ => unlessCompleted(completed)(response.getOutputStream.write("]".getBytes(jsonList.charset))))
    }
  }.onErrorHandle {
    case _: EOFException =>
      logger.warn("Request was cancelled by the client during streaming REST response")
    case ex =>
      // When an error occurs during streaming, we immediately close the connection rather than
      // attempting to send an error response. This is intentional because:
      // The client has likely already received and started processing partial data
      // for structured formats (like JSON arrays), the stream is now in an invalid state
      logger.error("Failure during streaming REST response", ex)
      response.getOutputStream.close()
  }

  private def writeResponseBody(
    response: HttpServletResponse,
    restResponse: AbstractRestResponse,
    completed: AtomicBoolean,
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
          case neBody: StreamedBody.NonEmpty => writeNonEmptyStreamedBody(response, neBody, completed)
        }
    }

  private def writeResponse(response: HttpServletResponse, restResponse: RestResponse): Unit = {
    setResponseHeaders(response, restResponse.code, restResponse.headers)
    restResponse.body match {
      case HttpBody.Empty =>
      case neBody: HttpBody.NonEmpty => writeNonEmptyBody(response, neBody)
    }
  }

  private def writeFailure(response: HttpServletResponse, message: Opt[String]): Unit = {
    response.setStatus(500)
    message.foreach { msg =>
      response.setContentType(s"text/plain;charset=utf-8")
      response.getWriter.write(msg)
    }
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
