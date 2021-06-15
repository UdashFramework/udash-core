package io.udash
package rest

import org.eclipse.jetty.server.{AbstractNetworkConnector, Server}
import org.scalatest.{BeforeAndAfterAll, Suite}

trait UsesHttpServer extends BeforeAndAfterAll { this: Suite =>
  private val server: Server = new Server(0)
  protected final def port: Int = server.getConnectors.head.asInstanceOf[AbstractNetworkConnector].getLocalPort
  protected final def baseUrl = s"http://localhost:$port"

  protected def setupServer(server: Server): Unit

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    setupServer(server)
    server.start()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }
}
