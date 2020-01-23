package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.single.{AbstractProperty, CastableProperty, Property}

object SeqProperty {
  /** Creates a blank DirectSeqProperty[T]. */
  def blank[T](implicit pc: SeqPropertyCreator[T, Seq], blank: Blank[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property.blank[Seq[T]](pc, blank).asSeq[T]

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T](item: T, more: T*)(implicit pc: SeqPropertyCreator[T, Seq]): SeqProperty[T, CastableProperty[T]] =
    apply(item +: more)

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T](init: Seq[T])(implicit pc: SeqPropertyCreator[T, Seq]): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](init)(pc).asSeq[T]
}

trait SeqProperty[A, +ElemType <: Property[A]] extends ReadableSeqProperty[A, ElemType] with Property[BSeq[A]] {
  /** Replaces `amount` elements from index `idx` with provided `values`. */
  def replaceSeq(idx: Int, amount: Int, values: BSeq[A]): Unit
  final def replace(idx: Int, amount: Int, values: A*): Unit = replaceSeq(idx, amount, values)

  /** Inserts `values` on index `idx`. */
  def insertSeq(idx: Int, values: BSeq[A]): Unit
  final def insert(idx: Int, values: A*): Unit = insertSeq(idx, values)

  /** Removes `amount` elements starting from index `idx`. */
  def remove(idx: Int, amount: Int): Unit

  /** Removes first occurrence of `value`. */
  def remove(value: A): Unit

  /** Adds `values` at the begging of the sequence. */
  def prependSeq(values: BSeq[A]): Unit
  final def prepend(values: A*): Unit = prependSeq(values)

  /** Adds `values` at the end of the sequence. */
  def appendSeq(values: BSeq[A]): Unit
  final def append(values: A*): Unit = appendSeq(values)

  /** Removes all elements from this SeqProperty. */
  def clear(): Unit

  /** Creates SeqProperty[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
   * Prefer this to `bitransform` whenever you don't need the whole sequence to perform the transformation.
   *
   * @return New SeqProperty[B], which will be synchronised with original SeqProperty[A]. */
  def bitransformElements[B](transformer: A => B)(revert: B => A): SeqProperty[B, Property[B]]

  /** Creates `SeqProperty[A]` providing reversed order of elements from `this`. */
  override def reversed(): SeqProperty[A, Property[A]]
}

private[properties] trait AbstractSeqProperty[A, ElemType <: Property[A]]
  extends AbstractReadableSeqProperty[A, ElemType] with AbstractProperty[BSeq[A]] with SeqProperty[A, ElemType] {

  def insertSeq(idx: Int, values: BSeq[A]): Unit = replaceSeq(idx, 0, values)

  def remove(idx: Int, amount: Int): Unit = replace(idx, amount)

  def remove(value: A): Unit = {
    val idx: Int = elemProperties.map(p => p.get).indexOf(value)
    if (idx >= 0) replace(idx, 1)
  }

  def prependSeq(values: BSeq[A]): Unit = insertSeq(0, values)

  def appendSeq(values: BSeq[A]): Unit = insertSeq(get.size, values)

  def clear(): Unit = remove(0, size)

  override def clearListeners(): Unit = {
    super.clearListeners()
    structureListeners.clear()
  }

  override def bitransformElements[B](transformer: A => B)(revert: B => A): SeqProperty[B, Property[B]] =
    new TransformedSeqProperty[A, B](this, transformer, revert)

  override def reversed(): SeqProperty[A, Property[A]] =
    new ReversedSeqProperty[A](this)
}
