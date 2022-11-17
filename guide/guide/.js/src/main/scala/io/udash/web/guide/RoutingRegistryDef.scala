package io.udash.web.guide

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash._

import scala.scalajs.js.URIUtils

class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
  def matchUrl(url: Url): RoutingState = {
    val stripped = url.value.stripPrefix("/").stripSuffix("/")
    url2State.applyOrElse("/" + stripped, (_: String) => ErrorState)
  }

  def matchState(state: RoutingState): Url =
    Url(state2Url.apply(state))

  private val (url2State, state2Url) = bidirectional {
    case "/" => IntroState
    case "/bootstrapping" => BootstrappingIntroState
    case "/bootstrapping/sbt" => BootstrappingSbtState
    case "/bootstrapping/advanced" => AdvancedBootstrappingSbtState
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
    case "/ext/jquery" => JQueryExtState
    case "/ext/activity" => UserActivityExtState
    case "/faq" => FaqState
    case "/license" => LicenseState
    case "/bonanza" => PropertiesBonanzaState.Default
    case "/bonanza" / s => PropertiesBonanzaStateEncoder(s)
  }

  private object UrlEncoder {
    def apply(encoded: String): String = URIUtils.decodeURIComponent(encoded)
    def unapply(decoded: String): Option[String] = Some(URIUtils.encodeURIComponent(decoded))
  }

  private abstract class GenericUrlEncoder[T: GenCodec] {
    def apply(encoded: String): T = JsonStringInput.read[T](UrlEncoder(encoded))
    def unapply(decoded: T): Option[String] = UrlEncoder.unapply(JsonStringOutput.write[T](decoded))
  }


  private object PropertiesBonanzaStateEncoder extends GenericUrlEncoder[PropertiesBonanzaState]


}