package io.udash.properties.seq

import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.CrossCollections

private[properties] class ReversedReadableSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType]
) extends ForwarderWithLocalCopy[A, A, ElemType, ElemType] {

  override protected def loadFromOrigin(): Seq[A] = origin.get.reverse
  override protected def elementsFromOrigin(): Seq[ElemType] = origin.elemProperties.reverse
  override protected def transformPatchAndUpdateElements(patch: Patch[ElemType]): Patch[ElemType] = {
    val transPatch = Patch[ElemType](
      origin.size - patch.idx - patch.added.size,
      patch.removed.reverse,
      patch.added.reverse,
      patch.clearsProperty
    )

    CrossCollections.replace(transformedElements, transPatch.idx, transPatch.removed.length, transPatch.added: _*)
    transPatch
  }
}

private[properties] final class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends ReversedReadableSeqProperty[A, Property[A]](origin) with AbstractSeqProperty[A, Property[A]] {

  override def setInitValue(t: Seq[A]): Unit =
    origin.setInitValue(t.reverse)

  override def set(t: Seq[A], force: Boolean = false): Unit =
    origin.set(t.reverse, force)

  override def touch(): Unit =
    origin.touch()

    override def replace(idx: Int, amount: Int, values: A*): Unit =
      origin.replace(origin.size - idx, amount, values.reverse: _*)
  }
