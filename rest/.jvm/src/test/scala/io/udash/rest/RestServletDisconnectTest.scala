package io.udash
package rest

import io.udash.rest.raw.{AbstractRestResponse, HttpBody, IMapping, RawRest, RestResponse, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.{ExecutionModel, Scheduler, UncaughtExceptionReporter}
import monix.reactive.Observable
import org.eclipse.jetty.ee8.nested.{ErrorHandler, Request as JettyRequest}
import org.eclipse.jetty.ee8.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.{HttpConnectionFactory, Server, ServerConnector}
import org.slf4j.event.{Level, SubstituteLoggingEvent}
import org.slf4j.helpers.SubstituteLogger

import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.{Socket, URI}
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue, CountDownLatch, TimeUnit}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/**
 * Covers cancellation of the handler `Task` when the client abandons a request.
 *
 * The server speaks both HTTP/1.1 and cleartext HTTP/2, because disconnect detection differs between them:
 * an HTTP/2 abort arrives as `RST_STREAM` on a connection the server is already reading and surfaces as
 * `AsyncListener.onError`, while an aborted HTTP/1.1 request goes unnoticed until the async cycle times out.
 */
class RestServletDisconnectTest extends UdashSharedTest with UsesHttpServer {

  private val HandleTimeout = 8.seconds // long enough to tell a disconnect apart from a timeout
  private val ShortHandleTimeout = 1.second

  private val started = new ConcurrentHashMap[String, CountDownLatch]
  private val cancelled = new ConcurrentHashMap[String, CountDownLatch]
  private val finished = new ConcurrentHashMap[String, CountDownLatch]
  private val uncaughtErrors = new ConcurrentLinkedQueue[Throwable]
  private val logEvents = new ConcurrentLinkedQueue[SubstituteLoggingEvent]
  private val errorDispatches = new ConcurrentLinkedQueue[String]

  private implicit val scheduler: Scheduler = Scheduler(
    Scheduler.global,
    UncaughtExceptionReporter(t => { uncaughtErrors.add(t); () }),
    ExecutionModel.Default,
  )

  private def latch(latches: ConcurrentHashMap[String, CountDownLatch], scenario: String): CountDownLatch =
    latches.computeIfAbsent(scenario, _ => new CountDownLatch(1))

  // The path is "<behaviour>/<scenario>": "ping" answers immediately, "finishing" answers with a non-empty body
  // a second later, "uncancelable" does the same but ignores cancellation, "stream" streams chunks until stopped
  // and anything else hangs until cancelled. Each scenario reports its progress through latches.
  private val handleRequest: RawRest.HandleRequestWithStreaming = request => {
    val path = request.parameters.path.map(_.value)
    val scenario = path.last
    def report(latches: ConcurrentHashMap[String, CountDownLatch]): Task[Unit] =
      Task(latch(latches, scenario).countDown())
    def lateResponse: Task[AbstractRestResponse] =
      Task.sleep(1.second)
        .map(_ => RestResponse(200, IMapping.empty, HttpBody.plain("finished after the client left")))
        .doOnFinish(_ => report(finished))
        .doOnCancel(report(cancelled))
    path.headOption match {
      case Some("ping") =>
        report(started).map(_ => RestResponse(204, IMapping.empty, HttpBody.Empty))
      case Some("finishing") =>
        report(started).flatMap(_ => lateResponse)
      case Some("uncancelable") =>
        report(started).flatMap(_ => lateResponse.uncancelable)
      case Some("stream") =>
        // "started" fires with the first chunk written, so the abort lands mid-stream
        val chunks = Observable.intervalAtFixedRate(20.millis)
          .map(_ => Array.fill[Byte](64)('x'))
          .doOnStart(_ => report(started))
          .guaranteeCase(_ => report(finished))
        Task.now(StreamedRestResponse(200, IMapping.empty, StreamedBody.RawBinary(chunks, "application/octet-stream")))
      case _ =>
        report(started).flatMap(_ => Task.never[AbstractRestResponse].doOnCancel(report(cancelled)))
    }
  }

  protected def setupServer(server: Server): Unit = {
    val connector = server.getConnectors.head.asInstanceOf[ServerConnector]
    val httpConfig = connector.getConnectionFactory(classOf[HttpConnectionFactory]).getHttpConfiguration
    connector.addConnectionFactory(new HTTP2CServerConnectionFactory(httpConfig))

    val servletLogger = new SubstituteLogger(classOf[RestServlet].getName, logEvents, false)
    def servlet(handleTimeout: FiniteDuration, cancelOnDisconnect: Boolean): ServletHolder =
      new ServletHolder(new RestServlet(handleRequest, handleTimeout, customLogger = servletLogger, cancelOnDisconnect = cancelOnDisconnect))

    val handler = new ServletContextHandler()
    handler.setErrorHandler(new ErrorHandler {
      override def handle(target: String, baseRequest: JettyRequest, request: HttpServletRequest, response: HttpServletResponse): Unit = {
        errorDispatches.add(s"${request.getDispatcherType} $target -> ${response.getStatus}")
        super.handle(target, baseRequest, request, response)
      }
    })
    handler.addServlet(servlet(HandleTimeout, cancelOnDisconnect = true), "/api/*")
    handler.addServlet(servlet(HandleTimeout, cancelOnDisconnect = false), "/no-cancel-api/*")
    handler.addServlet(servlet(ShortHandleTimeout, cancelOnDisconnect = true), "/short-timeout-api/*")
    server.setHandler(handler)
  }

