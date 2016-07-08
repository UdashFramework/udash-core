package io.udash.web.guide

import io.udash._
import io.udash.web.guide.views._
import io.udash.web.guide.views.bootstrapping.{BootstrappingFrontendViewPresenter, _}
import io.udash.web.guide.views.ext.{I18NExtViewPresenter, JQueryExtViewPresenter}
import io.udash.web.guide.views.frontend.{FrontendFormsViewPresenter, FrontendPropertiesViewPresenter, FrontendRoutingViewPresenter, FrontendTemplatesViewPresenter, _}
import io.udash.web.guide.views.rest._
import io.udash.web.guide.views.rpc.{RpcIntroViewPresenter, RpcServerClientViewPresenter, _}

class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewPresenter[_ <: RoutingState] =
    state match {
      case RootState => RootViewPresenter
      case ContentState => ContentViewPresenter

      case IntroState => IntroViewPresenter

      case BootstrappingState => BootstrappingViewPresenter
      case BootstrappingIntroState => BootstrappingIntroViewPresenter
      case BootstrappingSBTState => BootstrappingSBTViewPresenter
      case BootstrappingRpcState => BootstrappingRpcViewPresenter
      case BootstrappingFrontendState => BootstrappingFrontendViewPresenter
      case BootstrappingBackendState => BootstrappingBackendViewPresenter
      case BootstrappingGeneratorsState => BootstrappingGeneratorsViewPresenter

      case FrontendState => FrontendViewPresenter
      case FrontendIntroState => FrontendIntroViewPresenter
      case FrontendRoutingState(s) => FrontendRoutingViewPresenter
      case FrontendMVPState => FrontendMVPViewPresenter
      case FrontendTemplatesState => FrontendTemplatesViewPresenter
      case FrontendPropertiesState => FrontendPropertiesViewPresenter
      case FrontendBindingsState => FrontendBindingsViewPresenter
      case FrontendFormsState => FrontendFormsViewPresenter

      case RpcState => RpcViewPresenter
      case RpcIntroState => RpcIntroViewPresenter
      case RpcInterfacesState => RpcInterfacesViewPresenter
      case RpcSerializationState => RpcSerializationViewPresenter
      case RpcClientServerState => RpcClientServerViewPresenter
      case RpcServerClientState => RpcServerClientViewPresenter

      case RestState => RestViewPresenter
      case RestIntroState => RestIntroViewPresenter
      case RestInterfacesState => RestInterfacesViewPresenter
      case RestClientServerState => RestClientServerViewPresenter

      case I18NExtState => I18NExtViewPresenter
      case JQueryExtState => JQueryExtViewPresenter

      case FAQState => FAQViewPresenter
      case LicenseState => LicenseViewPresenter

      case _ => ErrorViewPresenter
    }
}