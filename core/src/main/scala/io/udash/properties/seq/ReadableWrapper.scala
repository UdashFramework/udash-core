package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{ReadableProperty, ReadableWrapper => SingleReadableWrapper}
import io.udash.utils.Registration

private[properties] class ReadableWrapper[T](private val p: ReadableSeqProperty[T, _ <: ReadableProperty[T]])
  extends SingleReadableWrapper[BSeq[T]](p) with ReadableSeqProperty[T, ReadableProperty[T]] {

  override def readable: ReadableSeqProperty[T, ReadableProperty[T]] = this

  override def elemProperties: BSeq[ReadableProperty[T]] =
    p.elemProperties.map(_.readable)

  override def listenStructure(structureListener: Patch[ReadableProperty[T]] => Any): Registration =
    p.listenStructure { patch =>
      structureListener(patch.copy(
        added = patch.added.map(_.readable),
        removed = patch.removed.map(_.readable)
      ))
    }

  override def structureListenersCount(): Int = p.structureListenersCount()
  override def transformElements[B](transformer: T => B): ReadableSeqProperty[B, ReadableProperty[B]] = p.transformElements(transformer)
  override def reversed(): ReadableSeqProperty[T, ReadableProperty[T]] = p.reversed()
  override def filter(matcher: T => Boolean): ReadableSeqProperty[T, _ <: ReadableProperty[T]] = p.filter(matcher)
  override def zipWithIndex: ReadableSeqProperty[(T, Int), ReadableProperty[(T, Int)]] = p.zipWithIndex
}