  private def awaitStarted(path: String): Unit =
    assert(latch(started, path.split('/').last).await(4, TimeUnit.SECONDS), s"handler for $path never started")

  private lazy val http2Client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

  private def http2Request(path: String): HttpRequest =
    HttpRequest.newBuilder(URI.create(s"$baseUrl/$path")).GET().build()

  /** Sends a request over HTTP/2, waits for the handler to start, then aborts it - resetting the stream. */
  private def abortHttp2Request(path: String): Unit = {
    // A cleartext HTTP/2 connection starts as an HTTP/1.1 upgrade; aborting before the upgrade completes would
    // close that connection instead of resetting a stream, so make sure the pooled connection already speaks h2.
    val ping = http2Client.send(http2Request("api/ping/warmup"), HttpResponse.BodyHandlers.discarding())
    assert(ping.version() == HttpClient.Version.HTTP_2, s"expected an HTTP/2 connection, got ${ping.version()}")

    val response = http2Client.sendAsync(http2Request(path), HttpResponse.BodyHandlers.discarding())
    awaitStarted(path)
    assert(response.cancel(true), "request already completed, nothing was aborted")
  }

  /** Sends a request over HTTP/1.1, waits for the handler to start, then closes the socket abruptly (TCP RST). */
  private def abortHttp1Request(path: String): Unit = {
    val socket = new Socket("localhost", port)
    socket.setSoLinger(true, 0)
    try {
      val out = socket.getOutputStream
      out.write(s"GET /$path HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes("utf-8"))
      out.flush()
      awaitStarted(path)
    } finally socket.close()
  }

  private def assertNoFailures(minLevel: Level = Level.WARN): Unit = {
    val errors = uncaughtErrors.asScala.toList
    assert(errors.isEmpty, errors.map(e => s"${e.getClass.getName}: ${e.getMessage}").mkString(", "))
    val problems = logEvents.asScala.filter(_.getLevel.toInt >= minLevel.toInt).toList
    assert(problems.isEmpty, problems.map(e => s"${e.getLevel} ${e.getMessage}: ${e.getThrowable}").mkString(", "))
  }

  private def resetFailures(): Unit = {
    uncaughtErrors.clear()
    logEvents.clear()
  }

  // Lets the servlet's own completion, which follows the handler's, run before inspecting side effects
  private def settle(): Unit = Thread.sleep(500)

  "RestServlet" should {
    "cancel the handler task when an HTTP/2 client aborts the request" in {
      abortHttp2Request("api/hanging/h2-abort")
      assert(latch(cancelled, "h2-abort").await(4, TimeUnit.SECONDS))
    }

    "let the handler task finish when cancelOnDisconnect is disabled" in {
      resetFailures()
      abortHttp2Request("no-cancel-api/finishing/h2-abort-disabled")
      assert(latch(finished, "h2-abort-disabled").await(4, TimeUnit.SECONDS), "handler never finished")
      assert(!latch(cancelled, "h2-abort-disabled").await(0, TimeUnit.SECONDS), "handler was cancelled")
      settle()
      assertNoFailures()
    }

    "complete the aborted request itself instead of leaving it to the container's error dispatch" in {
      errorDispatches.clear()
      abortHttp2Request("api/hanging/h2-abort-no-error-page")
      assert(latch(cancelled, "h2-abort-no-error-page").await(4, TimeUnit.SECONDS))
      settle()
      assert(errorDispatches.isEmpty, errorDispatches.asScala.mkString(", "))
    }

    "not write into or complete the recycled request when an uncancelable handler finishes after the abort" in {
      resetFailures()
      abortHttp2Request("api/uncancelable/late-response")
      assert(latch(finished, "late-response").await(4, TimeUnit.SECONDS), "handler never finished")
      settle()
      assertNoFailures()
    }

    "stop a streamed response when the client aborts" in {
      resetFailures()
      abortHttp2Request("api/stream/h2-abort-stream")
      assert(latch(finished, "h2-abort-stream").await(4, TimeUnit.SECONDS), "stream was never stopped")
      settle()
      // a chunk write racing the abort is reported at warn, which is expected
      assertNoFailures(minLevel = Level.ERROR)
    }

    "stop a streamed response when the client aborts and cancelOnDisconnect is disabled" in {
      resetFailures()
      abortHttp2Request("no-cancel-api/stream/h2-abort-stream-disabled")
      assert(latch(finished, "h2-abort-stream-disabled").await(4, TimeUnit.SECONDS), "stream was never stopped")
      settle()
      assertNoFailures(minLevel = Level.ERROR)
    }

    // Jetty does not read an HTTP/1.1 connection while the async cycle is idle, so the abort itself goes
    // unnoticed and the handler is only bounded by handleTimeout.
    "cancel the handler of an aborted HTTP/1.1 request once handleTimeout elapses" in {
      abortHttp1Request("short-timeout-api/hanging/h1-abort")
      assert(latch(cancelled, "h1-abort").await(4, TimeUnit.SECONDS))
    }
  }
}
