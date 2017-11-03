package io.udash.properties
package single

import io.udash.utils.Registration

import scala.concurrent.Future

/** Represents ReadableProperty[A] transformed to ReadableProperty[B]. */
private[properties]
class TransformedReadableProperty[A, B](override protected val origin: ReadableProperty[A],
                                        transformer: A => B) extends ForwarderReadableProperty[B] {
  protected var lastValue: A = _
  protected var transformedValue: B = _
  protected var originListenerRegistration: Registration = _

  protected def originListener(originValue: A) : Unit = {
    lastValue = originValue
    transformedValue = transformer(originValue)
    fireValueListeners()
  }

  override def listen(valueListener: (B) => Any, initUpdate: Boolean = false): Registration = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive()) {
      originListenerRegistration = origin.listen(originListener)
    }
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: (B) => Any): Registration = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive()) {
      originListenerRegistration = origin.listen(originListener)
    }
    super.listenOnce(valueListener)
  }

  override def get: B = {
    val originValue = origin.get
    if (lastValue != originValue) {
      lastValue = originValue
      transformedValue = transformer(originValue)
    }
    transformedValue
  }
}

/** Represents Property[A] transformed to Property[B]. */
private[properties]
class TransformedProperty[A, B](override protected val origin: Property[A], transformer: A => B, revert: B => A)
  extends TransformedReadableProperty[A, B](origin, transformer) with ForwarderProperty[B] {

  protected var originValidatorRegistration: Registration = _

  override def set(t: B, force: Boolean = false): Unit =
    origin.set(revert(t), force)

  override def setInitValue(t: B): Unit =
    origin.setInitValue(revert(t))

  override def touch(): Unit =
    origin.touch()

  override def addValidator(v: Validator[B]): Registration = {
    if (originValidatorRegistration == null || !originValidatorRegistration.isActive()) {
      originValidatorRegistration = origin.addValidator(new Validator[A] {
        override def apply(element: A): Future[ValidationResult] = {
          import Validator._

          import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
          val transformedValue = transformer(element)
          Future.sequence(
            validators.map(_.apply(transformedValue)).toSeq
          ).foldValidationResult
        }
      })
    }
    super.addValidator(v)
  }

  override def clearValidators(): Unit = {
    originValidatorRegistration = null
    super.clearValidators()
    origin.clearValidators()
  }

  override def clearListeners(): Unit = {
    originListenerRegistration = null
    super.clearListeners()
    origin.clearListeners()
  }
}
