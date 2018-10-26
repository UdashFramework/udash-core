package io.udash.selenium.routing

import io.udash._
import io.udash.selenium.views._

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] =
    state match {
      case RootState => RootViewFactory
      case IntroState => IntroViewFactory
      case FrontendDemosState => FrontendDemosViewFactory
      case FrontendRoutingDemosState(_) => FrontendRoutingDemosViewFactory
      case RpcDemosState => RpcDemosViewFactory
      case RestDemosState => RestDemosViewFactory
      case I18nDemosState => I18nDemosViewFactory
      case JQueryDemosState => JQueryDemosViewFactory
      case BootstrapDemosState => BootstrapDemosViewFactory
      case _ => RootViewFactory
    }
}