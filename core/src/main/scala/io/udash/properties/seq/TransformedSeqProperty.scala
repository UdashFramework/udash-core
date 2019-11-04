package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.CrossCollections

private[properties] class TransformedReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, OrigType], transformer: A => B
) extends ForwarderWithLocalCopy[A, B, ElemType, OrigType] {

  private var lastValue: BSeq[A] = _
  private var transformedLastValue: BSeq[B] = _

  override protected def loadFromOrigin(): BSeq[B] = {
    if (origin.size != transformedElements.length || origin.get != lastValue) {
      lastValue = origin.get
      transformedLastValue = lastValue.map(transformer)
    }
    transformedLastValue
  }
  override protected def elementsFromOrigin(): BSeq[ElemType] = origin.elemProperties.map(transformElement)
  override protected def transformPatchAndUpdateElements(patch: Patch[OrigType]): Patch[ElemType] = {
    val transPatch = Patch[ElemType](
      patch.idx,
      patch.removed.map(transformElement),
      patch.added.map(transformElement),
      patch.clearsProperty
    )

    CrossCollections.replaceSeq(transformedElements, patch.idx, patch.removed.length, transPatch.added)
    transPatch
  }

  override protected def onListenerInit(): Unit = {
    lastValue = Seq.empty
    transformedLastValue = Seq.empty
    super.onListenerInit()
  }

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]
}

private[properties] class TransformedSeqProperty[A, B](
  override protected val origin: SeqProperty[A, Property[A]],
  transformer: A => B, revert: B => A
) extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer)
    with ForwarderSeqProperty[A, B, Property[B], Property[A]]
    with AbstractSeqProperty[B, Property[B]] {

  override protected def transformElement(el: Property[A]): Property[B] =
    el.transform(transformer, revert)

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[B]): Unit =
    origin.replaceSeq(idx, amount, values.map(revert))

  override def set(t: BSeq[B], force: Boolean = false): Unit =
    origin.set(t.map(revert), force)

  override def setInitValue(t: BSeq[B]): Unit =
    origin.setInitValue(t.map(revert))

  override def touch(): Unit =
    origin.touch()
}
