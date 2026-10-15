package io.udash
package rest

import io.udash.rest.raw.{HttpBody, IMapping, RestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.Scheduler
import org.eclipse.jetty.ee8.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.{Server, ServerConnector}

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.util.concurrent.{CountDownLatch, TimeUnit}
import scala.concurrent.duration.*

trait LifecycleApi {
  @timeout(3.seconds) def hang: Task[Unit]
}
object LifecycleApi extends DefaultRestApiCompanion[LifecycleApi]

class RestServletLifecycleTest extends UdashSharedTest {
  private implicit val scheduler: Scheduler = Scheduler.global

  private val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()

  private def request(server: Server, path: String): HttpRequest.Builder = {
    val port = server.getConnectors.head.asInstanceOf[ServerConnector].getLocalPort
    HttpRequest.newBuilder(URI.create(s"http://localhost:$port/$path"))
  }

  "RestServlet" should {
    "keep answering after its server restarts with the same servlet instance" in {
      val servlet = new RestServlet(_ => Task.now(RestResponse(204, IMapping.empty, HttpBody.Empty)))
      val server = new Server(0)
      val handler = new ServletContextHandler()
      handler.addServlet(new ServletHolder(servlet), "/*")
      server.setHandler(handler)
      def ping(): Int = client.send(request(server, "ping").GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode()
      try {
        server.start()
        assert(ping() == 204)
        server.stop()
        server.start()
        assert(ping() == 204)
      } finally server.stop()
    }

    "still time out a call in flight when destroyed" in {
      val started = new CountDownLatch(1)
      val servlet = RestServlet[LifecycleApi](new LifecycleApi {
        def hang: Task[Unit] = Task(started.countDown()) >> Task.never
      })
      val server = new Server(0)
      val handler = new ServletContextHandler()
      handler.addServlet(new ServletHolder(servlet), "/*")
      server.setHandler(handler)
      try {
        server.start()
        val answer = client.sendAsync(request(server, "hang").POST(HttpRequest.BodyPublishers.noBody()).build(),
          HttpResponse.BodyHandlers.discarding())
        assert(started.await(2, TimeUnit.SECONDS), "the call never started")
        servlet.destroy()
        assert(answer.get(8, TimeUnit.SECONDS).statusCode() == 500)
      } finally server.stop()
    }
  }
}
