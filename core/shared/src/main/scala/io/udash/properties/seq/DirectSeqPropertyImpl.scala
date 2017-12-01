package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.PropertyCreator
import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.utils.{JsArrayRegistration, Registration}

import scala.scalajs.js

class DirectSeqPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                              (implicit propertyCreator: PropertyCreator[A])
  extends SeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = js.Array[CastableProperty[A]]()
  private val structureListeners: js.Array[Patch[CastableProperty[A]] => Any] = js.Array()

  override def elemProperties: Seq[CastableProperty[A]] =
    properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = properties.jsSlice(idx, idx + amount)
    val newProperties = if (values != null) values.map(value => propertyCreator.newProperty(value, this)) else Seq.empty

    properties.splice(idx, amount, newProperties: _*)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty), structureListeners)
    valueChanged()
  }

  override def set(t: Seq[A], force: Boolean = false): Unit =
    if (force || t != get) {
      replace(0, properties.length, t: _*)
    }

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = t.map(value => propertyCreator.newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  override def touch(): Unit =
    replace(0, properties.length, get: _*)

  def get: Seq[A] =
    properties.map(_.get)

  override def listenStructure(l: (Patch[CastableProperty[A]]) => Any): Registration = {
    structureListeners += l
    new JsArrayRegistration(structureListeners, l)
  }

  override def clearListeners(): Unit = {
    super.clearListeners()
    structureListeners.clear()
  }
}
