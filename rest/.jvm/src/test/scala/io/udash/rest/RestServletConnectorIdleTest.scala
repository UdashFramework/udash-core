package io.udash
package rest

import io.udash.rest.raw.{HttpBody, IMapping, RawRest, RestResponse, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.eclipse.jetty.ee8.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.{HttpConnectionFactory, Server, ServerConnector}
import org.slf4j.event.SubstituteLoggingEvent
import org.slf4j.helpers.SubstituteLogger

import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.{Socket, URI}
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.{ConcurrentLinkedQueue, CountDownLatch, TimeUnit}
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

/**
 * Pins what the servlet's own timeouts rely on: Jetty does not expire a request that is suspended without pending
 * I/O when the connector's idle timeout elapses, so a call may run past it up to the call's own timeout. A write that
 * its client stops reading does fail at that idle timeout, which is the only bound on it.
 */
class RestServletConnectorIdleTest extends UdashSharedTest with UsesHttpServer {

  private val ConnectorIdleTimeout = 1.second

  private implicit val scheduler: Scheduler = Scheduler.global

  private def chunk: Array[Byte] = Array.fill[Byte](64)('x')

  private val unreadStreamEnded = new CountDownLatch(1)

  private val logEvents = new ConcurrentLinkedQueue[SubstituteLoggingEvent]
  private val servletLogger = new SubstituteLogger(classOf[RestServlet].getName, logEvents, false)

  private val handleRequest: RawRest.HandleRequestWithStreaming = request =>
    request.parameters.path.map(_.value) match {
      case List("ping") =>
        Task.now(RestResponse(204, IMapping.empty, HttpBody.Empty))
      case List("sleep") =>
        Task.sleep(3.seconds).as(RestResponse(204, IMapping.empty, HttpBody.Empty))
      case List("unread-stream") =>
        val content = Observable.repeatEval(new Array[Byte](64 * 1024)).guarantee(Task(unreadStreamEnded.countDown()))
        Task.now(StreamedRestResponse(200, IMapping.empty, StreamedBody.RawBinary(content, "application/octet-stream")))
      case _ =>
        val content = Observable(chunk) ++ Observable.evalDelayed(3.seconds, chunk)
        Task.now(StreamedRestResponse(200, IMapping.empty, StreamedBody.RawBinary(content, "application/octet-stream")))
    }

  protected def setupServer(server: Server): Unit = {
    val connector = server.getConnectors.head.asInstanceOf[ServerConnector]
    connector.setIdleTimeout(ConnectorIdleTimeout.toMillis)
    val httpConfig = connector.getConnectionFactory(classOf[HttpConnectionFactory]).getHttpConfiguration
    connector.addConnectionFactory(new HTTP2CServerConnectionFactory(httpConfig))

    val handler = new ServletContextHandler()
    handler.addServlet(new ServletHolder(new RestServlet(handleRequest, 10.seconds, customLogger = servletLogger)), "/api/*")
    server.setHandler(handler)
  }

  private lazy val clients = Map(
    "h1" -> HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build(),
    "h2" -> HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build(),
  )

  private def get[T](protocol: String, path: String, body: HttpResponse.BodyHandler[T]): HttpResponse[T] = {
    val client = clients(protocol)
    def request(path: String) = HttpRequest.newBuilder(URI.create(s"$baseUrl/api/$path")).GET().build()
    logEvents.clear()
    // a cleartext HTTP/2 connection starts as an HTTP/1.1 upgrade, so the request itself must find it upgraded
    if (protocol == "h2") client.send(request("ping"), HttpResponse.BodyHandlers.discarding())
    // a failure carries the servlet's log, which tells a reset by the container from a failed write
    val response =
      try client.sendAsync(request(path), body).get(8, TimeUnit.SECONDS)
      catch {
        case NonFatal(e) =>
          val log = logEvents.asScala.map(event => s"${event.getLevel} ${event.getMessage}: ${event.getThrowable}")
          throw new AssertionError(s"$path failed: $e; servlet log: ${log.mkString("[", ", ", "]")}", e)
      }
    assert(response.version() == (if (protocol == "h2") HttpClient.Version.HTTP_2 else HttpClient.Version.HTTP_1_1))
    response
  }

  "RestServlet" should {
    for (protocol <- List("h1", "h2")) {
      s"keep a suspended call past the connector's idle timeout over $protocol" in {
        assert(get(protocol, "sleep", HttpResponse.BodyHandlers.discarding()).statusCode() == 204)
      }

      s"keep a stream open across a gap longer than the connector's idle timeout over $protocol" in {
        val response = get(protocol, "gap-stream", HttpResponse.BodyHandlers.ofByteArray())
        assert(response.statusCode() == 200)
        assert(response.body().length == 2 * 64)
      }
    }

    "end a stream whose client stops reading at the connector's idle timeout" in {
      val socket = new Socket("localhost", port)
      try {
        socket.getOutputStream.write("GET /api/unread-stream HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes(UTF_8))
        socket.getOutputStream.flush()
        assert(unreadStreamEnded.await(5, TimeUnit.SECONDS), "the stalled write never failed")
      } finally socket.close()
    }
  }
}
