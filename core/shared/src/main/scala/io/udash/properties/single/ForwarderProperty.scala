package io.udash.properties.single

import io.udash.properties.{PropertyCreator, PropertyId, ValidationResult}

import scala.concurrent.Future

trait ForwarderReadableProperty[A] extends AbstractReadableProperty[A] {
  protected def origin: ReadableProperty[_]

  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] def parent: ReadableProperty[_] = null

  override def isValid: Future[ValidationResult] =
    origin.isValid

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()
}

trait ForwarderProperty[A] extends ForwarderReadableProperty[A] with AbstractProperty[A] {
  protected def origin: Property[_]
}
