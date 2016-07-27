package io.udash.properties

import java.util.UUID

import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Describes changes in SeqProperty structure.
  *
  * @param idx Index where changes starts.
  * @param removed Properties removed from index `idx`.
  * @param added Properties added on index `idx`.
  * @tparam P Contained properties type.
  */
case class Patch[+P <: ReadableProperty[_]](idx: Int, removed: Seq[P], added: Seq[P], clearsProperty: Boolean)

object SeqProperty {
  /** Creates empty DirectSeqProperty[T]. */
  def apply[T](implicit pc: PropertyCreator[Seq[T]], ev: ModelSeq[Seq[T]], ec: ExecutionContext): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]].asSeq[T]

  /** Creates DirectSeqProperty[T] with initial value. */
  def apply[T](item: T, more: T*)(implicit pc: PropertyCreator[Seq[T]], ev: ModelSeq[Seq[T]], ec: ExecutionContext): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](item +: more).asSeq[T]

  /** Creates DirectSeqProperty[T] with initial value. */
  def apply[T](init: Seq[T])(implicit pc: PropertyCreator[Seq[T]], ev: ModelSeq[Seq[T]], ec: ExecutionContext): SeqProperty[T, CastableProperty[T]] =
    Property[Seq[T]](init).asSeq[T]
}

/** Read-only interface of SeqProperty[A]. */
trait ReadableSeqProperty[A, +ElemType <: ReadableProperty[A]] extends ReadableProperty[Seq[A]] {
  /** @return Sequence of child properties. */
  def elemProperties: Seq[ElemType]

  /** Registers listener, which will be called on every property structure change. */
  def listenStructure(l: Patch[ElemType] => Any): Registration

