package io.udash
package rest

import com.softwaremill.sttp.SttpBackend
import io.udash.rest.raw.RawRest.HandleRequest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

import scala.concurrent.Future
import scala.concurrent.duration._

class HttpRestCallTest extends AbstractRestCallTest with UsesHttpServer {
  override def patienceConfig: PatienceConfig = PatienceConfig(10.seconds)

  implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()

  protected def setupServer(server: Server): Unit = {
    val servlet = new RestServlet(serverHandle)
    val holder = new ServletHolder(servlet)
    val handler = new ServletHandler
    handler.addServletWithMapping(holder, "/api/*")
    server.setHandler(handler)
  }

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest(s"$baseUrl/api")
}
