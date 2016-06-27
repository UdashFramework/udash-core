package io.udash.bootstrap

import io.udash.wrappers.jquery.{JQueryEvent, _}
import org.scalajs.dom

import scala.collection.mutable

trait Listenable[ComponentType <: Listenable[ComponentType, _], EventType <: ListenableEvent[ComponentType]] {

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

  protected def jQFire(ev: EventType): JQueryCallback =
    (_: dom.Element, _: JQueryEvent) => fire(ev)
}

trait ListenableEvent[ComponentType <: Listenable[ComponentType, _]] {
  def source: ComponentType
}
