package io.udash.guide

import io.udash._
import io.udash.guide.views.bootstrapping.{BootstrappingFrontendViewPresenter, _}
import io.udash.guide.views.frontend.{FrontendFormsViewPresenter, FrontendPropertiesViewPresenter, FrontendRoutingViewPresenter, FrontendTemplatesViewPresenter, _}
import io.udash.guide.views.rpc.{RpcIntroViewPresenter, RpcServerClientViewPresenter, _}
import io.udash.guide.views.{FAQViewPresenter, IntroViewPresenter, ErrorViewPresenter, RootViewPresenter}

class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewPresenter[_ <: RoutingState] =
    state match {
      case RootState => RootViewPresenter

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
      case RpcClientServerState => RpcClientServerViewPresenter
      case RpcServerClientState => RpcServerClientViewPresenter

      case FAQState => FAQViewPresenter

      case _ => ErrorViewPresenter
    }
}