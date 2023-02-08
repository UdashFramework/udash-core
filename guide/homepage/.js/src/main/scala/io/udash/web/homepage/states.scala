package io.udash.web.homepage

import com.avsystem.commons.misc.AbstractSealedEnumCompanion
import io.udash._
import io.udash.web.homepage.components.demo.{CodeDemo, HelloDemo, SelectDemo}

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState
  def url(implicit application: Application[RoutingState]): String = s"${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState)

case object RootState extends ContainerRoutingState(None)

case object ErrorState extends RoutingState(Some(RootState))

sealed abstract class IndexState(val name: String, val codeDemo: CodeDemo) extends RoutingState(Some(RootState))
case object HelloState extends IndexState("Hello, World!", HelloDemo)
case object SelectState extends IndexState("Select", SelectDemo)
object IndexState extends AbstractSealedEnumCompanion[IndexState] {
  override val values: Seq[IndexState] = caseObjects
}