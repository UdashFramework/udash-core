package io.udash.properties

import java.util.UUID

import io.udash.properties.single.{CastableProperty, ReadableProperty}

import scala.concurrent.ExecutionContext

trait PropertyCreator[T] {
  def newProperty(prt: ReadableProperty[_])(implicit ec: ExecutionContext): CastableProperty[T]

  def newProperty(value: T, prt: ReadableProperty[_])(implicit ec: ExecutionContext): CastableProperty[T] = {
    val prop = newProperty(prt)
    prop.setInitValue(value)
    prop
  }
}

object PropertyCreator {
  implicit def propertyCreator[T]: PropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyPropertyCreator[T]

  def newID(): UUID =
    UUID.randomUUID()
}