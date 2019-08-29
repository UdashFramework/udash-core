package io.udash.web.guide

import io.udash._
import io.udash.web.commons.views.MarkdownPageViewFactory
import io.udash.web.guide.views._
import io.udash.web.guide.views.bootstrapping.{BootstrappingFrontendViewFactory, _}
import io.udash.web.guide.views.ext._
import io.udash.web.guide.views.frontend.{FrontendFormsViewFactory, FrontendPropertiesViewFactory, FrontendRoutingViewFactory, FrontendTemplatesViewFactory, _}
import io.udash.web.guide.views.rpc.{RpcIntroViewFactory, RpcServerClientViewFactory, _}

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] =
    state match {
      case RootState => RootViewFactory
      case ContentState => ContentViewFactory

      case IntroState => MarkdownPageViewFactory[IntroState.type]()(Context.serverRpc.pages)

      case BootstrappingState => BootstrappingViewFactory
      case BootstrappingIntroState => BootstrappingIntroViewFactory
      case BootstrappingSbtState => BootstrappingSbtViewFactory
      case AdvancedBootstrappingSbtState => AdvancedBootstrappingSbtViewFactory
      case BootstrappingRpcState => BootstrappingRpcViewFactory
      case BootstrappingFrontendState => BootstrappingFrontendViewFactory
      case BootstrappingBackendState => BootstrappingBackendViewFactory

      case FrontendState => FrontendViewFactory
      case FrontendIntroState => FrontendIntroViewFactory
      case FrontendRoutingState(_) => FrontendRoutingViewFactory
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

      case RestState => MarkdownPageViewFactory[RestState.type]()(Context.serverRpc.pages)

      case I18NExtState => I18NExtViewFactory
      case BootstrapExtState => BootstrapExtViewFactory
      case AuthorizationExtState => AuthorizationExtViewFactory
      case ChartsExtState => ChartsExtViewFactory
      case JQueryExtState => JQueryExtViewFactory
      case UserActivityExtState => UserActivityExtViewFactory

      case FaqState => FaqViewFactory
      case LicenseState => MarkdownPageViewFactory[LicenseState.type]()(Context.serverRpc.pages)
      case ErrorState => ErrorViewFactory
    }
}