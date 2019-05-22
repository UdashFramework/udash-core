package io.udash.properties.single

import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.{PropertyCreator, PropertyId}
import io.udash.utils.Registration

private[properties] class ReadableWrapper[T](private val p: ReadableProperty[T]) extends ReadableProperty[T] {
  override val id: PropertyId = p.id
  override def get: T = p.get
  override def listen(valueListener: T => Any, initUpdate: Boolean): Registration = p.listen(valueListener, initUpdate)
  override def listenOnce(valueListener: T => Any): Registration = p.listenOnce(valueListener)
  override def listenersCount(): Int = p.listenersCount()
  override protected[properties] def parent: ReadableProperty[_] = p.parent
  override protected[properties] def fireValueListeners(): Unit = p.fireValueListeners()
  override protected[properties] def valueChanged(): Unit = p.valueChanged()
  override protected[properties] def listenersUpdate(): Unit = p.listenersUpdate()
  override def transform[B](transformer: T => B): ReadableProperty[B] = p.transform(transformer)

  override def transformToSeq[B: PropertyCreator](transformer: T => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    p.transformToSeq(transformer)

  override def streamTo[B](target: Property[B], initUpdate: Boolean)(transformer: T => B): Registration =
    p.streamTo(target, initUpdate)(transformer)

  override def readable: ReadableProperty[T] = this
}
