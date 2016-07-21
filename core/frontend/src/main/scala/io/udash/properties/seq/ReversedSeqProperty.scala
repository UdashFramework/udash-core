package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.PropertyCreator
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

import scala.concurrent.ExecutionContext

private[properties]
abstract class BaseReversedSeqProperty[A, +ElemType <: ReadableProperty[A], OriginType <: ReadableSeqProperty[A, ElemType]](origin: OriginType)
  extends ReadableSeqProperty[A, ReadableProperty[A]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null
  override implicit protected[properties] def executionContext: ExecutionContext = origin.executionContext

  override def get: Seq[A] =
    origin.get.reverse

  override def elemProperties: Seq[ElemType] =
    origin.elemProperties.reverse

  override def listen(l: (Seq[A]) => Any): Registration =
    origin.listen(s => l(s.reverse))
}

private[properties]
class ReversedReadableSeqProperty[A](origin: ReadableSeqProperty[A, ReadableProperty[A]])
  extends BaseReversedSeqProperty[A, ReadableProperty[A], ReadableSeqProperty[A, ReadableProperty[A]]](origin) {

  override def listenStructure(l: (Patch[ReadableProperty[A]]) => Any): Registration =
    origin.listenStructure((patch) => l(patch.copy(
      idx = origin.size - patch.idx - patch.added.size,
      removed = patch.removed.reverse,
      added = patch.added.reverse
    )))
}

private[properties]
class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends BaseReversedSeqProperty[A, Property[A], SeqProperty[A, Property[A]]](origin) with SeqProperty[A, Property[A]] {

    override def setInitValue(t: Seq[A]): Unit =
      origin.setInitValue(t.reverse)

    override def set(t: Seq[A]): Unit =
      origin.set(t.reverse)

    override def replace(idx: Int, amount: Int, values: A*): Unit =
      origin.replace(origin.size - idx, amount, values.reverse:_*)

    override def listenStructure(l: (Patch[Property[A]]) => Any): Registration =
      origin.listenStructure((patch) => l(patch.copy(
        idx = origin.size - patch.idx - patch.added.size,
        removed = patch.removed.reverse,
        added = patch.added.reverse
      )))
  }
