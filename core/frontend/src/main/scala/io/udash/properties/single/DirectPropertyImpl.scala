package io.udash.properties.single

import java.util.UUID

abstract class DirectPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
  extends CastableProperty[A] {

  private var value: A = _

  override def get: A = value

  override def set(t: A, force: Boolean = false): Unit =
    if (force || value != t) {
      value = t
      valueChanged()
    }

  override def setInitValue(t: A): Unit =
    value = t

  override def touch(): Unit =
    valueChanged()
}
