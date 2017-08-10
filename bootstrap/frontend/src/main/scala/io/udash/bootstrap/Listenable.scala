package io.udash.bootstrap

import io.udash.utils.{Registration, SetRegistration}
import io.udash.wrappers.jquery.{JQueryEvent, _}
import org.scalajs.dom

import scala.collection.mutable

/** Bootstrap component exposing events. */
trait Listenable[ComponentType <: Listenable[ComponentType, _], EventType <: ListenableEvent[ComponentType]] {
  type EventHandler = PartialFunction[EventType, Unit]

  private val onClickActions = mutable.LinkedHashSet.empty[EventHandler]

  /**
    * Register event handler in component.
    * @param onEvent Partial function which handles component events.
    * @return [[Registration]] which allows you to remove listener from this component.
    */
  def listen(onEvent: EventHandler): Registration = {
    onClickActions += onEvent
    new SetRegistration(onClickActions, onEvent)
  }

  protected def fire(event: EventType): Unit =
    onClickActions.iterator.foreach(handler =>
      if (handler.isDefinedAt(event)) handler(event)
    )

  protected def jQFire(ev: EventType): JQueryCallback =
    (_: dom.Element, _: JQueryEvent) => fire(ev)
}

/** Bootstrap component event. */
trait ListenableEvent[ComponentType <: Listenable[ComponentType, _]] {
  def source: ComponentType
}
