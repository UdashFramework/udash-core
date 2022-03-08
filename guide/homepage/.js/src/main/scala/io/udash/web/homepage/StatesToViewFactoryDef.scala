package io.udash.web.homepage

import io.udash._
import io.udash.web.homepage.views._

final class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {

  import Context.applicationInstance

  def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] = state match {
    case RootState => RootViewFactory
    case _: IndexState => new IndexViewFactory
    case _ => ErrorViewFactory
  }
}