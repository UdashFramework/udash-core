package io.udash
package rest

import io.udash.rest.raw.HttpErrorException
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

import scala.concurrent.duration._

abstract class ServletRestCallTest extends AbstractRestCallTest with UsesHttpServer {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10.seconds)

  final val MaxPayloadSize = 1024 * 1024

  protected def setupServer(server: Server): Unit = {
    val servlet = new RestServlet(serverHandle, maxPayloadSize = MaxPayloadSize)
    val holder = new ServletHolder(servlet)
    val handler = new ServletHandler
    handler.addServletWithMapping(holder, "/api/*")
    server.setHandler(handler)
  }

  test("too large binary request") {
    val future = proxy.binaryEcho(Array.fill[Byte](MaxPayloadSize + 1)(5))
    val exception = future.failed.futureValue
    assert(exception == HttpErrorException(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
  }
}
