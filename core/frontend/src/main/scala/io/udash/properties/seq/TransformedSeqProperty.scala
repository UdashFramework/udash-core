package io.udash.properties.seq

import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

private[properties]
class TransformedReadableSeqProperty[A, B, +ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
                                    (override protected val origin: ReadableSeqProperty[A, OrigType], transformer: A => B)
  extends ForwarderReadableSeqProperty[B, ElemType] {

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]

  override def get: Seq[B] =
    origin.get.map(transformer)

  override def elemProperties: Seq[ElemType] =
    origin.elemProperties.map(p => transformElement(p))

  override def listenStructure(structureListener: (Patch[ElemType]) => Any): Registration =
    origin.listenStructure(patch =>
      structureListener(Patch[ElemType](
        patch.idx,
        patch.removed.map(p => transformElement(p)),
        patch.added.map(p => transformElement(p)),
        patch.clearsProperty
      ))
    )

  override def listen(valueListener: (Seq[B]) => Any): Registration =
    origin.listen((seq: Seq[A]) => valueListener(seq.map(transformer)))

  override def listenOnce(valueListener: (Seq[B]) => Any): Registration =
    origin.listenOnce((seq: Seq[A]) => valueListener(seq.map(transformer)))

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()
}

private[properties]
class TransformedSeqProperty[A, B](override protected val origin: SeqProperty[A, Property[A]],
                                   transformer: A => B, revert: B => A)
  extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer) with ForwarderSeqProperty[B, Property[B]] {

  override protected def transformElement(el: Property[A]): Property[B] =
    el.transform(transformer, revert)

  override def replace(idx: Int, amount: Int, values: B*): Unit =
    origin.replace(idx, amount, values.map(revert): _*)

  override def set(t: Seq[B], force: Boolean = false): Unit =
    origin.set(t.map(revert), force)

  override def setInitValue(t: Seq[B]): Unit =
    origin.setInitValue(t.map(revert))

  override def touch(): Unit =
    origin.touch()
}
