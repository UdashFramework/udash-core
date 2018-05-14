package io.udash.properties.single

import io.udash.properties._
import io.udash.properties.seq.{SeqProperty, SeqPropertyFromSingleValue}
import io.udash.utils.Registration

import scala.util.{Failure, Success}

object Property {
  /** Creates an empty `DirectProperty[T]`.
    * It's not recommended to use this method. Use `apply` with initial value if possible. */
  def empty[T](implicit pc: PropertyCreator[T], blank: Blank[T]): CastableProperty[T] =
    pc.newProperty(null)(blank)

  /** Creates an empty `DirectProperty[T]`. */
  @deprecated("Use `Property.empty` instead.", "0.6.0")
  def apply[T: PropertyCreator]: CastableProperty[T] =
    empty

  /** Creates `DirectProperty[T]` with initial value. */
  def apply[T](init: T)(implicit pc: PropertyCreator[T]): CastableProperty[T] =
    pc.newProperty(init, null)

  private[single] class ValidationProperty[A](target: ReadableProperty[A]) {
    import scala.concurrent.ExecutionContext.Implicits.global
    private var initialized: Boolean = false
    private var p: Property[ValidationResult] = _
    private val listener = (_: A) => target.isValid onComplete {
      case Success(result) => p.set(result)
      case Failure(ex) => p.set(Invalid(ex.getMessage))
    }

    def property: ReadableProperty[ValidationResult] = {
      if (!initialized) {
        initialized = true
        p = Property[ValidationResult](Valid)
        listener(target.get)
        target.listen(listener)
      }
      p.transform(identity)
    }

    def clear(): Unit =
      if (p != null) p.set(Valid)
  }
}

/** Property which can be modified. */
trait Property[A] extends AbstractReadableProperty[A] {
  /** Changes current property value. Fires value change listeners.
    * @param t Should not be null!
    * @param force If true, the value change listeners will be fired even if value didn't change. */
  def set(t: A, force: Boolean = false): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Fires value change listeners with current value and clears validation result. */
  def touch(): Unit

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new MutableBufferRegistration(validators, v)
  }

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(f: (A) => ValidationResult): Registration =
    addValidator(Validator(f))

  /** Removes all validators from property and clears current validation result. It does not fire validation process. */
  def clearValidators(): Unit = {
    validators.clear()
    validationResult = null
    validationProperty.clear()
  }

  /** Removes all listeners from property. */
  def clearListeners(): Unit = {
    listeners.clear()
    oneTimeListeners.clear()
  }

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
  def transformToSeq[B : PropertyCreator](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert)

  /**
    * Bidirectionally synchronizes Property[B] with `this`. The transformed value is synchronized from `this`
    * to Property[B] on initialization.
    *
    * @param p           Property to be synchronized with `this`.
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert      Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return Bidirectional registration between existing and new property.
    */
  def sync[B](p: Property[B])(transformer: A => B, revert: B => A): Registration = {
    val transformerRegistration = this.streamTo(p)(transformer)
    val revertRegistration = p.streamTo(this, initUpdate = false)(revert)
    new Registration {
      override def cancel(): Unit = {
        transformerRegistration.cancel()
        revertRegistration.cancel()
      }

      override def isActive: Boolean = {
        transformerRegistration.isActive && revertRegistration.isActive
      }

      override def restart(): Unit = {
        transformerRegistration.restart()
        revertRegistration.restart()
        touch()
      }
    }

  }
}
