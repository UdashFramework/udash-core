package io.udash.properties.single

import com.avsystem.commons.misc.Opt
import io.udash.properties._
import io.udash.properties.seq.{SeqProperty, SeqPropertyFromSingleValue}
import io.udash.utils.Registration

import scala.util.{Failure, Success}

object Property {
  /** Creates a blank `DirectProperty[T]`.  */
  def blank[T](implicit pc: PropertyCreator[T], blank: Blank[T]): CastableProperty[T] =
    pc.newProperty(null)(blank)

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
      p.readable
    }

    def clear(): Unit =
      if (p != null) p.set(Valid)
  }
}

/** Property which can be modified. */
trait Property[A] extends ReadableProperty[A] {
  /** Changes current property value. Fires value change listeners.
    * @param t Should not be null!
    * @param force If true, the value change listeners will be fired even if value didn't change. */
  def set(t: A, force: Boolean = false): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Fires value change listeners with current value and clears validation result. */
  def touch(): Unit

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(v: Validator[A]): Registration

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(f: A => ValidationResult): Registration

  /** Removes all validators from property and clears current validation result. It does not fire validation process. */
  def clearValidators(): Unit

  /** Removes all listeners from property. */
  def clearListeners(): Unit

  /**
    * Creates Property[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return New Property[B], which will be synchronised with original Property[A].
    */
  def transform[B](transformer: A => B, revert: B => A): Property[B]

  /**
    * Creates SeqProperty[B] linked to `this`. Changes will be synchronized with `this` in both directions.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @param revert Method transforming type Seq[B] to A.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original Property[A].
    */
  def transformToSeq[B](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]]

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
  def sync[B](p: Property[B])(transformer: A => B, revert: B => A): Registration
}

/** Property which can be modified. */
private[properties] trait AbstractProperty[A] extends AbstractReadableProperty[A] with Property[A] {
  override def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new MutableBufferRegistration(validators, v, Opt.empty)
  }

  override def addValidator(f: A => ValidationResult): Registration =
    addValidator(Validator(f))

  override def clearValidators(): Unit = {
    validators.clear()
    validationResult = null
    validationProperty.clear()
  }

  override def clearListeners(): Unit = {
    listenersUpdate()
    listeners.clear()
    oneTimeListeners.clear()
  }

  override def transform[B](transformer: A => B, revert: B => A): Property[B] =
    new TransformedProperty[A, B](this, transformer, revert)

  override def transformToSeq[B](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert)

  override def sync[B](p: Property[B])(transformer: A => B, revert: B => A): Registration = {
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

  //def cmb[B, O](p: Property[B])(transformer: (A, B))
}
