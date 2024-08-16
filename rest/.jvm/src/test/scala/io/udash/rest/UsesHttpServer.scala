package io.udash
package rest

import org.eclipse.jetty.http.UriCompliance
import org.eclipse.jetty.server.{AbstractNetworkConnector, HttpConnectionFactory, Server}
import org.scalatest.{BeforeAndAfterAll, Suite}

trait UsesHttpServer extends BeforeAndAfterAll { this: Suite =>
  private val server: Server = new Server(0)
  private val connector = server.getConnectors.head.asInstanceOf[AbstractNetworkConnector]
  protected final def port: Int = connector.getLocalPort
  protected final def baseUrl = s"http://localhost:$port"

  protected def setupServer(server: Server): Unit

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    // Unsafe URI compliance is required for testing purposes
    connector.getConnectionFactory(classOf[HttpConnectionFactory]).getHttpConfiguration.setUriCompliance(UriCompliance.UNSAFE)
    setupServer(server)
    server.start()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }
}
