package io.udash.selenium.routing

import io.udash._

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState

  def url(implicit application: Application[RoutingState]): String =
    s"${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with ContainerState
sealed abstract class FinalRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with FinalState

case object RootState extends ContainerRoutingState(None)

case object IntroState extends FinalRoutingState(Some(RootState))

case object FrontendDemosState extends FinalRoutingState(Some(RootState))

case class FrontendRoutingDemosState(value: Option[String]) extends FinalRoutingState(Some(RootState))

case object RpcDemosState extends FinalRoutingState(Some(RootState))

case object RestDemosState extends FinalRoutingState(Some(RootState))

case object JQueryDemosState extends FinalRoutingState(Some(RootState))

case object I18nDemosState extends FinalRoutingState(Some(RootState))

case object UserActivityDemosState extends FinalRoutingState(Some(RootState))