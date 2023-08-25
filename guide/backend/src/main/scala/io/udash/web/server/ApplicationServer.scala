package io.udash.web.server

import io.udash.rest._
import io.udash.rpc._
import io.udash.rpc.utils.{CallLogging, DefaultAtmosphereFramework}
import io.udash.web.guide.demos.activity.{Call, CallLogger}
import io.udash.web.guide.demos.rest.MainServerREST
import io.udash.web.guide.rest.ExposedRestInterfaces
import io.udash.web.guide.rpc.ExposedRpcInterfaces
import io.udash.web.guide.{GuideExceptions, MainServerRPC}
import monix.execution.Scheduler
import org.eclipse.jetty.ee8.nested.SessionHandler
import org.eclipse.jetty.ee8.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.ee8.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer
import org.eclipse.jetty.rewrite.handler.RewriteHandler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.gzip.GzipHandler

class ApplicationServer(val port: Int, homepageResourceBase: String, guideResourceBase: String)(implicit scheduler: Scheduler) {
  private val server = new Server(port)

  def start(): Unit = {
    server.start()
  }

  def stop(): Unit = {
    server.stop()
  }

  private val homepage = {
    val contextHandler = createContextHandler(Array("udash.io", "www.udash.io", "udash.local", "127.0.0.1"))
    contextHandler.addServlet(createStaticHandler(homepageResourceBase), "/*")
    val context = new GzipHandler()
    context.setHandler(contextHandler)
    context
  }

  private val guide = {
    val contextHandler = createContextHandler(Array("guide.udash.io", "www.guide.udash.io", "guide.udash.local", "127.0.0.2", "localhost"))
    contextHandler.getSessionHandler.addEventListener(new org.atmosphere.cpr.SessionSupport())
    contextHandler.addServlet(createStaticHandler(guideResourceBase), "/*")

    val atmosphereHolder = {
      val config = new DefaultAtmosphereServiceConfig[MainServerRPC](clientId => {
        val callLogger = new CallLogger
        new DefaultExposesServerRPC[MainServerRPC](new ExposedRpcInterfaces(callLogger, guideResourceBase)(clientId)) with CallLogging[MainServerRPC] {
          override protected val metadata: ServerRpcMetadata[MainServerRPC] = MainServerRPC.metadata

          override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
            callLogger.append(Call(rpcName, methodName, args))
        }
      })

      val framework = new DefaultAtmosphereFramework(config, exceptionsRegistry = GuideExceptions.registry)
      val atmosphereHolder = new ServletHolder(new RpcServlet(framework))
      atmosphereHolder.setAsyncSupported(true)
      atmosphereHolder
    }
    contextHandler.addServlet(atmosphereHolder, "/atm/*")

    //this is required for org.atmosphere.container.JSR356AsyncSupport to setup at all
    JavaxWebSocketServletContainerInitializer.configure(contextHandler, null)

    val restHolder = new ServletHolder(
      RestServlet[MainServerREST](new ExposedRestInterfaces)
    )
    restHolder.setAsyncSupported(true)
    contextHandler.addServlet(restHolder, "/rest_api/*")

    val handler = new GzipHandler()
    handler.setHandler(contextHandler)
    handler
  }

  private val contexts = new ContextHandlerCollection
  contexts.setHandlers(homepage, guide)

  private val rewriteHandler = {
    import org.eclipse.jetty.rewrite.handler.RewriteRegexRule
    val rewrite = new RewriteHandler()

    val spaRewrite = new RewriteRegexRule
    spaRewrite.setRegex("^/(?!assets|scripts|styles|atm|rest_api)(.*/?)*$")
    spaRewrite.setReplacement("/")
    rewrite.addRule(spaRewrite)
    rewrite.setHandler(contexts)
    rewrite
  }

  server.setHandler(rewriteHandler)

  private def createContextHandler(hosts: Array[String]): ServletContextHandler = {
    val contextHandler = new ServletContextHandler
    contextHandler.setSessionHandler(new SessionHandler)
    contextHandler.setVirtualHosts(hosts)
    contextHandler
  }

  private def createStaticHandler(resourceBase: String): ServletHolder = {
    val appHolder = new ServletHolder(new DefaultServlet)
    appHolder.setAsyncSupported(true)
    appHolder.setInitParameter("baseResource", resourceBase)
    appHolder
  }
}