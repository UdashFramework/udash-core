package io.udash.bindings.modifiers

import scalatags.generic.Modifier

private[udash] final class EmptyModifier[T] extends Modifier[T] {
  override def applyTo(t: T): Unit = ()
}