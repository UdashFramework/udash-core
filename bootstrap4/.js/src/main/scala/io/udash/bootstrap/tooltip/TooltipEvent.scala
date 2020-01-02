package io.udash.bootstrap
package tooltip

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}

final case class TooltipEvent(
  override val source: Tooltip,
  tpe: TooltipEvent.EventType
) extends AbstractCase with ListenableEvent

object TooltipEvent {
  /** More: <a href="http://getbootstrap.com/docs/4.1/components/tooltips/#events">Bootstrap Docs</a> */
  final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object EventType extends AbstractValueEnumCompanion[EventType] {
    /** This event fires immediately when the show instance method is called. */
    final val Show: Value = new EventType
    /** This event is fired when the tooltip has been made visible to the user (will wait for CSS transitions to complete). */
    final val Shown: Value = new EventType
    /** This event is fired immediately when the hide instance method has been called. */
    final val Hide: Value = new EventType
    /** This event is fired when the tooltip has finished being hidden from the user (will wait for CSS transitions to complete). */
    final val Hidden: Value = new EventType
    /** This event is fired after the show.bs.tooltip event when the tooltip template has been added to the DOM. */
    final val Inserted: Value = new EventType
  }
}