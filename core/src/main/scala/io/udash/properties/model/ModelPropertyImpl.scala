package io.udash.properties
package model

import com.avsystem.commons.MMap
import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}
import io.udash.utils.CrossCollections

abstract class ModelPropertyImpl[A](val parent: ReadableProperty[_])
  extends ModelProperty[A] with CastableProperty[A] with ModelPropertyMacroApi[A] {

  protected final val properties: MMap[String, Property[_]] = CrossCollections.createDictionary[Property[_]]

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

  override def getSubProperty[T: PropertyCreator](getter: A => T, key: String): Property[T] = {
    if (!initialized) {
      initialized = true
      initialize()
    }
    properties(key).asInstanceOf[Property[T]]
  }

  def getSubModel[T: ModelPropertyCreator](getter: A => T, key: String): ReadableModelProperty[T] =
    getSubProperty(getter, key).asInstanceOf[ReadableModelProperty[T]]

  def touch(): Unit = CallbackSequencer().sequence {
    properties.values.foreach(_.touch())
  }

  override def get: A =
    if (isEmpty) null.asInstanceOf[A]
    else internalGet

  override def set(t: A, force: Boolean): Unit =
    if (!isEmpty || t != null) {
      if (t != null) isEmpty = false
      CallbackSequencer().sequence {
        internalSet(t, withCallbacks = true, force = force)
      }
    }

  override def setInitValue(t: A): Unit =
    if (!isEmpty || t != null) {
      if (t != null) isEmpty = false
      CallbackSequencer().sequence {
        internalSet(t, withCallbacks = false, force = false)
      }
    }

  protected def setSubProp[T](p: Property[T], v: T, withCallbacks: Boolean, force: Boolean): Unit =
    if (withCallbacks) p.set(v, force) else p.setInitValue(v)
}
