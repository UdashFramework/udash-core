package io.udash.bootstrap

import io.udash.utils.{CallbacksHandler, Registration}
import io.udash.wrappers.jquery.{JQueryEvent, _}
import org.scalajs.dom

/** Bootstrap component exposing events. */
trait Listenable[ComponentType <: Listenable[ComponentType, _], EventType <: ListenableEvent[ComponentType]] {
  private val actions = new CallbacksHandler[EventType]

  /**
    * Register event handler in component.
    *
    * The callbacks are executed in order of registration. Registration operations don't preserve callbacks order.
    * Each callback is executed once, exceptions thrown in callbacks are swallowed.
    *
    * @param onEvent Partial function which handles component events.
    * @return [[Registration]] which allows you to remove listener from this component.
    */
  def listen(onEvent: actions.CallbackType): Registration =
    actions.register(onEvent)

  protected def fire(event: EventType): Unit =
    actions.fire(event)

  protected def jQFire(ev: EventType): JQueryCallback =
    (_: dom.Element, _: JQueryEvent) => fire(ev)
}

/** Bootstrap component event. */
trait ListenableEvent[ComponentType <: Listenable[ComponentType, _]] {
  def source: ComponentType
}
