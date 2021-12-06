package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.PropertyCreator
import io.udash.properties.single.{CastableProperty, ReadableProperty}
import io.udash.utils.CrossCollections

import scala.collection.Factory

private[properties] final class DirectSeqProperty[A: PropertyCreator, SeqTpe[T] <: BSeq[T]](
  override protected val parent: ReadableProperty[_])(implicit fac: Factory[A, SeqTpe[A]])
  extends AbstractSeqProperty[A, CastableProperty[A]] with CastableProperty[BSeq[A]] {

  private val properties = CrossCollections.createArray[CastableProperty[A]]

  override def elemProperties: BSeq[CastableProperty[A]] = properties

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[A]): Unit = {
    val oldProperties = CrossCollections.slice(properties, idx, idx + amount)
    val newProperties = values.map(value => PropertyCreator[A].newProperty(value, this))

    CrossCollections.replaceSeq(properties, idx, amount, newProperties)

    fireElementsListeners(Patch(idx, oldProperties.toSeq, newProperties.toSeq))
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

  override def touch(): Unit = {
    fireElementsListeners(Patch(0, properties.toSeq, properties.toSeq))
    valueChanged()
  }

  def get: SeqTpe[A] = properties.map(_.get).to(fac)
}
