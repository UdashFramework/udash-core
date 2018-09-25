package io.udash.selenium.routing

import io.udash._

class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
  def matchUrl(url: Url): RoutingState =
    url2State.applyOrElse("/" + url.value.stripPrefix("/").stripSuffix("/"), (x: String) => IntroState)

  def matchState(state: RoutingState): Url =
    Url(state2Url.apply(state))

  private val (url2State, state2Url) = bidirectional {
    case "/" => IntroState
    case "/frontend" => FrontendDemosState
    case "/frontend/routing" => FrontendRoutingDemosState(None)
    case "/frontend/routing" / arg => FrontendRoutingDemosState(Some(arg))
    case "/rpc" => RpcDemosState
    case "/rest" => RestDemosState
    case "/i18n" => I18nDemosState
    case "/jquery" => JQueryDemosState
    case "/activity" => UserActivityDemosState
  }
}