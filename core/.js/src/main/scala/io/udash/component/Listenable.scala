package io.udash.component

import com.avsystem.commons._
import io.udash.Registration

/**
 * A class (typically a component) exposing callbacks events.
 *
 * Callbacks emitted by this utility interface are executed in the order of registration (restart counts as an registration).
 * Events can be defined via implementing [[ListenableEvent]] and setting the `EventType` appropriately.
 *
 */
trait Listenable {

  /**
   * The type of emitted events. Has to be set to the top-level type of events emitted by this component.
   */
  type EventType <: ListenableEvent

  final type EventHandler = PartialFunction[EventType, Unit]

  //visible for testing
  private[udash] val listeners = MLinkedHashSet.empty[EventHandler]

  /**
   * Registers an event handler in the component.
   *
   * @param onEvent Partial function which handles component events.
   * @return [[Registration]] which allows you to manage the lifecycle of the listener.
   */
  final def listen(onEvent: EventHandler): Registration = new ListenableRegistration(onEvent)

  /**
   * Removes all registered listeners.
   */
  final def removeListeners(): Unit = listeners.clear()

  private class ListenableRegistration(onEvent: EventHandler) extends Registration {
    locally(start())

    private def start(): Unit = listeners += onEvent
    override def cancel(): Unit = listeners -= onEvent
    override def restart(): Unit = {
      cancel()
      start()
    }
    override def isActive: Boolean = listeners.contains(onEvent)
  }

  /**
   * Fires an event on the Listenable.
   */
  protected final def fire(event: EventType): Unit = listeners.foreach(_.applyOrElse(event, (_: EventType) => ()))
}

/**
 * An event emitted by a [[Listenable]].
 */
trait ListenableEvent { self =>
  val source: Listenable {type EventType >: self.type <: ListenableEvent}
}
