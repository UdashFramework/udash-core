package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.{Context, _}
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object BootstrappingBackendViewPresenter extends DefaultViewPresenterFactory[BootstrappingBackendState.type](() => new BootstrappingBackendView)

class BootstrappingBackendView extends FinalView {
  import Context._
  import io.udash.web.guide.views.References._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Bootstrapping application backend"),
    p("This chapter covers:"),
    ul(GuideStyles.get.defaultList)(
      li("Implementation of the server-side RPC endpoint"),
      li("Configuration of the ", a(href := JettyHomepage)("Jetty"), " webserver to handle RPC and static files requests"),
      li("Implementation of a simple system launcher")
    ),
    p("The backend application is expected to serve static files like HTML, JS or images and handle RPC requests from client applications."),
    p(
      "This guide uses ", a(href := JettyHomepage)("Jetty"), " as the webserver, but of course Udash does not depend on any webserver, " +
      "so you can use any implementation you like."
    ),
    h3("Server RPC implementation"),
    p("First of all, the server-side should implement ", i("MainServerRPC"), ""),
    CodeBlock(
      """class ExposedRpcInterfaces(implicit clientId: ClientId) extends MainServerRPC {
        |  // Call pong with provided id on all connected clients
        |  override def ping(id: Int): Unit =
        |    ClientRPC(AllClients).pong(id)
        |
        |  override def hello(name: String): Future[String] =
        |    Future.successful("Hello, ${name}!")
        |}""".stripMargin)(GuideStyles),
    p("To make usage of client RPC more friendly, it is recommended to create a wrapper object like the one below:"),
    CodeBlock(
      """object ClientRPC {
        |  def apply(target: ClientRPCTarget)
        |           (implicit ec: ExecutionContext): MainClientRPC =
        |    new DefaultClientRPC[MainClientRPC](target).get
        |}""".stripMargin)(GuideStyles),
    h3("Application server"),
    p(
      "Application server creates Jetty server and configures content holders. ", i("resourceBase"), " is the directory containing ",
      i("index.html"), " and ", i("port"), " is the port for Jetty server to bind to."
    ),
    CodeBlock(
      """class ApplicationServer(val port: Int, resourceBase: String) {
        |  private val server = new Server(port)
        |  private val contextHandler = new ServletContextHandler
        |  private val appHolder = createAppHolder()
        |  private val atmosphereHolder = createAtmosphereHolder()
        |
        |  contextHandler.setSessionHandler(new SessionHandler)
        |  contextHandler.addServlet(atmosphereHolder, "/atm/*")
        |  contextHandler.addServlet(appHolder, "/*")
        |
        |  server.setHandler(contextHandler)
        |
        |  def start() = server.start()
        |
        |  def stop() = server.stop()
        |
        |  private def createAtmosphereHolder() = {
        |    val config = new DefaultAtmosphereServiceConfig((clientId) =>
        |      new DefaultExposesServerRPC[MainServerRPC](
        |        new ExposedRpcInterfaces()(clientId)
        |      )
        |    )
        |    val framework = new DefaultAtmosphereFramework(config)
        |
        |    framework.init()
        |
        |    val atmosphereHolder = new ServletHolder(new RpcServlet(framework))
        |    atmosphereHolder.setAsyncSupported(true)
        |    atmosphereHolder
        |  }
        |
        |  private def createAppHolder() = {
        |    val appHolder = new ServletHolder(new DefaultServlet)
        |    appHolder.setAsyncSupported(true)
        |    appHolder.setInitParameter("resourceBase", resourceBase)
        |    appHolder
        |  }
        |}""".stripMargin)(GuideStyles),
    p(
      i("AtmosphereServiceConfig"), " is used to manage ", i("ExposedRpcInterfaces"), " instances and link them to ",
        "server connections. At this point you can inject something like a user context into a service layer. In this example ",
        i("DefaultAtmosphereServiceConfig"), " was used. RPC implementation is cached in ", i("resource"),
        " and it provides ClientId to ", i("ExposedRpcInterfaces"), ""
    ),
    h3("Application launcher"),
    p(
      "Below you can find a simple application launcher. It just creates ", i("ApplicationServer"), " with hardcoded " +
      "parameters and starts it. You can also use an IoC container to inject required parameters from a configuration file."
    ),
    CodeBlock(
      """object Launcher {
        |  def main(args: Array[String]): Unit = {
        |    val startTime = System.nanoTime
        |
        |    val server = new ApplicationServer(8080, "backend/target/UdashStatic/WebContent")
        |    server.start()
        |
        |    import scala.concurrent.duration._
        |    val duration: Long = (System.nanoTime - startTime).nanos.toUnit(SECONDS)
        |    logger.info(s"Application started in ${duration}s.")
        |  }
        |}""".stripMargin)(GuideStyles),
    h2("What's next?"),
    p(
      "Now that the server-side of the application is ready, it is time to implement the ",
      a(href := BootstrappingFrontendState.url)("client-side"), " of the application."
    )
  )
}