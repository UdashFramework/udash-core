package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.CrossCollections

private[properties] class ReversedReadableSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType]
) extends ForwarderWithLocalCopy[A, A, ElemType, ElemType] {

  override protected def loadFromOrigin(): BSeq[A] = origin.get.reverse
  override protected def elementsFromOrigin(): BSeq[ElemType] = origin.elemProperties.reverse
  override protected def transformPatchAndUpdateElements(patch: Patch[ElemType]): Patch[ElemType] = {
    val transPatch = Patch[ElemType](
      origin.size - patch.idx - patch.added.size,
      patch.removed.reverse,
      patch.added.reverse,
      patch.clearsProperty
    )

    CrossCollections.replaceSeq(transformedElements, transPatch.idx, transPatch.removed.length, transPatch.added)
    transPatch
  }
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
