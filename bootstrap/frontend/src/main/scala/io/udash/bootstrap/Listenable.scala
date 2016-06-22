package io.udash.bootstrap

import scala.collection.mutable

trait Listenable[EventType <: ListenableEvent] {

  type EventHandler = PartialFunction[EventType, Unit]

  private val onClickActions = mutable.LinkedHashSet.empty[EventHandler]

  def listen(onEvent: EventHandler): Registration = {
    onClickActions += onEvent
    new Registration(onEvent)
  }

  class Registration private[Listenable](onEvent: EventHandler) {
    def cancel(): Unit = onClickActions -= onEvent
  }

  protected def fire(event: EventType): Unit =
    onClickActions.iterator.foreach(handler => if (handler.isDefinedAt(event)) handler(event))
}

trait ListenableEvent
