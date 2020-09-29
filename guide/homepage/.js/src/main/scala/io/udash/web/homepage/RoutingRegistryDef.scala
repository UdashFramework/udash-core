package io.udash.web.homepage

import io.udash._

class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
  def matchUrl(url: Url): RoutingState =
    url2State.applyOrElse("/" + url.stripPrefix("/").stripSuffix("/"), (_: String) => ErrorState)

  def matchState(state: RoutingState): Url =
    state2Url.apply(state)

  private val (url2State, state2Url) = bidirectional {
    case "/" => IndexState(None)
    case "/demo" / s => IndexState(Some(s))
  }
}