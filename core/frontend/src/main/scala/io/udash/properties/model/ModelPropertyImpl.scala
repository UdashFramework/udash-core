package io.udash.properties
package model

import java.util.UUID

import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}


abstract class ModelPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
  extends ModelProperty[A] with CastableProperty[A] {

  /** False if subproperties were not created yet. */
  protected var initialized: Boolean = false
  /** True if value was never changed. */
  protected var isEmpty: Boolean = true

  /** Creates all sub properties and puts them in `properties`. */
  protected def initialize(): Unit

  override protected[properties] def valueChanged(): Unit = {
    isEmpty = false
    super.valueChanged()
  }

  def getSubProperty[T](key: String): Property[T] = {
    if (!initialized) {
      initialized = true
      initialize()
    }
    properties(key).asInstanceOf[Property[T]]
  }
}
