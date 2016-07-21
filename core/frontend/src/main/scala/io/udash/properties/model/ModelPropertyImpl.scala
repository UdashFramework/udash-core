package io.udash.properties
package model

import java.util.UUID

import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}

import scala.concurrent.ExecutionContext

abstract class ModelPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                                   (implicit val executionContext: ExecutionContext)
  extends ModelProperty[A] with CastableProperty[A] {

  protected var initialized: Boolean = false

  /** Creates all sub properties and puts them in `properties`. */
  protected def initialize(): Unit

  def getSubProperty[T](key: String): Property[T] = {
    if (!initialized) {
      initialized = true
      initialize()
    }
    properties(key).asInstanceOf[Property[T]]
  }
}
