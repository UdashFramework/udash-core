package io.udash.bootstrap.tooltip

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.bootstrap.ListenableEvent

final case class TooltipEvent[TooltipType <: Tooltip[_, TooltipType]](
  override val source: TooltipType,
  tpe: TooltipEvent.EventType
) extends AbstractCase with ListenableEvent[TooltipType]

object TooltipEvent {
  final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object EventType extends AbstractValueEnumCompanion[EventType] {
    final val Show, Shown, Hide, Hidden, Inserted: Value = new EventType
  }
}