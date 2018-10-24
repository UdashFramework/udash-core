package io.udash.component

/** Component's event. */
trait ListenableEvent[ComponentType <: Listenable[ComponentType, _]] {
  def source: ComponentType
}
