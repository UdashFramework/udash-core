package io.udash.routing

import io.udash.core.State

trait Routing {
  val  /:/                       = io.udash.routing./:/
  type RoutingEngine[S <: State] = io.udash.routing.RoutingEngine[S]
  type UrlChangeProvider         = io.udash.routing.UrlChangeProvider
}
