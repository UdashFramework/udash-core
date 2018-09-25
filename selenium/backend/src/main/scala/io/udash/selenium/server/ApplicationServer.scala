package io.udash.selenium.server


import io.udash.rest.server.{DefaultExposesREST, DefaultRestServlet}
import io.udash.rpc._
import io.udash.rpc.utils.{CallLogging, DefaultAtmosphereFramework}
import io.udash.selenium.demos.activity.CallLogger
import io.udash.selenium.rest.ExposedRestInterfaces
import io.udash.selenium.rpc.demos.activity.Call
import io.udash.selenium.rpc.demos.rest.MainServerREST
import io.udash.selenium.rpc.{ExposedRpcInterfaces, GuideExceptions, MainServerRPC}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}

class ApplicationServer(val port: Int, resourceBase: String) {
  import scala.concurrent.ExecutionContext.Implicits.global

  private val server = new Server(port)

  def start(): Unit = {
    server.start()
  }

  def stop(): Unit = {
    server.stop()
  }

  private val webContext = {
    val ctx = createContextHandler()
    ctx.getSessionHandler.addEventListener(new org.atmosphere.cpr.SessionSupport())
    ctx.addServlet(createStaticHandler(resourceBase), "/*")

    val atmosphereHolder = {
      val config = new DefaultAtmosphereServiceConfig[MainServerRPC](clientId => {
        val callLogger = new CallLogger
        new DefaultExposesServerRPC[MainServerRPC](new ExposedRpcInterfaces(callLogger)(clientId, implicitly)) with CallLogging[MainServerRPC] {
          import localFramework.RPCMetadata
          override protected val metadata: RPCMetadata[MainServerRPC] = MainServerRPC.metadata

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

    val restHolder = new ServletHolder(
      new DefaultRestServlet(new DefaultExposesREST[MainServerREST](new ExposedRestInterfaces)))
    restHolder.setAsyncSupported(true)
    ctx.addServlet(restHolder, "/rest_api/*")
    ctx
  }

  private val rewriteHandler = {
    import org.eclipse.jetty.rewrite.handler.RewriteRegexRule
    val rewrite = new org.eclipse.jetty.rewrite.handler.RewriteHandler()
    rewrite.setRewriteRequestURI(true)
    rewrite.setRewritePathInfo(false)

    val spaRewrite = new RewriteRegexRule
    spaRewrite.setRegex("^/(?!assets|scripts|styles|atm|rest_api)(.*/?)*$")
    spaRewrite.setReplacement("/")
    rewrite.addRule(spaRewrite)
    rewrite.setHandler(webContext)
    rewrite
  }

  server.setHandler(rewriteHandler)

  private def createContextHandler(): ServletContextHandler = {
    val context = new ServletContextHandler
    context.setSessionHandler(new SessionHandler)
    context.setGzipHandler(new GzipHandler)
    context
  }

  private def createStaticHandler(resourceBase: String): ServletHolder = {
    val appHolder = new ServletHolder(new DefaultServlet)
    appHolder.setAsyncSupported(true)
    appHolder.setInitParameter("resourceBase", resourceBase)
    appHolder
  }
}