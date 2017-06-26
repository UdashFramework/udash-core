package io.udash.web.server

import io.udash.rpc._
import io.udash.rpc.utils.CallLogging
import io.udash.web.guide.demos.activity.{Call, CallLogger}
import io.udash.web.guide.demos.rest.{EchoServerREST, MainServerREST, RestExampleClass, SimpleServerREST}
import io.udash.web.guide.rest.ExposedRestInterfaces
import io.udash.web.guide.rpc.ExposedRpcInterfaces
import io.udash.web.guide.{GuideExceptions, MainServerRPC}
import io.udash.web.styles.CssRenderer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}

import scala.concurrent.Future

class ApplicationServer(val port: Int, homepageResourceBase: String, guideResourceBase: String) {
  import io.udash.web.Implicits._
  private val server = new Server(port)

  def start(): Unit = {
    CssRenderer.renderHomepage(s"${homepageResourceBase.stripSuffix("/")}/styles")
    CssRenderer.renderGuide(s"${guideResourceBase.stripSuffix("/")}/styles")
    server.start()
  }

  def stop(): Unit =
    server.stop()

  private val homepage = createContextHandler(Array("udash.io", "www.udash.io", "127.0.0.1"))
  private val guide = createContextHandler(Array("guide.udash.io", "www.guide.udash.io", "127.0.0.2", "localhost"))
  guide.getSessionHandler.addEventListener(new org.atmosphere.cpr.SessionSupport())

  homepage.addServlet(createStaticHandler(homepageResourceBase), "/*")
  guide.addServlet(createStaticHandler(guideResourceBase), "/*")

  private val atmosphereHolder = {
    val config = new DefaultAtmosphereServiceConfig[MainServerRPC]((clientId) => {
      val callLogger = new CallLogger
      new DefaultExposesServerRPC[MainServerRPC](new ExposedRpcInterfaces(callLogger)(clientId)) with CallLogging[MainServerRPC] {
        override protected val metadata: localFramework.RPCMetadata[MainServerRPC] = localFramework.RPCMetadata[MainServerRPC]

        override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
          callLogger.append(Call(rpcName, methodName, args))
      }
    })

    val framework = new DefaultAtmosphereFramework(config, exceptionsRegistry = GuideExceptions.registry)
    val atmosphereHolder = new ServletHolder(new RpcServlet(framework))
    atmosphereHolder.setAsyncSupported(true)
    atmosphereHolder
  }
  guide.addServlet(atmosphereHolder, "/atm/*")

  private val restHolder = {
    import io.udash.rest.server._
    val restImpl = new ExposedRestInterfaces
    val holder = new ServletHolder(new RestServlet(new DefaultExposesREST[MainServerREST](restImpl)))
    holder.setAsyncSupported(true)
    holder
  }
  guide.addServlet(restHolder, "/rest/*")

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