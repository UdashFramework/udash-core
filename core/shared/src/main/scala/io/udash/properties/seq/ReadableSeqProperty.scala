package io.udash.properties.seq

import com.avsystem.commons.misc.Opt
import io.udash.properties._
import io.udash.properties.single.{AbstractReadableProperty, ReadableProperty}
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.Future

/** Read-only interface of SeqProperty[A]. */
trait ReadableSeqProperty[A, +ElemType <: ReadableProperty[A]] extends ReadableProperty[Seq[A]] {
  /** @return Sequence of child properties. */
  def elemProperties: Seq[ElemType]

  /** Registers listener, which will be called on every property structure change. */
  def listenStructure(structureListener: Patch[ElemType] => Any): Registration

  /** Returns structure listeners count. */
  def structureListenersCount(): Int

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

  /** Transforms ReadableSeqProperty[A] into ReadableSeqProperty[B].
    *
    * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableSeqProperty[A]. */
  def transform[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]]

  /** Creates `ReadableSeqProperty[A]` providing reversed order of elements from `this`. */
  def reversed(): ReadableSeqProperty[A, ReadableProperty[A]]

  /** Filters ReadableSeqProperty[A].
    *
    * @return New ReadableSeqProperty[A] with matched elements, which will be synchronised with original ReadableSeqProperty[A]. */
  def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType]

  /** Combines every element of this `SeqProperty` with provided `Property` creating new `ReadableSeqProperty` as the result. */
  def combine[B, O : PropertyCreator](property: ReadableProperty[B])(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] =
    new CombinedReadableSeqProperty(this, property, combiner)

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`. */
  def zip[B, O : PropertyCreator](
    property: ReadableSeqProperty[B, ReadableProperty[B]]
  )(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedReadableSeqProperty(this, property, combiner)

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`.
    * Uses `defaultA` and `defaultB` to fill smaller sequence. */
  def zipAll[B, O: PropertyCreator](property: ReadableSeqProperty[B, ReadableProperty[B]])(
    combiner: (A, B) => O,
    defaultA: ReadableProperty[A],
    defaultB: ReadableProperty[B]
  ): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedAllReadableSeqProperty(this, property, combiner, defaultA, defaultB)

  /** Zips elements from `this` SeqProperty with their indexes. */
  def zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]]

  override def readable: ReadableSeqProperty[A, ReadableProperty[A]]
}

private[properties] trait AbstractReadableSeqProperty[A, +ElemType <: ReadableProperty[A]]
  extends AbstractReadableProperty[Seq[A]] with ReadableSeqProperty[A, ElemType] {

  protected[this] final val structureListeners: mutable.Buffer[Patch[ElemType] => Any] = CrossCollections.createArray

  override def structureListenersCount(): Int = structureListeners.size
  protected def wrapStructureListenerRegistration(reg: Registration): Registration =
    wrapListenerRegistration(reg)

  override def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
    structureListeners += structureListener
    listenersUpdate()
    wrapStructureListenerRegistration(
      new MutableBufferRegistration(structureListeners, structureListener, Opt(listenersUpdate _))
    )
  }

  /** SeqProperty is valid if all validators return [[io.udash.properties.Valid]] and all subproperties are valid.
    *
    * @return Validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  override def isValid: Future[ValidationResult] = {
    import Validator._

    import scala.concurrent.ExecutionContext.Implicits.global

    if (validationResult == null) {
      validationResult = Future.sequence(Seq(super.isValid) ++ elemProperties.map(p => p.isValid)).foldValidationResult
    }
    validationResult
  }

  override def transform[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]] =
    new TransformedReadableSeqProperty[A, B, ReadableProperty[B], ReadableProperty[A]](this, transformer)

  override def reversed(): ReadableSeqProperty[A, ReadableProperty[A]] =
    new ReversedReadableSeqProperty[A](this)

  override def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType] =
    new FilteredSeqProperty[A, ElemType](this, matcher)

  lazy val zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]] =
    new ZippedWithIndexReadableSeqProperty[A](this)

  protected final def fireElementsListeners[ItemType <: ReadableProperty[A]](
    patch: Patch[ItemType], structureListeners: mutable.Buffer[Patch[ItemType] => Any]
  ): Unit = {
    val cpy = CrossCollections.copyArray(structureListeners)
    CallbackSequencer().queue(s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}", () => cpy.foreach(_.apply(patch)))
  }

  override lazy val readable: ReadableSeqProperty[A, ReadableProperty[A]] =
    new ReadableWrapper[A](this)
}
