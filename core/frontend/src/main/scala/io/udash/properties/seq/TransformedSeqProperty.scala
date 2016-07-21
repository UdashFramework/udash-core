package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

import scala.concurrent.ExecutionContext

private[properties]
class TransformedReadableSeqProperty[A, B, +ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
(origin: ReadableSeqProperty[A, OrigType], transformer: A => B, override val id: UUID) extends ReadableSeqProperty[B, ElemType] {

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]

  override def elemProperties: Seq[ElemType] =
    origin.elemProperties.map(p => transformElement(p))

  override def listenStructure(l: (Patch[ElemType]) => Any): Registration =
    origin.listenStructure(patch =>
      l(Patch[ElemType](
        patch.idx,
        patch.removed.map(p => transformElement(p)),
        patch.added.map(p => transformElement(p)),
        patch.clearsProperty
      ))
    )

  override def listen(l: (Seq[B]) => Any): Registration =
    origin.listen((seq: Seq[A]) => l(seq.map(transformer)))

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()

  override def get: Seq[B] =
    origin.get.map(transformer)

  override protected[properties] def parent: ReadableProperty[_] =
    origin.parent

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()

  override implicit protected[properties] def executionContext: ExecutionContext =
    origin.executionContext
}

private[properties]
class TransformedSeqProperty[A, B](origin: SeqProperty[A, Property[A]], transformer: A => B, revert: B => A, override val id: UUID)
  extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer, id) with SeqProperty[B, Property[B]] {

    override protected def transformElement(el: Property[A]): Property[B] =
      el.transform(transformer, revert)

    override def replace(idx: Int, amount: Int, values: B*): Unit =
      origin.replace(idx, amount, values.map(revert): _*)

    override def set(t: Seq[B]): Unit =
      origin.set(t.map(revert))

    override def setInitValue(t: Seq[B]): Unit =
      origin.setInitValue(t.map(revert))
  }
