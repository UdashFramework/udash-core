package io.udash.web.guide

import io.udash._
import io.udash.web.guide.views._
import io.udash.web.guide.views.bootstrapping.{BootstrappingFrontendViewFactory, _}
import io.udash.web.guide.views.ext._
import io.udash.web.guide.views.frontend.{FrontendFormsViewFactory, FrontendPropertiesViewFactory, FrontendRoutingViewFactory, FrontendTemplatesViewFactory, _}
import io.udash.web.guide.views.rest._
import io.udash.web.guide.views.rpc.{RpcIntroViewFactory, RpcServerClientViewFactory, _}

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] =
    state match {
      case RootState => RootViewFactory
      case ContentState => ContentViewFactory

      case IntroState => IntroViewFactory

      case BootstrappingState => BootstrappingViewFactory
      case BootstrappingIntroState => BootstrappingIntroViewFactory
      case BootstrappingSBTState => BootstrappingSBTViewFactory
      case BootstrappingRpcState => BootstrappingRpcViewFactory
      case BootstrappingFrontendState => BootstrappingFrontendViewFactory
      case BootstrappingBackendState => BootstrappingBackendViewFactory
      case BootstrappingGeneratorsState => BootstrappingGeneratorsViewFactory

      case FrontendState => FrontendViewFactory
      case FrontendIntroState => FrontendIntroViewFactory
      case FrontendRoutingState(s) => FrontendRoutingViewFactory
      case FrontendMVPState => FrontendMVPViewFactory
      case FrontendTemplatesState => FrontendTemplatesViewFactory
      case FrontendPropertiesState => FrontendPropertiesViewFactory
      case FrontendBindingsState => FrontendBindingsViewFactory
      case FrontendFormsState => FrontendFormsViewFactory
      case FrontendFilesState => FrontendFilesViewFactory

      case RpcState => RpcViewFactory
      case RpcIntroState => RpcIntroViewFactory
      case RpcInterfacesState => RpcInterfacesViewFactory
      case RpcSerializationState => RpcSerializationViewFactory
      case RpcClientServerState => RpcClientServerViewFactory
      case RpcServerClientState => RpcServerClientViewFactory

      case RestState => RestViewFactory
      case RestIntroState => RestIntroViewFactory
      case RestInterfacesState => RestInterfacesViewFactory
      case RestClientServerState => RestClientServerViewFactory
      case RestServerState => RestServerViewFactory

      case I18NExtState => I18NExtViewFactory
      case BootstrapExtState => BootstrapExtViewFactory
      case AuthorizationExtState => AuthorizationExtViewFactory
      case ChartsExtState => ChartsExtViewFactory
      case JQueryExtState => JQueryExtViewFactory
      case UserActivityExtState => UserActivityExtViewFactory

      case FAQState => FAQViewFactory
      case LicenseState => LicenseViewFactory

      case _ => ErrorViewFactory
    }
}