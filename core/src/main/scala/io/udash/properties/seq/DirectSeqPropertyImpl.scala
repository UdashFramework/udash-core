package io.udash.properties.seq

import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.properties.{PropertyCreator, PropertyId}
import io.udash.utils.CrossCollections

import scala.collection.generic.CanBuildFrom

private[properties] class DirectSeqPropertyImpl[A: PropertyCreator, SeqTpe[T] <: Seq[T]](
  val parent: ReadableProperty[_], override val id: PropertyId)(implicit cbf: CanBuildFrom[Nothing, A, SeqTpe[A]])
  extends AbstractSeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = CrossCollections.createArray[CastableProperty[A]]

  override def elemProperties: Seq[CastableProperty[A]] = properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = CrossCollections.slice(properties, idx, idx + amount)
    val newProperties = if (values != null) values.map(value => PropertyCreator[A].newProperty(value, this)) else Seq.empty

    CrossCollections.replace(properties, idx, amount, newProperties: _*)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty))
    valueChanged()
  }

  override def set(t: Seq[A], force: Boolean = false): Unit =
    if (force || t != get) {
      replace(0, properties.length, Option(t).getOrElse(Seq.empty): _*)
    }

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = Option(t)
      .getOrElse(Seq.empty)
      .map(value => PropertyCreator[A].newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  override def touch(): Unit =
    replace(0, properties.length, get: _*)

  def get: SeqTpe[A] = properties.map(_.get).to[SeqTpe]
}
