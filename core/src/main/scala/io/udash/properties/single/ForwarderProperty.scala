package io.udash.properties.single

import io.udash.properties.{PropertyCreator, PropertyId}

private[properties] trait ForwarderReadableProperty[A] extends AbstractReadableProperty[A] {
  protected def origin: ReadableProperty[_]

  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] def parent: ReadableProperty[_] = null

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()
}

private[properties] trait ForwarderProperty[A] extends ForwarderReadableProperty[A] with AbstractProperty[A] {
  protected def origin: Property[_]
}
