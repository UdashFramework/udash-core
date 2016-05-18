package io.udash.web.server

import io.udash.web.guide.MainServerRPC
import io.udash.web.guide.rpc.ExposedRpcInterfaces
import io.udash.rpc._
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}

class ApplicationServer(val port: Int, homepageResourceBase: String, guideResourceBase: String) {
  import io.udash.web.Implicits._
  private val server = new Server(port)

  def start() = server.start()

  def stop() = server.stop()

  private val homepage = createContextHandler(Array("udash.io", "www.udash.io", "127.0.0.1"))
  private val guide = createContextHandler(Array("guide.udash.io", "www.guide.udash.io", "127.0.0.2"))

  homepage.addServlet(createStaticHandler(homepageResourceBase), "/*")
  guide.addServlet(createStaticHandler(guideResourceBase), "/*")

  private val atmosphereHolder = {
    val config = new DefaultAtmosphereServiceConfig[MainServerRPC]((clientId) =>
      new DefaultExposesServerRPC[MainServerRPC](new ExposedRpcInterfaces()(clientId))
    )
    val framework = new DefaultAtmosphereFramework(config)

    framework.init()

    val atmosphereHolder = new ServletHolder(new RpcServlet(framework))
    atmosphereHolder.setAsyncSupported(true)
    atmosphereHolder
  }
  guide.addServlet(atmosphereHolder, "/atm/*")

  val contexts = new ContextHandlerCollection
  contexts.setHandlers(Array(homepage, guide))
  server.setHandler(contexts)

  private def createContextHandler(hosts: Array[String]): ServletContextHandler = {
    val context = new ServletContextHandler
    context.setSessionHandler(new SessionHandler)
    context.setGzipHandler(new GzipHandler)
    context.setVirtualHosts(hosts)
    context
  }

  private def createStaticHandler(resourceBase: String): ServletHolder = {
    val appHolder = new ServletHolder(new DefaultServlet)
    appHolder.setAsyncSupported(true)
    appHolder.setInitParameter("resourceBase", resourceBase)
    appHolder
  }
}
