package io.udash.bootstrap

import scala.collection.mutable

trait Listenable[EventType <: ListenableEvent] {

  type EventHandler = PartialFunction[EventType, Unit]

  private val onClickActions = mutable.LinkedHashSet.empty[EventHandler]

  def listen(onClick: EventHandler): Registration = {
    onClickActions += onClick
    new Registration(onClick)
  }

  class Registration private[Listenable](onClick: EventHandler) {
    def cancel(): Unit = onClickActions -= onClick
  }

  protected def fire(event: EventType): Unit =
    onClickActions.iterator.foreach(handler => if (handler.isDefinedAt(event)) handler(event))
}

trait ListenableEvent
