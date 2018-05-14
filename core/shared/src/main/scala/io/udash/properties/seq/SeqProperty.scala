package io.udash.properties.seq

import io.udash.properties._
import io.udash.properties.single.{CastableProperty, Property}

object SeqProperty {
  /** Creates an empty DirectSeqProperty[T]. */
  @deprecated("Use `SeqProperty.blank` instead.", "0.7.0")
  def empty[T : PropertyCreator](implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property.empty[Seq[T]].asSeq[T]

  /** Creates a blank DirectSeqProperty[T]. */
  def blank[T : PropertyCreator](implicit pc: PropertyCreator[Seq[T]], blank: Blank[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property.blank[Seq[T]].asSeq[T]

  /** Creates an empty DirectSeqProperty[T]. */
  @deprecated("Use `SeqProperty.empty` instead.", "0.6.0")
  def apply[T : PropertyCreator](implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    empty

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T : PropertyCreator](item: T, more: T*)(implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    apply(item +: more)

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T : PropertyCreator](init: Seq[T])(implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](init).asSeq[T]
}

trait SeqProperty[A, +ElemType <: Property[A]] extends AbstractReadableSeqProperty[A, ElemType] with Property[Seq[A]] {
  /** Replaces `amount` elements from index `idx` with provided `values`. */
  def replace(idx: Int, amount: Int, values: A*): Unit

  /** Inserts `values` on index `idx`. */
  def insert(idx: Int, values: A*): Unit = replace(idx, 0, values: _*)

  /** Removes `amount` elements starting from index `idx`. */
  def remove(idx: Int, amount: Int): Unit = replace(idx, amount)

  /** Removes first occurrence of `value`. */
  def remove(value: A): Unit = {
    val idx: Int = elemProperties.map(p => p.get).indexOf(value)
    if (idx >= 0) replace(idx, 1)
  }

  /** Adds `values` at the begging of the sequence. */
  def prepend(values: A*): Unit = insert(0, values: _*)

  /** Adds `values` at the end of the sequence. */
  def append(values: A*): Unit = insert(get.size, values: _*)

  /** Removes all elements from this SeqProperty. */
  def clear(): Unit = remove(0, size)

  /** Transforms SeqProperty[A] into SeqProperty[B].
    *
    * @return New SeqProperty[B], which will be synchronised with original SeqProperty[A]. */
  def transform[B](transformer: A => B, revert: B => A): SeqProperty[B, Property[B]] =
    new TransformedSeqProperty[A, B](this, transformer, revert)

  /** Creates `SeqProperty[A]` providing reversed order of elements from `this`. */
  override def reversed(): SeqProperty[A, Property[A]] =
    new ReversedSeqProperty[A](this)
}



