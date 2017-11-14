package io.udash.properties.single

import java.util.UUID

import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, ReadableSeqPropertyFromSingleValue, SeqProperty, SeqPropertyFromSingleValue}
import io.udash.utils.{JsArrayRegistration, Registration}

import scala.concurrent.{Future, Promise}
import scala.language.higherKinds
import scala.scalajs.js
import scala.util.{Failure, Success}

object Property {
  /** Creates an empty DirectProperty[T]. */
  def empty[T](implicit pc: PropertyCreator[T]): CastableProperty[T] =
    pc.newProperty(null)

  /** Creates an empty DirectProperty[T]. */
  def apply[T: PropertyCreator]: CastableProperty[T] =
    empty

  /** Creates DirectProperty[T] with initial value. */
  def apply[T](init: T)(implicit pc: PropertyCreator[T]): CastableProperty[T] =
    pc.newProperty(init, null)

  private[single] class ValidationProperty[A](target: ReadableProperty[A]) {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    private var initialized: Boolean = false
    private var p: Property[ValidationResult] = _
    private val listener = (_: A) => target.isValid onComplete {
      case Success(result) => p.set(result)
      case Failure(ex) => p.set(Invalid(ex.getMessage))
    }

    def property: ReadableProperty[ValidationResult] = {
      if (!initialized) {
        initialized = true
        p = Property.empty[ValidationResult]
        listener(target.get)
        target.listen(listener)
      }
      p.transform(identity)
    }

    def clear(): Unit =
      if (p != null) p.set(Valid)
  }
}

/** Base interface of every Property in Udash. */
trait ReadableProperty[A] {
  protected[this] val listeners: js.Array[A => Any] = js.Array()
  protected[this] val oneTimeListeners: js.Array[(A => Any, () => Any)] = js.Array()

  protected[this] lazy val validationProperty: Property.ValidationProperty[A] = new Property.ValidationProperty[A](this)
  protected[this] val validators: js.Array[Validator[A]] = js.Array()
  protected[this] var validationResult: Future[ValidationResult] = _

  /** Unique property ID. */
  val id: UUID

  /** @return Current property value. */
  def get: A

  /**
    * Registers listener which will be called on value change.
    * @param initUpdate If `true`, listener will be instantly triggered with current value of property.
    */
  def listen(valueListener: A => Any, initUpdate: Boolean = false): Registration = {
    listeners += valueListener
    if (initUpdate) valueListener(this.get)
    new JsArrayRegistration(listeners, valueListener)
  }

  /** Registers listener which will be called on the next value change. This listener will be fired only once. */
  def listenOnce(valueListener: A => Any): Registration = {
    val reg = new JsArrayRegistration(listeners, valueListener)
    oneTimeListeners += ((valueListener, () => reg.cancel()))
    reg
  }

  /** Returns listeners count. */
  private[properties] def listenersCount(): Int =
    listeners.length + oneTimeListeners.length

  /** @return validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  def isValid: Future[ValidationResult] = {
    if (validationResult == null) validate()
    validationResult
  }

  /** Property containing validation result. */
  lazy val valid: ReadableProperty[ValidationResult] = validationProperty.property

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
  def transformToSeq[B : ModelValue](transformer: A => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ReadableSeqPropertyFromSingleValue(this, transformer)

  /** Streams value changes to the `target` property.
    * It is not as strong relation as `transform`, because `target` can change value independently. */
  def streamTo[B](target: Property[B], initUpdate: Boolean = true)(transformer: A => B): Registration = {
    @inline def update(v: A) =
      target.set(transformer(v))
    if (initUpdate) update(get)
    val listenerRegistration = listen(update)
    new Registration {
      override def cancel(): Unit = listenerRegistration.cancel()
      override def isActive: Boolean = listenerRegistration.isActive
      override def restart(): Unit = {
        listenerRegistration.restart()
        update(get)
      }
    }
  }

  protected[properties] def parent: ReadableProperty[_]

  protected[properties] def fireValueListeners(): Unit = {
    CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => {
      val t = get
      val listenersCopy = listeners.jsSlice()
      val oneTimeListenersCopy = oneTimeListeners.jsSlice()
      oneTimeListeners.clear()
      listenersCopy.foreach(_.apply(t))
      oneTimeListenersCopy.foreach { case (callback, cancel) =>
        callback(t)
        cancel()
      }
    })
  }

  protected[properties] def valueChanged(): Unit = {
    validationResult = null
    fireValueListeners()
    if (parent != null) parent.valueChanged()
  }

  protected[properties] def validate(): Unit = {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    if (validators.nonEmpty) {
      val p = Promise[ValidationResult]
      validationResult = p.future
      CallbackSequencer.queue(s"${this.id.toString}:fireValidation", () => {
        import Validator._
        val currentValue = this.get
        val cpy = validators.jsSlice()
        p.completeWith {
          Future.sequence(
            cpy.map(_ (currentValue)).toSeq
          ).foldValidationResult
        }
      })
    } else validationResult = Future.successful(Valid)
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
  def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new JsArrayRegistration(validators, v)
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
  def transformToSeq[B : ModelValue](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert)
}

