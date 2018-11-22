package io.udash.selenium.server


import io.udash.rest.RestServlet
import io.udash.rpc._
import io.udash.rpc.utils.TimeoutConfig
import io.udash.selenium.demos.activity.CallLogger
import io.udash.selenium.rest.ExposedRestInterfaces
import io.udash.selenium.rpc.demos.activity.Call
import io.udash.selenium.rpc.demos.rest.MainServerREST
import io.udash.selenium.rpc.{ExposedRpcInterfaces, GuideExceptions, MainClientRPC, MainServerRPC}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer

import scala.concurrent.duration.DurationInt

class ApplicationServer(val port: Int, resourceBase: String) {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val server = new Server(port)

  def start(): Unit = {
    server.start()
  }

  def stop(): Unit = {
    server.stop()
  }

  private val webContext = createContextHandler()
  webContext.addServlet(createStaticHandler(resourceBase), "/*")

  private val rewriteHandler = {
    import org.eclipse.jetty.rewrite.handler.RewriteRegexRule
    val rewrite = new org.eclipse.jetty.rewrite.handler.RewriteHandler()
    rewrite.setRewriteRequestURI(true)
    rewrite.setRewritePathInfo(false)

    val spaRewrite = new RewriteRegexRule
    spaRewrite.setRegex("^/(?!assets|scripts|styles|websocket|rest_api)(.*/?)*$")
    spaRewrite.setReplacement("/")
    rewrite.addRule(spaRewrite)
    rewrite.setHandler(webContext)
    rewrite
  }

  server.setHandler(rewriteHandler)

  private val rpcServer: DefaultRpcServer[MainServerRPC, MainClientRPC] = {
    val callLogger = new CallLogger
    new DefaultRpcServer[MainServerRPC, MainClientRPC](
      (server, clientId) => new ExposedRpcInterfaces(server, callLogger)(clientId, implicitly),
      GuideExceptions.registry, TimeoutConfig.Default, 30 seconds,
      log => callLogger.append(Call(log.rpcName, log.methodName, log.args))
    )
  }

  private val wscontainer: ServerContainer = WebSocketServerContainerInitializer.configureContext(webContext)
  wscontainer.addEndpoint(rpcServer.endpointConfig("/websocket"))

  private val restHolder = new ServletHolder(RestServlet[MainServerREST](new ExposedRestInterfaces))
  restHolder.setAsyncSupported(true)
  webContext.addServlet(restHolder, "/rest_api/*")

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