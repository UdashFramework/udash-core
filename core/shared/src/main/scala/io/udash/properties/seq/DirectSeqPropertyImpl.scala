package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.{CrossCollections, MutableBufferRegistration, PropertyCreator}
import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.utils.Registration

class DirectSeqPropertyImpl[A : PropertyCreator](val parent: ReadableProperty[_], override val id: UUID)
  extends SeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = CrossCollections.createArray[CastableProperty[A]]
  private val structureListeners = CrossCollections.createArray[Patch[CastableProperty[A]] => Any]

  override def elemProperties: Seq[CastableProperty[A]] =
    properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = CrossCollections.slice(properties, idx, idx + amount)
    val newProperties = if (values != null) values.map(value => implicitly[PropertyCreator[A]].newProperty(value, this)) else Seq.empty

    CrossCollections.replace(properties, idx, amount, newProperties: _*)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty), structureListeners)
    valueChanged()
  }

  override def set(t: Seq[A], force: Boolean = false): Unit =
    if (force || t != get) {
      replace(0, properties.length, t: _*)
    }

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = t.map(value => implicitly[PropertyCreator[A]].newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  override def touch(): Unit =
    replace(0, properties.length, get: _*)

  def get: Seq[A] =
    properties.map(_.get)

  override def listenStructure(l: (Patch[CastableProperty[A]]) => Any): Registration = {
    structureListeners += l
    new MutableBufferRegistration(structureListeners, l)
  }

  override def clearListeners(): Unit = {
    super.clearListeners()
    structureListeners.clear()
  }
}
