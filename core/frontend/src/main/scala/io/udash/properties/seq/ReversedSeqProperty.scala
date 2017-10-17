package io.udash.properties.seq

import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

private[properties]
abstract class BaseReversedSeqProperty[A, +ElemType <: ReadableProperty[A], OriginType <: ReadableSeqProperty[A, ElemType]]
                                      (override protected val origin: OriginType)
  extends ForwarderReadableSeqProperty[A, ReadableProperty[A]] {

  override def get: Seq[A] =
    origin.get.reverse

  override def elemProperties: Seq[ElemType] =
    origin.elemProperties.reverse

  override def listen(valueListener: (Seq[A]) => Any): Registration =
    origin.listen(s => valueListener(s.reverse))

  override def listenOnce(valueListener: (Seq[A]) => Any): Registration =
    origin.listenOnce(s => valueListener(s.reverse))

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()
}

private[properties]
class ReversedReadableSeqProperty[A](origin: ReadableSeqProperty[A, ReadableProperty[A]])
  extends BaseReversedSeqProperty[A, ReadableProperty[A], ReadableSeqProperty[A, ReadableProperty[A]]](origin) {

  override def listenStructure(structureListener: (Patch[ReadableProperty[A]]) => Any): Registration =
    origin.listenStructure((patch) => structureListener(patch.copy(
      idx = origin.size - patch.idx - patch.added.size,
      removed = patch.removed.reverse,
      added = patch.added.reverse
    )))
}

private[properties]
class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends BaseReversedSeqProperty[A, Property[A], SeqProperty[A, Property[A]]](origin) with ForwarderSeqProperty[A, Property[A]] {

    override def setInitValue(t: Seq[A]): Unit =
      origin.setInitValue(t.reverse)

    override def set(t: Seq[A], force: Boolean = false): Unit =
      origin.set(t.reverse, force)

    override def touch(): Unit =
      origin.touch()

    override def replace(idx: Int, amount: Int, values: A*): Unit =
      origin.replace(origin.size - idx, amount, values.reverse:_*)

    override def listenStructure(structureListener: (Patch[Property[A]]) => Any): Registration =
      origin.listenStructure((patch) => structureListener(patch.copy(
        idx = origin.size - patch.idx - patch.added.size,
        removed = patch.removed.reverse,
        added = patch.added.reverse
      )))
  }
