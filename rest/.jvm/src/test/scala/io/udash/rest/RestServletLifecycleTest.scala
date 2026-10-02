package io.udash
package rest

import io.udash.rest.raw.{HttpBody, IMapping, RestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.Scheduler
import org.eclipse.jetty.ee8.servlet.{FilterHolder, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.{Server, ServerConnector}

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import javax.servlet.http.{HttpServletResponse, HttpServletResponseWrapper}
import javax.servlet.{DispatcherType, Filter, FilterChain, ServletRequest, ServletResponse}
import scala.concurrent.duration.*

trait LifecycleApi {
  @timeout(200.millis) def quickHang: Task[Unit]
  @timeout(1.minute) def slowHang: Task[Unit]
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

    "drop its pending timeouts and end their thread when destroyed" in {
      val servlet = RestServlet[LifecycleApi](new LifecycleApi {
        def quickHang: Task[Unit] = Task.never
        def slowHang: Task[Unit] = Task.never
      })
      @volatile var timerThread: Thread = null
      // records the thread that answers with the timeout's 500
      val timerRecorder = new FilterHolder(new Filter {
        def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit =
          chain.doFilter(request, new HttpServletResponseWrapper(response.asInstanceOf[HttpServletResponse]) {
            override def setStatus(sc: Int): Unit = {
              if (sc == 500) timerThread = Thread.currentThread()
              super.setStatus(sc)
            }
          })
      })
      timerRecorder.setAsyncSupported(true)
      val server = new Server(0)
      val handler = new ServletContextHandler()
      handler.addFilter(timerRecorder, "/*", java.util.EnumSet.of(DispatcherType.REQUEST))
      handler.addServlet(new ServletHolder(servlet), "/*")
      server.setHandler(handler)
      def post(path: String): HttpRequest = request(server, path).POST(HttpRequest.BodyPublishers.noBody()).build()
      try {
        server.start()
        client.sendAsync(post("slowHang"), HttpResponse.BodyHandlers.discarding())
        val deadline = 4.seconds.fromNow
        while (servlet.pendingTimeouts == 0 && deadline.hasTimeLeft()) Thread.sleep(10)
        assert(servlet.pendingTimeouts == 1)
        assert(client.send(post("quickHang"), HttpResponse.BodyHandlers.discarding()).statusCode() == 500)
        assert(timerThread.getName == "udash-rest-servlet-timer")
      } finally server.stop()
      assert(servlet.pendingTimeouts == 0)
      timerThread.join(3000)
      assert(!timerThread.isAlive, "the timer thread outlived the servlet")
    }
  }
}
