package io.udash.properties.single

import java.util.UUID

import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, ReadableSeqPropertyFromSingleValue, SeqProperty, SeqPropertyFromSingleValue}
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

object Property {
  /** Creates empty DirectProperty[T]. */
  def apply[T](implicit pc: PropertyCreator[T], ec: ExecutionContext): CastableProperty[T] =
    pc.newProperty(null)

  /** Creates DirectProperty[T] with initial value. */
  def apply[T](init: T)(implicit pc: PropertyCreator[T], ec: ExecutionContext): CastableProperty[T]=
    pc.newProperty(init, null)
}

/** Base interface of every Property in Udash. */
trait ReadableProperty[A] {
  protected[this] val listeners: mutable.Set[A => Any] = mutable.Set()

  protected[this] val validators: mutable.Set[Validator[A]] = mutable.Set()
  protected[this] var validationResult: Future[ValidationResult] = null

  implicit protected[properties] def executionContext: ExecutionContext

  /** Unique property ID. */
  val id: UUID

  /** @return Current property value. */
  def get: A

  /** Registers listener which will be called on value change. */
  def listen(l: A => Any): Registration = {
    listeners += l
    new PropertyRegistration(listeners, l)
  }

  /** @return validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  def isValid: Future[ValidationResult] = {
    if (validationResult == null) validate()
    validationResult
  }

  /**
    * Combines two properties into a new one. Created property will be updated after any change in the origin ones.
    *
    * @param property `Property[B]` to combine with `this`.
    * @param combinedParent Parent of combined property, `null` by default.
    * @param combiner Method combining values A and B into O.
    * @tparam B Type of elements in provided property.
    * @tparam O Output property elements type.
    * @return Property[O] updated on any change in `this` or `property`.
    */
  def combine[B, O : ModelValue](property: ReadableProperty[B], combinedParent: ReadableProperty[_] = null)
                                (combiner: (A, B) => O): ReadableProperty[O] = {
    val pc = implicitly[PropertyCreator[O]]
    val output = pc.newProperty(combinedParent)

    def update(x: A, y: B): Unit =
      output.set(combiner(x, y))

    output.setInitValue(combiner(get, property.get))
    listen(x => update(x, property.get))
    property.listen(y => update(get, y))
    output
  }

  /**
    * Creates ReadableProperty[B] linked to `this`. Changes will be synchronized with `this`.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @tparam B Type of new Property.
    * @return New ReadableProperty[B], which will be synchronised with original ReadableProperty[A].
    */
  def transform[B](transformer: A => B): ReadableProperty[B] =
    new TransformedReadableProperty[A, B](this, transformer)

  /**
    * Creates ReadableSeqProperty[B] linked to `this`. Changes will be synchronized with `this`.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableProperty[A].
    */
  def transform[B : ModelValue](transformer: A => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ReadableSeqPropertyFromSingleValue(this, transformer)

  protected[properties] def parent: ReadableProperty[_]

  protected[properties] def fireValueListeners(): Unit = {
    val t = get
    CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => listeners.foreach(_.apply(t)))
  }

  protected[properties] def valueChanged(): Unit = {
    validationResult = null
    fireValueListeners()
    if (parent != null) parent.valueChanged()
  }

  protected[properties] def validate(): Unit = {
    if (validators.nonEmpty) {
      CallbackSequencer.queue(s"${this.id.toString}:fireValidation", () => {
        import Validator._
        validationResult = Future.sequence(
          validators.collect { case v => v(this.get) }.toSeq
        ).foldValidationResult
      })
    } else validationResult = Future.successful(Valid)
  }
}

/** Property which can be modified. */
trait Property[A] extends ReadableProperty[A] {
  /** Changes current property value. Fires value change listeners.
    * @param t Should not be null! */
  def set(t: A): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new PropertyRegistration(validators, v)
  }

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(f: (A) => ValidationResult): Registration =
    addValidator(Validator(f))

  /**
    * Creates Property[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return New Property[B], which will be synchronised with original Property[A].
    */
  def transform[B](transformer: A => B, revert: B => A): Property[B] =
    new TransformedProperty[A, B](this, transformer, revert)

  /**
    * Creates SeqProperty[B] linked to `this`. Changes will be synchronized with `this` in both directions.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @param revert Method transforming type Seq[B] to A.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original Property[A].
    */
  def transform[B : ModelValue](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert)
}

