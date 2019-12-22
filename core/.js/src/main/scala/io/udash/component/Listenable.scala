package io.udash.component

import com.avsystem.commons._
import io.udash.Registration

trait Listenable {
  type EventType <: ListenableEvent
  final type EventHandler = PartialFunction[EventType, Unit]

  //visible for testing
  private[udash] val listeners = MLinkedHashSet.empty[EventHandler]

  /**
   * Register event handler in component.
   *
   * @param onEvent Partial function which handles component events.
   * @return [[Registration]] which allows you to remove listener from this component.
   */
  final def listen(onEvent: EventHandler): Registration = {
    listeners += onEvent
    new ListenableRegistration(onEvent)
  }

  /**
   * Remove all registered listeners.
   */
  final def removeListeners(): Unit = {
    listeners.clear()
  }

  private class ListenableRegistration(onEvent: EventHandler) extends Registration {
    override def cancel(): Unit = listeners -= onEvent
    override def restart(): Unit = listeners += onEvent
    override def isActive: Boolean = listeners.contains(onEvent)
  }

  /**
   * Fires event on the Listenable.
   */
  protected final def fire(event: EventType): Unit = listeners.foreach(_.applyOrElse(event, (_: EventType) => ()))
}

trait ListenableEvent { self =>
  def source: Listenable {type EventType >: self.type <: ListenableEvent}
}
