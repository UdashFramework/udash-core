package io.udash.component

trait Components {
  type Listenable[ComponentType <: Listenable[ComponentType, _], EventType <: ListenableEvent[ComponentType]] = io.udash.component.Listenable[ComponentType, EventType]
  type ListenableEvent[ComponentType <: Listenable[ComponentType, _]] = io.udash.component.ListenableEvent[ComponentType]
}

object Components extends Components
