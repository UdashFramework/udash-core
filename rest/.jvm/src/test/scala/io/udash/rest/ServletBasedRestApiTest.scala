package io.udash
package rest

import org.eclipse.jetty.ee10.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.Server

import scala.concurrent.duration.*

abstract class ServletBasedRestApiTest extends RestApiTest with UsesHttpServer {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10.seconds)

  def maxPayloadSize: Int = 1024 * 1024
  def serverTimeout: FiniteDuration = 10.seconds

  protected def setupServer(server: Server): Unit = {
    val servlet = new RestServlet(serverHandle, serverTimeout, maxPayloadSize)
    val handler = new ServletContextHandler()
    handler.addServlet(new ServletHolder(servlet), "/api/*")
    server.setHandler(handler)
  }
}
