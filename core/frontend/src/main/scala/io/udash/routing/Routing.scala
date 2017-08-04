package io.udash.routing

import io.udash.GState

trait Routing {
  val /:/ = io.udash.routing./:/
  type RoutingEngine[S <: GState[S]] = io.udash.routing.RoutingEngine[S]
  type UrlChangeProvider = io.udash.routing.UrlChangeProvider
}
