package io.udash.web.guide

import io.udash._

class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
  def matchUrl(url: Url): RoutingState = {
    val stripped = url.value.stripPrefix("/").stripSuffix("/")
    url2State.applyOrElse("/" + stripped, (x: String) => ErrorState)
  }

  def matchState(state: RoutingState): Url =
    Url(state2Url.apply(state))

  private val (url2State, state2Url) = bidirectional {
    case "/" => IntroState
    case "/bootstrapping" => BootstrappingIntroState
    case "/bootstrapping/sbt" => BootstrappingSBTState
    case "/bootstrapping/rpc" => BootstrappingRpcState
    case "/bootstrapping/backend" => BootstrappingBackendState
    case "/bootstrapping/frontend" => BootstrappingFrontendState
    case "/frontend" => FrontendIntroState
    case "/frontend/routing" => FrontendRoutingState(None)
    case "/frontend/routing" / s => FrontendRoutingState(Some(s))
    case "/frontend/mvp" => FrontendMVPState
    case "/frontend/templates" => FrontendTemplatesState
    case "/frontend/properties" => FrontendPropertiesState
    case "/frontend/bindings" => FrontendBindingsState
    case "/frontend/forms" => FrontendFormsState
    case "/frontend/files" => FrontendFilesState
    case "/rpc" => RpcIntroState
    case "/rpc/interfaces" => RpcInterfacesState
    case "/rpc/serialization" => RpcSerializationState
    case "/rpc/client-server" => RpcClientServerState
    case "/rpc/server-client" => RpcServerClientState
    case "/rest" => RestState
    case "/ext/i18n" => I18NExtState
    case "/ext/bootstrap" => BootstrapExtState
    case "/ext/authorization" => AuthorizationExtState
    case "/ext/charts" => ChartsExtState
    case "/ext/jquery" => JQueryExtState
    case "/ext/activity" => UserActivityExtState
    case "/faq" => FaqState
    case "/license" => LicenseState
  }
}