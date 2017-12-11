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

  protected def internalSet(value: A, withCallbacks: Boolean, force: Boolean): Unit
  protected def internalGet: A

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

  def touch(): Unit = CallbackSequencer.sequence {
    properties.values.foreach(_.touch())
  }

  override def get: A =
    if (isEmpty) null.asInstanceOf[A]
    else internalGet

  override def set(t: A, force: Boolean): Unit =
    if (!isEmpty || t != null) {
      if (t != null) isEmpty = false
      CallbackSequencer.sequence {
        internalSet(t, withCallbacks = true, force = force)
      }
    }

  override def setInitValue(t: A): Unit =
    if (!isEmpty || t != null) {
      if (t != null) isEmpty = false
      CallbackSequencer.sequence {
        internalSet(t, withCallbacks = false, force = false)
      }
    }

  protected def setSubProp[T](p: Property[T], v: T, withCallbacks: Boolean, force: Boolean): Unit =
    if (withCallbacks) p.set(v, force) else p.setInitValue(v)
}
