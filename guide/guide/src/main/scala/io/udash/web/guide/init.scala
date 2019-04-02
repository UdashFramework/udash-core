package io.udash.web.guide

import io.udash._
import io.udash.routing.{UrlLogging, WindowUrlPathChangeProvider}
import io.udash.rpc._
import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.web.guide.demos.rest.MainServerREST
import io.udash.web.guide.rpc.RPCService
import io.udash.web.guide.views.ext.demo.UrlLoggingDemo
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSExport
import scala.util.Try

object Context {
  implicit val executionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
  private lazy val routingRegistry = new RoutingRegistryDef
  private lazy val viewFactoryRegistry = new StatesToViewFactoryDef

  implicit val applicationInstance = new Application[RoutingState](
    routingRegistry, viewFactoryRegistry, WindowUrlPathChangeProvider
  ) with UrlLogging[RoutingState] {
    override protected def log(url: String, referrer: Option[String]): Unit = UrlLoggingDemo.log(url, referrer)
  }
  val serverRpc: MainServerRPC = DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService, exceptionsRegistry = GuideExceptions.registry)

  val restServer: MainServerREST = {
    import io.udash.rest._
    val (scheme, defaultPort) = if (dom.window.location.protocol == "https:") ("https", 443) else ("http", 80)
    val port = Try(dom.window.location.port.toInt).getOrElse(defaultPort)
    DefaultRestClient[MainServerREST](s"$scheme://${dom.window.location.hostname}:$port/rest_api")
  }

  val mainMenuEntries: Seq[MenuEntry] = Seq(
    MenuLink("Intro", IntroState),
    MenuContainer("Bootstrapping", Seq(
      MenuLink("Introduction", BootstrappingIntroState),
      MenuLink("SBT configuration", BootstrappingSBTState),
      MenuLink("Shared RPC", BootstrappingRpcState),
      MenuLink("Backend", BootstrappingBackendState),
      MenuLink("Frontend", BootstrappingFrontendState)
    )),
    MenuContainer("Frontend", Seq(
      MenuLink("Introduction", FrontendIntroState),
      MenuLink("Routing", FrontendRoutingState(None)),
      MenuLink("Model, View, Presenter & ViewFactory", FrontendMVPState),
      MenuLink("Scalatags & UdashCSS", FrontendTemplatesState),
      MenuLink("Properties", FrontendPropertiesState),
      MenuLink("Template Data Binding", FrontendBindingsState),
      MenuLink("Two-way Forms Binding", FrontendFormsState),
      MenuLink("Files upload", FrontendFilesState)
    )),
    MenuContainer("RPC", Seq(
      MenuLink("Introduction", RpcIntroState),
      MenuLink("Interfaces", RpcInterfacesState),
      MenuLink("Client ➔ Server", RpcClientServerState),
      MenuLink("Server ➔ Client", RpcServerClientState),
      MenuLink("Serialization", RpcSerializationState)
    )),
    MenuLink("REST", RestState),
    MenuContainer("Extensions", Seq(
      MenuLink("Internationalization", I18NExtState),
      MenuLink("Authorization", AuthorizationExtState),
      MenuLink("Bootstrap Components", BootstrapExtState),
      MenuLink("Charts", ChartsExtState),
      MenuLink("jQuery wrapper", JQueryExtState),
      MenuLink("User activity", UserActivityExtState)
    )),
    MenuLink("License", LicenseState) /*,
    MenuLink("FAQ", FAQState)*/
  )
}

object Init {

  import Context._

  @JSExport
  def main(args: Array[String]): Unit = {
    jQ((jThis: Element) => {
      val appRoot = jQ("#application").get(0).get
      applicationInstance.run(appRoot)

      // Scroll view to top on state change
      applicationInstance.onStateChange(ev => {
        if (ev.currentState.getClass != ev.oldState.getClass) {
          jQ("html, body").animate(Map[String, Any]("scrollTop" -> 0), 250)
        }
      })
    })
  }
}