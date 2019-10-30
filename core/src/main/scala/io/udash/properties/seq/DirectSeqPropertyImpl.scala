package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.properties.{PropertyCreator, PropertyId}
import io.udash.utils.CrossCollections

import scala.collection.compat._

private[properties] class DirectSeqPropertyImpl[A: PropertyCreator, SeqTpe[T] <: BSeq[T]](
  val parent: ReadableProperty[_], override val id: PropertyId)(implicit fac: Factory[A, SeqTpe[A]])
  extends AbstractSeqProperty[A, CastableProperty[A]] with CastableProperty[BSeq[A]] {

  private val properties = CrossCollections.createArray[CastableProperty[A]]

  override def elemProperties: BSeq[CastableProperty[A]] = properties

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[A]): Unit = {
    val oldProperties = CrossCollections.slice(properties, idx, idx + amount)
    val newProperties = if (values != null) values.map(value => PropertyCreator[A].newProperty(value, this)) else Seq.empty

    CrossCollections.replaceSeq(properties, idx, amount, newProperties)

    fireElementsListeners(Patch(idx, oldProperties.toSeq, newProperties.toSeq, properties.isEmpty), structureListeners)
    valueChanged()
  }

  override def set(t: BSeq[A], force: Boolean = false): Unit =
    if (force || t != get) {
      replaceSeq(0, properties.length, t.opt.iterator.flatMap(_.iterator).toSeq)
    }

  override def setInitValue(t: BSeq[A]): Unit = {
    val newProperties = Option(t)
      .getOrElse(Seq.empty)
      .map(value => PropertyCreator[A].newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  override def touch(): Unit =
    replaceSeq(0, properties.length, get.toSeq)

  def get: SeqTpe[A] = properties.map(_.get).to(fac)
}
