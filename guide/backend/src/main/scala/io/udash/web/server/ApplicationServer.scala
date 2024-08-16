package io.udash.web.server

import com.avsystem.commons.universalOps
import io.udash.rest.*
import io.udash.rpc.*
import io.udash.rpc.utils.CallLogging
import io.udash.web.guide.demos.activity.{Call, CallLogger}
import io.udash.web.guide.demos.rest.MainServerREST
import io.udash.web.guide.rest.ExposedRestInterfaces
import io.udash.web.guide.rpc.ExposedRpcInterfaces
import io.udash.web.guide.{GuideExceptions, MainServerRPC}
import monix.execution.Scheduler
import org.eclipse.jetty.ee10.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer
import org.eclipse.jetty.rewrite.handler.{RewriteHandler, RewriteRegexRule}
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
    val ctx = createContextHandler("udash.io", "www.udash.io", "udash.local", "127.0.0.1")
    ctx.addServlet(createStaticHandler(homepageResourceBase), "/*")
    ctx
  }

  private val guide = {
    val ctx = createContextHandler("guide.udash.io", "www.guide.udash.io", "guide.udash.local", "127.0.0.2", "localhost")
    ctx.getSessionHandler.addEventListener(new org.atmosphere.cpr.SessionSupport())
    ctx.addServlet(createStaticHandler(guideResourceBase), "/*")

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
    ctx.addServlet(atmosphereHolder, "/atm/*")

    //required for org.atmosphere.container.JSR356AsyncSupport
    JakartaWebSocketServletContainerInitializer.configure(ctx, null)

    val restHolder = new ServletHolder(
      RestServlet[MainServerREST](new ExposedRestInterfaces)
    )
    restHolder.setAsyncSupported(true)
    ctx.addServlet(restHolder, "/rest_api/*")
    ctx
  }

  private val contexts = new ContextHandlerCollection
  contexts.setHandlers(homepage, guide)

  private val rewriteHandler =
    new RewriteHandler(contexts).setup(_.addRule(new RewriteRegexRule("^/(?!assets|scripts|styles|atm|rest_api)(.*/?)*$", "/")))

  server.setHandler(rewriteHandler)

  private def createContextHandler(hosts: String*): ServletContextHandler =
    new ServletContextHandler(ServletContextHandler.SESSIONS).setup { context =>
      context.insertHandler(new GzipHandler)
      context.addVirtualHosts(hosts *)
    }

  private def createStaticHandler(resourceBase: String): ServletHolder = {
    val appHolder = new ServletHolder(new DefaultServlet)
    appHolder.setAsyncSupported(true)
    appHolder.setInitParameter("resourceBase", resourceBase)
    appHolder
  }
}