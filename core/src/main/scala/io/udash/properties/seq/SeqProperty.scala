package io.udash.properties.seq

import io.udash.properties._
import io.udash.properties.single.{AbstractProperty, CastableProperty, Property}

object SeqProperty {
  /** Creates a blank DirectSeqProperty[T]. */
  def blank[T](implicit pc: SeqPropertyCreator[T], blank: Blank[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property.blank[Seq[T]](pc, blank).asSeq[T]

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T: SeqPropertyCreator](item: T, more: T*): SeqProperty[T, CastableProperty[T]] =
    apply(item +: more)

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T](init: Seq[T])(implicit pc: SeqPropertyCreator[T]): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](init)(pc).asSeq[T]
}

trait SeqProperty[A, +ElemType <: Property[A]] extends ReadableSeqProperty[A, ElemType] with Property[Seq[A]] {
  /** Replaces `amount` elements from index `idx` with provided `values`. */
  def replace(idx: Int, amount: Int, values: A*): Unit

  /** Inserts `values` on index `idx`. */
  def insert(idx: Int, values: A*): Unit

  /** Removes `amount` elements starting from index `idx`. */
  def remove(idx: Int, amount: Int): Unit

  /** Removes first occurrence of `value`. */
  def remove(value: A): Unit

  /** Adds `values` at the begging of the sequence. */
  def prepend(values: A*): Unit

  /** Adds `values` at the end of the sequence. */
  def append(values: A*): Unit

  /** Removes all elements from this SeqProperty. */
  def clear(): Unit

  /** Transforms SeqProperty[A] into SeqProperty[B].
    *
    * @return New SeqProperty[B], which will be synchronised with original SeqProperty[A]. */
  def transform[B](transformer: A => B, revert: B => A): SeqProperty[B, Property[B]]

  /** Creates `SeqProperty[A]` providing reversed order of elements from `this`. */
  override def reversed(): SeqProperty[A, Property[A]]
}

private[properties] trait AbstractSeqProperty[A, +ElemType <: Property[A]]
  extends AbstractReadableSeqProperty[A, ElemType] with AbstractProperty[Seq[A]] with SeqProperty[A, ElemType] {

  def insert(idx: Int, values: A*): Unit = replace(idx, 0, values: _*)

  def remove(idx: Int, amount: Int): Unit = replace(idx, amount)

  def remove(value: A): Unit = {
    val idx: Int = elemProperties.map(p => p.get).indexOf(value)
    if (idx >= 0) replace(idx, 1)
  }

  def prepend(values: A*): Unit = insert(0, values: _*)

  def append(values: A*): Unit = insert(get.size, values: _*)

  def clear(): Unit = remove(0, size)

  override def clearListeners(): Unit = {
    super.clearListeners()
    structureListeners.clear()
  }

  def transform[B](transformer: A => B, revert: B => A): SeqProperty[B, Property[B]] =
    new TransformedSeqProperty[A, B](this, transformer, revert)

  override def reversed(): SeqProperty[A, Property[A]] =
    new ReversedSeqProperty[A](this)
}
