package io.udash.bootstrap.tooltip

import com.avsystem.commons.misc.AbstractCase
import io.udash.bootstrap.ListenableEvent

sealed trait TooltipEvent[TooltipType <: Tooltip[_, TooltipType]] extends AbstractCase with ListenableEvent[TooltipType]
object TooltipEvent {
  final case class ShowEvent[TooltipType <: Tooltip[_, TooltipType]](source: TooltipType) extends TooltipEvent[TooltipType]
  final case class ShownEvent[TooltipType <: Tooltip[_, TooltipType]](source: TooltipType) extends TooltipEvent[TooltipType]
  final case class HideEvent[TooltipType <: Tooltip[_, TooltipType]](source: TooltipType) extends TooltipEvent[TooltipType]
  final case class HiddenEvent[TooltipType <: Tooltip[_, TooltipType]](source: TooltipType) extends TooltipEvent[TooltipType]
  final case class InsertedEvent[TooltipType <: Tooltip[_, TooltipType]](source: TooltipType) extends TooltipEvent[TooltipType]
}