package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.CrossCollections

private[properties] class TransformedReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, OrigType], transformer: A => B
) extends ForwarderReadableSeqProperty[A, B, ElemType, OrigType] {

  override protected def getFromOrigin(): BSeq[B] = origin.get.map(transformer)
  override protected def transformElements(elemProperties: BSeq[OrigType]): BSeq[ElemType] = elemProperties.map(transformElement)
  override protected def transformPatchAndUpdateElements(patch: Patch[OrigType]): Opt[Patch[ElemType]] = {
    val transPatch = Patch[ElemType](
      patch.idx,
      transformedElements.slice(patch.idx, patch.idx + patch.removed.size).toSeq,
      patch.added.map(transformElement),
    )

    CrossCollections.replaceSeq(transformedElements, patch.idx, patch.removed.length, transPatch.added)
    transPatch.opt
  }

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]
}

private[properties] final class TransformedSeqProperty[A, B](
  origin: SeqProperty[A, Property[A]],
  transformer: A => B, revert: B => A
) extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer)
  with AbstractSeqProperty[B, Property[B]] {

  override protected def transformElement(el: Property[A]): Property[B] =
    el.bitransform(transformer)(revert)

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[B]): Unit =
    origin.replaceSeq(idx, amount, values.map(revert))

  override def set(t: BSeq[B], force: Boolean = false): Unit =
    origin.set(t.map(revert), force)

  override def setInitValue(t: BSeq[B]): Unit =
    origin.setInitValue(t.map(revert))

  override def touch(): Unit =
    origin.touch()
}
