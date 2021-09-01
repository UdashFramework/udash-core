package io.udash.web.guide

import io.udash._
import io.udash.routing.{UrlLogging, WindowUrlPathChangeProvider}
import io.udash.rpc._
import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.web.guide.rpc.RPCService
import io.udash.web.guide.views.ext.demo.UrlLoggingDemo
import io.udash.wrappers.jquery._

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExport

object Context {
  implicit val executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
  private lazy val routingRegistry = new RoutingRegistryDef
  private lazy val viewFactoryRegistry = new StatesToViewFactoryDef

  implicit val applicationInstance: Application[RoutingState] =
    new Application[RoutingState](
      routingRegistry, viewFactoryRegistry, new WindowUrlPathChangeProvider
    ) with UrlLogging[RoutingState] {
      override protected def log(url: String, referrer: Option[String]): Unit = UrlLoggingDemo.log(url, referrer)
    }
  val serverRpc: MainServerRPC = DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService, exceptionsRegistry = GuideExceptions.registry)

  def markdownLink(state: MarkdownState, chapter: String): MenuLink =
    MenuLink(chapter, state, MarkdownState.chapterFragment(chapter))

  val mainMenuEntries: Seq[MenuEntry] = Seq(
    MenuLink("Intro", IntroState),
    MenuContainer("Bootstrapping", Seq(
      MenuLink("Introduction", BootstrappingIntroState),
      MenuLink("sbt configuration", BootstrappingSbtState),
      MenuLink("Advanced sbt configuration", AdvancedBootstrappingSbtState),
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
    MenuContainer("REST", Seq(
      markdownLink(RestState, "Overview"),
      markdownLink(RestState, "Quickstart example"),
      markdownLink(RestState, "REST API traits"),
      markdownLink(RestState, "Serialization"),
      markdownLink(RestState, "API evolution"),
      markdownLink(RestState, "Implementing backends"),
      markdownLink(RestState, "Generating OpenAPI 3.0 specifications"),
    )),
    MenuContainer("Extensions", Seq(
      MenuLink("Internationalization", I18NExtState),
      MenuLink("Authorization", AuthorizationExtState),
      MenuLink("Bootstrap Components", BootstrapExtState),
      MenuLink("jQuery wrapper", JQueryExtState),
      MenuLink("User activity", UserActivityExtState)
    )),
    MenuLink("License", LicenseState),
  )
}

object Init {

  import Context._

  @JSExport
  def main(args: Array[String]): Unit =
    applicationInstance.run("#application", onApplicationStarted = _ => applicationInstance.onStateChange { ev =>
      if (ev.currentState.getClass != ev.oldState.getClass) {
        jQ("html, body").animate(Map[String, Any]("scrollTop" -> 0), 250)
      }
    })
}