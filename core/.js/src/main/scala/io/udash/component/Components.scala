package io.udash.component

trait Components {
  type Listenable = io.udash.component.Listenable
  type ListenableEvent = io.udash.component.ListenableEvent
}

object Components extends Components
