package io.udash.properties.single

import java.util.UUID

import scala.concurrent.ExecutionContext

abstract class DirectPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                                    (implicit val executionContext: ExecutionContext) extends CastableProperty[A] {
  private var value: A = _

  override def get: A = value

  override def set(t: A): Unit =
    if (value != t) {
      value = t
      valueChanged()
    }

  override def setInitValue(t: A): Unit =
    value = t
}
