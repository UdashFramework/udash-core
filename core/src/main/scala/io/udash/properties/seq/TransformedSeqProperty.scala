package io.udash.properties.seq

import com.avsystem.commons.misc.Opt
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.CrossCollections

private[properties] class TransformedReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, OrigType], transformer: A => B
) extends ForwarderWithLocalCopy[A, B, ElemType, OrigType] {

  override protected def loadFromOrigin(): Seq[B] = origin.get.map(transformer)
  override protected def elementsFromOrigin(): Seq[ElemType] = origin.elemProperties.map(transformElement)
  override protected def transformPatchAndUpdateElements(patch: Patch[OrigType]): Opt[Patch[ElemType]] = {
    val transPatch = Patch[ElemType](
      patch.idx,
      transformedElements.slice(patch.idx, patch.idx + patch.removed.size),
      patch.added.map(transformElement),
      patch.clearsProperty
    )

    CrossCollections.replace(transformedElements, patch.idx, patch.removed.length, transPatch.added: _*)
    transPatch.opt
  }

  override def originListener(originValue: Seq[A]): Unit = fireValueListeners() //could be optimized

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]
}

private[properties] class TransformedSeqProperty[A, B](
  override protected val origin: SeqProperty[A, Property[A]],
  transformer: A => B, revert: B => A
) extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer)
    with AbstractSeqProperty[B, Property[B]] {

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
