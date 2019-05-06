package io.udash
package rest

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

import scala.concurrent.duration._

abstract class ServletBasedRestApiTest extends RestApiTest with UsesHttpServer {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10.seconds)

  def maxPayloadSize: Int = 1024 * 1024
  def serverTimeout: FiniteDuration = 10.seconds

  protected def setupServer(server: Server): Unit = {
    val servlet = new RestServlet(serverHandle, serverTimeout, maxPayloadSize)
    val holder = new ServletHolder(servlet)
    val handler = new ServletHandler
    handler.addServletWithMapping(holder, "/api/*")
    server.setHandler(handler)
  }
}
