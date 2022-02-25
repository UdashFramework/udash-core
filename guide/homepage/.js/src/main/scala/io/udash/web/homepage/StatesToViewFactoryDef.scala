package io.udash.web.homepage

import io.udash._
import io.udash.web.homepage.views._

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] = state match {
    case RootState => RootViewFactory
    case _: IndexState => IndexViewFactory
    case _ => ErrorViewFactory
  }
}