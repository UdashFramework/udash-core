package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.single.{AbstractReadableProperty, ReadableProperty}
import io.udash.utils.Registration

/** Read-only interface of SeqProperty[A]. */
trait ReadableSeqProperty[+A, +ElemType <: ReadableProperty[A]] extends ReadableProperty[BSeq[A]] {
  /** @return Sequence of child properties. */
  def elemProperties: BSeq[ElemType]

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

  /** Transforms ReadableSeqProperty[A] into ReadableSeqProperty[B] element by element.
   * Prefer this to `transform` whenever you don't need the whole sequence to perform the transformation.
   *
   * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableSeqProperty[A]. */
  def transformElements[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]]

  /** Creates `ReadableSeqProperty[A]` providing reversed order of elements from `this`. */
  def reversed(): ReadableSeqProperty[A, ReadableProperty[A]]

  /** Filters ReadableSeqProperty[A].
   *
   * @return New ReadableSeqProperty[A] with matched elements, which will be synchronised with original ReadableSeqProperty[A]. */
  def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType]

  /** Combines every element of this `SeqProperty` with provided `Property` creating new `ReadableSeqProperty` as the result. */
  def combineElements[B, O](property: ReadableProperty[B])(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] =
    new CombinedReadableSeqProperty(this, property, combiner)

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`. */
  def zip[B, O](
    property: ReadableSeqProperty[B, ReadableProperty[B]]
  )(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedReadableSeqProperty(this, property, combiner, defaults = Opt.Empty)

  /** Zips elements from `this` and provided `property` by combining every pair using provided `combiner`.
   * Uses `defaultA` and `defaultB` to fill smaller sequence. */
  def zipAll[B, A1 >: A, O](property: ReadableSeqProperty[B, ReadableProperty[B]])(
    combiner: (A1, B) => O,
    defaultA: ReadableProperty[A1],
    defaultB: ReadableProperty[B]
  ): ReadableSeqProperty[O, ReadableProperty[O]] =
    new ZippedReadableSeqProperty(this, property, combiner, (defaultA, defaultB).opt)

  /** Zips elements from `this` SeqProperty with their indexes. */
  def zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]]

  override def readable: ReadableSeqProperty[A, ReadableProperty[A]]
}

private[properties] trait AbstractReadableSeqProperty[A, ElemType <: ReadableProperty[A]]
  extends AbstractReadableProperty[BSeq[A]] with ReadableSeqProperty[A, ElemType] {

  protected[this] final val structureListeners: MBuffer[Patch[ElemType] => Any] = MArrayBuffer.empty

  override final def structureListenersCount(): Int = structureListeners.size
  protected def wrapStructureListenerRegistration(reg: Registration): Registration =
    wrapListenerRegistration(reg)

  override def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
    structureListeners += structureListener
    listenersUpdate()
    wrapStructureListenerRegistration(
      new MutableBufferRegistration(structureListeners, structureListener, Opt(listenersUpdate _))
    )
  }

  override def transformElements[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]] =
    new TransformedReadableSeqProperty[A, B, ReadableProperty[B], ReadableProperty[A]](this, transformer)

  override def reversed(): ReadableSeqProperty[A, ReadableProperty[A]] =
    new ReversedReadableSeqProperty[A](this)

  override def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType] =
    new FilteredSeqProperty[A, ElemType](this, matcher)

  lazy val zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]] =
    new ZippedWithIndexReadableSeqProperty[A](this)

  protected final def fireElementsListeners(patch: Patch[ElemType]): Unit = {
    val originalListeners = structureListeners.toSet
    CallbackSequencer().queue(
      s"$hashCode:fireElementsListeners:${patch.hashCode()}",
      () => structureListeners.foreach { listener => if (originalListeners.contains(listener)) listener(patch) }
    )
  }

  override def readable: ReadableSeqProperty[A, ReadableProperty[A]] = this
}