  /** SeqProperty is valid if all validators return [[io.udash.properties.Valid]] and all subproperties are valid.
    *
    * @return Validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  override def isValid: Future[ValidationResult] = {
    import Validator._
    Future.sequence(Seq(super.isValid) ++ elemProperties.map(p => p.isValid)).foldValidationResult
  }

  /** Transforms ReadableSeqProperty[A] into ReadableSeqProperty[B].
    *
    * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableSeqProperty[A]. */
  def transform[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]] =
    new TransformedReadableSeqProperty[A, B, ReadableProperty[B], ReadableProperty[A]](this, transformer, PropertyCreator.newID())

  /** Creates `ReadableSeqProperty[A]` providing reversed order of elements from `this`. */
  def reversed(): ReadableSeqProperty[A, ReadableProperty[A]] =
    new ReversedReadableSeqProperty[A](this)

  /** Filters ReadableSeqProperty[A].
    *
    * @return New ReadableSeqProperty[A] with matched elements, which will be synchronised with original ReadableSeqProperty[A]. */
  def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ElemType] =
    new FilteredSeqProperty[A, ElemType](this, matcher, PropertyCreator.newID())

  /** Combines every element of this `SeqProperty` with provided `Property` creating new `ReadableSeqProperty` as the result. */
  def combine[B, O : ModelValue](property: ReadableProperty[B])(combiner: (A, B) => O): ReadableSeqProperty[O, ReadableProperty[O]] = {
    class CombinedReadableSeqProperty(s: ReadableSeqProperty[A, _ <: ReadableProperty[A]],
                                      p: ReadableProperty[B], override val executionContext: ExecutionContext)
      extends ReadableSeqProperty[O, ReadableProperty[O]] {

      override val id: UUID = PropertyCreator.newID()
      override protected[properties] val parent: ReadableProperty[_] = null

      private val children = mutable.ListBuffer.empty[ReadableProperty[O]]
      private val structureListeners: mutable.Set[Patch[ReadableProperty[O]] => Any] = mutable.Set()

      s.elemProperties.foreach(c => children.append(c.combine(p, this)(combiner)))
      s.listenStructure(patch => {
        val added = patch.added.map(c => c.combine(p, this)(combiner))
        val removed = children.slice(patch.idx, patch.idx + patch.removed.size)
        children.remove(patch.idx, patch.removed.size)
        children.insertAll(patch.idx, added)
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
      override def listenStructure(l: (Patch[ReadableProperty[O]]) => Any): Registration = {
        structureListeners += l
        new PropertyRegistration(structureListeners, l)
      }
    }

    new CombinedReadableSeqProperty(this, property, executionContext)
  }

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
}

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

abstract class BaseReversedSeqProperty[A, +ElemType <: ReadableProperty[A], OriginType <: ReadableSeqProperty[A, ElemType]](origin: OriginType)
  extends ReadableSeqProperty[A, ReadableProperty[A]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null
  override implicit protected[properties] def executionContext: ExecutionContext = origin.executionContext

  override def get: Seq[A] =
    origin.get.reverse

  override def elemProperties: Seq[ElemType] =
    origin.elemProperties.reverse

  override def listen(l: (Seq[A]) => Any): Registration =
    origin.listen(s => l(s.reverse))
}

class ReversedReadableSeqProperty[A](origin: ReadableSeqProperty[A, ReadableProperty[A]])
  extends BaseReversedSeqProperty[A, ReadableProperty[A], ReadableSeqProperty[A, ReadableProperty[A]]](origin) {

  override def listenStructure(l: (Patch[ReadableProperty[A]]) => Any): Registration =
    origin.listenStructure((patch) => l(patch.copy(
      idx = origin.size - patch.idx - patch.added.size,
      removed = patch.removed.reverse,
      added = patch.added.reverse
    )))
}

class ReversedSeqProperty[A](origin: SeqProperty[A, Property[A]])
  extends BaseReversedSeqProperty[A, Property[A], SeqProperty[A, Property[A]]](origin) with SeqProperty[A, Property[A]] {

  override def setInitValue(t: Seq[A]): Unit =
    origin.setInitValue(t.reverse)

  override def set(t: Seq[A]): Unit =
    origin.set(t.reverse)

  override def replace(idx: Int, amount: Int, values: A*): Unit =
    origin.replace(origin.size - idx, amount, values.reverse:_*)

  override def listenStructure(l: (Patch[Property[A]]) => Any): Registration =
    origin.listenStructure((patch) => l(patch.copy(
      idx = origin.size - patch.idx - patch.added.size,
      removed = patch.removed.reverse,
      added = patch.added.reverse
    )))
}

class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]]
  (origin: ReadableSeqProperty[A, _ <: ElemType],
   matcher: A => Boolean,
   override val id: UUID) extends ReadableSeqProperty[A, ElemType] {

  private def loadPropsFromOrigin() =
    origin.elemProperties.filter(el => matcher(el.get))

  private var filteredProps: Seq[ElemType] = loadPropsFromOrigin()
  private var filteredValues: Seq[A] = filteredProps.map(_.get)

  private val filteredListeners: mutable.Set[(Seq[A]) => Any] = mutable.Set.empty
  private val structureListeners: mutable.Set[(Patch[ElemType]) => Any] = mutable.Set.empty

  private def elementChanged(p: ElemType)(v: A): Unit = {
    val props = loadPropsFromOrigin()
    val oldIdx = filteredProps.indexOf(p)
    val newIdx = props.indexOf(p)

    val patch = (oldIdx, newIdx) match {
      case (oi, -1) if oi != -1 =>
        filteredProps = filteredProps.slice(0, oi) ++ filteredProps.slice(oi+1, filteredProps.size)
        filteredValues = filteredProps.map(_.get)
        Patch[ElemType](oi, Seq(p), Seq.empty, filteredProps.isEmpty)
      case (-1, ni) if ni != -1 =>
        filteredProps = (filteredProps.slice(0, ni) :+ p) ++ filteredProps.slice(ni, filteredProps.size)
        filteredValues = filteredProps.map(_.get)
        Patch[ElemType](ni, Seq.empty, Seq(p), filteredProps.isEmpty)
      case _ => null
    }

    if (oldIdx != newIdx || oldIdx != -1) {
      val callbackProps = props.map(_.get)
      CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => filteredListeners.foreach(_.apply(callbackProps)))
    }

    if (patch != null)
      CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${p.id}", () => structureListeners.foreach(_.apply(patch)))
  }

  private val registrations = mutable.HashMap.empty[ElemType, Registration]

  origin.elemProperties.foreach(p => registrations(p) = p.listen(elementChanged(p)))
  origin.listenStructure(patch => {
    patch.removed.foreach(p => if (registrations.contains(p)) {
      registrations(p).cancel()
      registrations.remove(p)
    })
    patch.added.foreach(p => registrations(p) = p.listen(elementChanged(p)))

    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get))
    if (added.nonEmpty || removed.nonEmpty) {
      val props = loadPropsFromOrigin()
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      val callbackProps = props.map(_.get)

      filteredProps = filteredProps.slice(0, idx) ++ added ++ filteredProps.slice(idx + removed.size, filteredProps.size)
      filteredValues = filteredProps.map(_.get)

      val filteredPatch = Patch[ElemType](idx, removed, added, filteredProps.isEmpty)

      CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => filteredListeners.foreach(_.apply(callbackProps)))
      CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}", () => structureListeners.foreach(_.apply(filteredPatch)))
    }
  })

  override def listen(l: (Seq[A]) => Any): Registration = {
    filteredListeners.add(l)
    new Registration {
      override def cancel(): Unit = filteredListeners.remove(l)
    }
  }

  override def listenStructure(l: (Patch[ElemType]) => Any): Registration = {
    structureListeners.add(l)
    new PropertyRegistration(structureListeners, l)
  }

  override def elemProperties: Seq[ElemType] =
    filteredProps

  override def get: Seq[A] =
    filteredValues

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()

  override protected[properties] def parent: ReadableProperty[_] =
    origin.parent

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()

  override implicit protected[properties] def executionContext: ExecutionContext =
    origin.executionContext
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
    new TransformedSeqProperty[A, B](this, transformer, revert, PropertyCreator.newID())

  /** Creates `SeqProperty[A]` providing reversed order of elements from `this`. */
  override def reversed(): SeqProperty[A, Property[A]] =
    new ReversedSeqProperty[A](this)
}

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

class DirectSeqPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                              (implicit propertyCreator: PropertyCreator[A],
                               val executionContext: ExecutionContext) extends SeqProperty[A, CastableProperty[A]] with CastableProperty[Seq[A]] {

  private val properties = mutable.ListBuffer[CastableProperty[A]]()
  private val structureListeners: mutable.Set[Patch[CastableProperty[A]] => Any] = mutable.Set()

  override def elemProperties: Seq[CastableProperty[A]] =
    properties

  override def replace(idx: Int, amount: Int, values: A*): Unit = {
    val oldProperties = properties.slice(idx, idx + amount)
    val newProperties = if (values != null) values.map(value => propertyCreator.newProperty(value, this)) else Seq.empty
    properties.remove(idx, amount)
    properties.insertAll(idx, newProperties)

    fireElementsListeners(Patch(idx, oldProperties, newProperties, properties.isEmpty))
    valueChanged()
  }

  override def set(t: Seq[A]): Unit =
    replace(0, properties.size, t: _*)

  override def setInitValue(t: Seq[A]): Unit = {
    val newProperties = t.map(value => propertyCreator.newProperty(value, this))
    properties.insertAll(0, newProperties)
  }

  def get: Seq[A] =
    properties.map(_.get)

  override def listenStructure(l: (Patch[CastableProperty[A]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }

  protected def fireElementsListeners(patch: Patch[CastableProperty[A]]): Unit =
    CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}", () => structureListeners.foreach(_.apply(patch)))
}