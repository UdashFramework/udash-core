package io.udash.properties.single

private[properties] class DirectPropertyImpl[A](protected val parent: ReadableProperty[_])
  extends AbstractProperty[A] with CastableProperty[A] {

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
