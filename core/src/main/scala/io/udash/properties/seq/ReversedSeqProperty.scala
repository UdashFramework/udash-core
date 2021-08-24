package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{Property, ReadableProperty}

private[properties] class ReversedReadableSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType]
) extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] {

  override protected def getFromOrigin(): BSeq[A] = origin.get.reverse
  override protected def transformElements(elemProperties: BSeq[ElemType]): BSeq[ElemType] = elemProperties.reverse
  override protected def transformPatch(patch: Patch[ElemType]): Opt[Patch[ElemType]] =
    Patch[ElemType](
      origin.size - patch.idx - patch.added.size,
      patch.removed.reverse,
      patch.added.reverse,
    ).opt
}

private[properties] final class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends ReversedReadableSeqProperty[A, Property[A]](origin) with AbstractSeqProperty[A, Property[A]] {

  override def setInitValue(t: BSeq[A]): Unit =
    origin.setInitValue(t.reverse)

  override def set(t: BSeq[A], force: Boolean = false): Unit =
    origin.set(t.reverse, force)

  override def touch(): Unit =
    origin.touch()

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[A]): Unit =
    origin.replaceSeq(origin.size - idx, amount, values.reverse)
}
