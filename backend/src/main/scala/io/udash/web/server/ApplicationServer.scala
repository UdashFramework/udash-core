package io.udash.web.server

import com.avsystem.commons.rpc.RPCMetadata
import io.udash.web.guide.MainServerRPC
import io.udash.web.guide.rpc.ExposedRpcInterfaces
import io.udash.rpc._
import io.udash.rpc.utils.CallLogging
import io.udash.web.guide.demos.activity.{Call, CallLogger}
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
    val config = new DefaultAtmosphereServiceConfig[MainServerRPC]((clientId) => {
      val callLogger = new CallLogger
      new DefaultExposesServerRPC[MainServerRPC](new ExposedRpcInterfaces(callLogger)(clientId)) with CallLogging[MainServerRPC] {
        override protected val metadata: RPCMetadata[MainServerRPC] = RPCMetadata[MainServerRPC]

        override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
          callLogger.append(Call(rpcName, methodName, args))
      }
    })
    val framework = new DefaultAtmosphereFramework(config)

    framework.init()

    val atmosphereHolder = new ServletHolder(new RpcServlet(framework))
    atmosphereHolder.setAsyncSupported(true)
    atmosphereHolder
  }
  guide.addServlet(atmosphereHolder, "/atm/*")

  private val restApiHolder = {
    import spray.servlet.Servlet30ConnectorServlet
    import spray.servlet.Initializer

    guide.addEventListener(new Initializer())
    val apiHolder = new ServletHolder(new Servlet30ConnectorServlet)
    apiHolder
  }
  guide.addServlet(restApiHolder, s"/${ApplicationServer.restPrefix}/*")

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

object ApplicationServer {
  val restPrefix = "rest"
}
