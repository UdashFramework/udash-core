package io.udash.properties.seq

import java.util.UUID

import io.udash.properties._
import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}
import io.udash.utils.{Registration, SetRegistration}

import scala.collection.mutable
import scala.concurrent.Future

object SeqProperty {
  /** Creates an empty DirectSeqProperty[T]. */
  def empty[T](implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property.empty[Seq[T]].asSeq[T]

  /** Creates an empty DirectSeqProperty[T]. */
  def apply[T](implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    empty

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T](item: T, more: T*)(implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    apply(item +: more)

  /** Creates a DirectSeqProperty[T] with initial value. */
  def apply[T](init: Seq[T])(implicit pc: PropertyCreator[Seq[T]]): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](init).asSeq[T]
}

/** Read-only interface of SeqProperty[A]. */
trait ReadableSeqProperty[A, +ElemType <: ReadableProperty[A]] extends ReadableProperty[Seq[A]] {
  /** @return Sequence of child properties. */
  def elemProperties: Seq[ElemType]

  /** Registers listener, which will be called on every property structure change. */
  def listenStructure(structureListener: Patch[ElemType] => Any): Registration

  /** SeqProperty is valid if all validators return [[io.udash.properties.Valid]] and all subproperties are valid.
    *
    * @return Validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  override def isValid: Future[ValidationResult] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import Validator._

    if (validationResult == null) {
      validationResult = Future.sequence(Seq(super.isValid) ++ elemProperties.map(p => p.isValid)).foldValidationResult
    }
    validationResult
  }

  /** Transforms ReadableSeqProperty[A] into ReadableSeqProperty[B].
    *
    * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableSeqProperty[A]. */
  def transform[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]] =
    new TransformedReadableSeqProperty[A, B, ReadableProperty[B], ReadableProperty[A]](this, transformer)

  /** Creates `ReadableSeqProperty[A]` providing reversed order of elements from `this`. */
  def reversed(): ReadableSeqProperty[A, ReadableProperty[A]] =
    new ReversedReadableSeqProperty[A](this)

  /** Filters ReadableSeqProperty[A].
    *
    * @return New ReadableSeqProperty[A] with matched elements, which will be synchronised with original ReadableSeqProperty[A]. */
  def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType] =
    new FilteredSeqProperty[A, ElemType](this, matcher)

  /** Combines every element of this `SeqProperty` with provided `Property` creating new `ReadableSeqProperty` as the result. */
  def combine[B, O](property: ReadableProperty[B])(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] = {
    class CombinedReadableSeqProperty(s: ReadableSeqProperty[A, _ <: ReadableProperty[A]], p: ReadableProperty[B])
      extends ReadableSeqProperty[O, ReadableProperty[O]] {

      override val id: UUID = PropertyCreator.newID()
      override protected[properties] val parent: ReadableProperty[_] = null

      private val children = CrossCollections.createArray[ReadableProperty[O]]
      private val structureListeners = mutable.Set.empty[Patch[ReadableProperty[O]] => Any]

      s.elemProperties.foreach(c => children.+=(c.combine(p, this)(combiner)))
      s.listenStructure(patch => {
        val added = patch.added.map(c => c.combine(p, this)(combiner))
        val removed = CrossCollections.slice(children, patch.idx, patch.idx + patch.removed.size)
        CrossCollections.replace(children, patch.idx, patch.removed.size, added: _*)
        val mappedPatch = Patch(patch.idx, removed, added, patch.clearsProperty)
        CallbackSequencer.queue(
          s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
          () => structureListeners.foreach(_.apply(mappedPatch))
        )
        valueChanged()
      })

      /** @return Current property value. */
      override def get: Seq[O] =
        children.map(_.get)

      /** @return Sequence of child properties. */
      override def elemProperties: Seq[ReadableProperty[O]] =
        children

      /** Registers listener, which will be called on every property structure change. */
      override def listenStructure(structureListener: (Patch[ReadableProperty[O]]) => Any): Registration = {
        structureListeners += structureListener
        new SetRegistration(structureListeners, structureListener)
      }
    }

    new CombinedReadableSeqProperty(this, property)
  }

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`. */
  def zip[B, O](property: ReadableSeqProperty[B, ReadableProperty[B]])(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedReadableSeqProperty(this, property, combiner)

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`.
    * Uses `defaultA` and `defaultB` to fill smaller sequence. */
  def zipAll[B, O](property: ReadableSeqProperty[B, ReadableProperty[B]])
                  (combiner: (A, B) => O,
                   defaultA: ReadableProperty[A],
                   defaultB: ReadableProperty[B]): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedAllReadableSeqProperty(this, property, combiner, defaultA, defaultB)

  /** Zips elements from `this` SeqProperty with their indexes. */
  lazy val zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]] =
    new ZippedWithIndexReadableSeqProperty[A](this)

  /** The size of this sequence, equivalent to length. */
  def size: Int =
    elemProperties.size

  /** The size of this sequence. */
  def length: Int = size

  /** Tests whether this traversable collection is empty. */
  def isEmpty: Boolean =
    elemProperties.isEmpty

  /** Tests whether this traversable collection is not empty. */
  def nonEmpty: Boolean =
    elemProperties.nonEmpty

  protected final def fireElementsListeners[ItemType <: ReadableProperty[A]](
    patch: Patch[ItemType], structureListeners: CrossCollections.Array[(Patch[ItemType]) => Any]
  ): Unit = {
    val cpy = CrossCollections.copyArray(structureListeners)
    CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}", () => cpy.foreach(_.apply(patch)))
  }
}

trait SeqProperty[A, +ElemType <: Property[A]] extends ReadableSeqProperty[A, ElemType] with Property[Seq[A]] {
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



