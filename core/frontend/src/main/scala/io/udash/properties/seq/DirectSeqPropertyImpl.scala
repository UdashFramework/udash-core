package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.{CallbackSequencer, PropertyCreator, PropertyRegistration}
import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class DirectSeqPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                              (implicit propertyCreator: PropertyCreator[A],
                               val executionContext: ExecutionContext) extends SeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = mutable.ListBuffer[CastableProperty[A]]()
  private val structureListeners: mutable.Set[Patch[CastableProperty[A]] => Any] = mutable.Set()

  override def elemProperties: Seq[CastableProperty[A]] =
    properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = properties.slice(idx, idx + amount)
    val newProperties = if (values != null) values.map(value => propertyCreator.newProperty(value, this)) else Seq.empty
    properties.remove(idx, amount)
    properties.insertAll(idx, newProperties)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty), structureListeners)
    valueChanged()
  }

  override def set(t: Seq[A]): Unit =
    replace(0, properties.size, t: _*)

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = t.map(value => propertyCreator.newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  def get: Seq[A] =
    properties.map(_.get)

  override def listenStructure(l: (Patch[CastableProperty[A]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}
