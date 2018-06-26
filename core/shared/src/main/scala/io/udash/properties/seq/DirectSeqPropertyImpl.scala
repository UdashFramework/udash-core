package io.udash.properties.seq

import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.properties.{CrossCollections, PropertyCreator, PropertyId}

private[properties] class DirectSeqPropertyImpl[A: PropertyCreator](val parent: ReadableProperty[_], override val id: PropertyId)
  extends AbstractSeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = CrossCollections.createArray[CastableProperty[A]]

  override def elemProperties: Seq[CastableProperty[A]] = properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = CrossCollections.slice(properties, idx, idx + amount)
    val newProperties = if (values != null) values.map(value => implicitly[PropertyCreator[A]].newProperty(value, this)) else Seq.empty

    CrossCollections.replace(properties, idx, amount, newProperties: _*)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty), structureListeners)
    valueChanged()
  }

  override def set(t: Seq[A], force: Boolean = false): Unit =
    if (force || t != get) {
      replace(0, properties.length, Option(t).getOrElse(Seq.empty): _*)
    }

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = Option(t)
      .getOrElse(Seq.empty)
      .map(value => implicitly[PropertyCreator[A]].newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  override def touch(): Unit =
    replace(0, properties.length, get: _*)

  def get: Seq[A] =
    properties.map(_.get)
}
