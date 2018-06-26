package io.udash.properties.seq

import io.udash.properties.CrossCollections
import io.udash.properties.single.{Property, ReadableProperty}

private[properties] abstract class BaseReversedSeqProperty[A, ElemType <: ReadableProperty[A], OriginType <: ReadableSeqProperty[A, ElemType]](
  override protected val origin: OriginType
) extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] with ForwarderWithLocalCopy[A, A, ElemType, ElemType] {

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

private[properties] class ReversedReadableSeqProperty[A](origin: ReadableSeqProperty[A, ReadableProperty[A]])
  extends BaseReversedSeqProperty[A, ReadableProperty[A], ReadableSeqProperty[A, ReadableProperty[A]]](origin)

private[properties] class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends BaseReversedSeqProperty[A, Property[A], SeqProperty[A, Property[A]]](origin)
    with ForwarderSeqProperty[A, A, Property[A], Property[A]]
    with AbstractSeqProperty[A, Property[A]] {

    override def setInitValue(t: Seq[A]): Unit =
      origin.setInitValue(t.reverse)

    override def set(t: Seq[A], force: Boolean = false): Unit =
      origin.set(t.reverse, force)

    override def touch(): Unit =
      origin.touch()

    override def replace(idx: Int, amount: Int, values: A*): Unit =
      origin.replace(origin.size - idx, amount, values.reverse: _*)
  }
