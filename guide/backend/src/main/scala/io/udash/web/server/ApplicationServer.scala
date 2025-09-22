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
import org.eclipse.jetty.compression.server.CompressionHandler
import org.eclipse.jetty.ee8.nested.SessionHandler
import org.eclipse.jetty.ee8.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.ee8.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer
import org.eclipse.jetty.rewrite.handler.{RewriteHandler, RewriteRegexRule}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.util.resource.ResourceFactory

import java.nio.file.Path

class ApplicationServer(val port: Int, homepageResourceBase: String, guideResourceBase: String)(implicit scheduler: Scheduler) {
  private val server = new Server(port)

  def start(): Unit =
    server.start()

  def stop(): Unit =
    server.stop()

  private val homepage =
    new CompressionHandler(createContextHandler(
      hosts = Array("udash.io", "www.udash.io", "udash.local", "127.0.0.1"),
      resourceBase = homepageResourceBase
    ).get())

  private val guide = {
    val contextHandler = createContextHandler(
      hosts = Array("guide.udash.io", "www.guide.udash.io", "guide.udash.local", "127.0.0.2", "localhost"),
      resourceBase = guideResourceBase
    )
    contextHandler.getSessionHandler.addEventListener(new org.atmosphere.cpr.SessionSupport())

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
      new ServletHolder(new RpcServlet(framework))
    }
    contextHandler.addServlet(atmosphereHolder, "/atm/*")

    //required for org.atmosphere.container.JSR356AsyncSupport
    JavaxWebSocketServletContainerInitializer.configure(contextHandler, null)

    contextHandler.addServlet(new ServletHolder(RestServlet[MainServerREST](new ExposedRestInterfaces)), "/rest_api/*")

    new CompressionHandler(contextHandler.get())
  }

  server.setHandler(
    new RewriteHandler(new ContextHandlerCollection().setup(_.setHandlers(homepage, guide)))
      .setup(_.addRule(new RewriteRegexRule("^/(?!assets|scripts|styles|atm|rest_api)(.*/?)*$", "/")))
  )

  private def createContextHandler(hosts: Array[String], resourceBase: String): ServletContextHandler = {
    val contextHandler = new ServletContextHandler
    contextHandler.setSessionHandler(new SessionHandler)
    contextHandler.setVirtualHosts(hosts)
    contextHandler.setBaseResource(ResourceFactory.of(contextHandler).newResource(Path.of(resourceBase).toRealPath()))
    contextHandler.addServlet(new ServletHolder(new DefaultServlet), "/*")
    contextHandler
  }

}