package io.udash.bindings.modifiers

import scalatags.generic.Modifier

private[bindings] class EmptyModifier[T] extends Modifier[T] {
  override def applyTo(t: T): Unit = ()
}