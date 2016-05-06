package io.udash.homepage

import io.udash._
import io.udash.homepage.views._

class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
  def matchStateToResolver(state: RoutingState): ViewPresenter[_ <: RoutingState] = state match {
    case RootState => RootViewPresenter
    case IndexState(s) => IndexViewPresenter
    case _ => ErrorViewPresenter
  }
}