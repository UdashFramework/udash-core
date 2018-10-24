package io.udash.component

import scalatags.JsDom.GenericAttr

trait Components {
  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]

  type Component = io.udash.component.Component
  type ComponentId = io.udash.component.ComponentId
  val ComponentId = io.udash.component.ComponentId
  type Listenable[ComponentType <: Listenable[ComponentType, _], EventType <: ListenableEvent[ComponentType]] = io.udash.component.Listenable[ComponentType, EventType]
  type ListenableEvent[ComponentType <: Listenable[ComponentType, _]] = io.udash.component.ListenableEvent[ComponentType]
}

object Components extends Components
