package io.udash.web.homepage

import io.udash._

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState
  def url(implicit application: Application[RoutingState]): String = s"${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with ContainerState

case object RootState extends ContainerRoutingState(None)

case object ErrorState extends RoutingState(Some(RootState))

case class IndexState(selectedDemoTab: Option[String]) extends RoutingState(Some(RootState))

