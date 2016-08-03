package io.udash.properties.single
import java.util.UUID

import io.udash.properties.PropertyCreator

import scala.concurrent.ExecutionContext

trait ForwarderReadableProperty[A] extends ReadableProperty[A] {
  protected def origin: ReadableProperty[_]

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] def parent: ReadableProperty[_] = null

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()

  override implicit protected[properties] def executionContext: ExecutionContext =
    origin.executionContext
}

trait ForwarderProperty[A] extends ForwarderReadableProperty[A] with Property[A] {
  protected def origin: Property[_]
}
