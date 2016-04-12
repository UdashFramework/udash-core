package io.udash.guide

import io.udash._
import io.udash.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.guide.rpc.RPCService
import io.udash.rpc._
import io.udash.wrappers.jquery._
import org.scalajs.dom.{Element, document}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object Context {
  implicit val executionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
  private lazy val routingRegistry = new RoutingRegistryDef
  private lazy val viewPresenterRegistry = new StatesToViewPresenterDef

  implicit val applicationInstance = new Application[RoutingState](routingRegistry, viewPresenterRegistry, RootState)
  val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService)

  val mainMenuEntries: Seq[MenuEntry] = Seq(
    MenuLink("Intro", IntroState),
    MenuLink("Udash generator", BootstrappingGeneratorsState),
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
      MenuLink("Model, View, Presenter & ViewPresenter", FrontendMVPState),
      MenuLink("Scalatags & ScalaCSS", FrontendTemplatesState),
      MenuLink("Properties", FrontendPropertiesState),
      MenuLink("Template Data Binding", FrontendBindingsState),
      MenuLink("Two-way Forms Binding", FrontendFormsState)
    )),
    MenuContainer("RPC", Seq(
      MenuLink("Introduction", RpcIntroState),
      MenuLink("Interfaces", RpcInterfacesState),
      MenuLink("Client ➔ Server", RpcClientServerState),
      MenuLink("Server ➔ Client", RpcServerClientState)
    ))/*,
    MenuLink("FAQ", FAQState)*/
  )
}

object Init extends JSApp {
  import Context._

  @JSExport
  override def main(): Unit = {
    jQ(document).ready((jThis: Element) => {
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