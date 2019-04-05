package io.udash
package rest

import com.softwaremill.sttp.SttpBackend
import io.udash.rest.raw.HttpErrorException
import io.udash.rest.raw.RawRest.HandleRequest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

import scala.concurrent.Future
import scala.concurrent.duration._

class HttpRestCallTest extends AbstractRestCallTest with UsesHttpServer {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10.seconds)

  implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()

  final val MaxPayloadSize = 1024 * 1024

  protected def setupServer(server: Server): Unit = {
    val servlet = new RestServlet(serverHandle, maxPayloadSize = MaxPayloadSize)
    val holder = new ServletHolder(servlet)
    val handler = new ServletHandler
    handler.addServletWithMapping(holder, "/api/*")
    server.setHandler(handler)
  }

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest(s"$baseUrl/api")

  test("too large binary request") {
    val future = proxy.binaryEcho(Array.fill[Byte](MaxPayloadSize + 1)(5))
    val exception = future.failed.futureValue
    assert(exception == HttpErrorException(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
  }
}
