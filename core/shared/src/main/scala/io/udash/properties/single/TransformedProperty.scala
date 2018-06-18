package io.udash.properties
package single

import io.udash.utils.Registration

import scala.concurrent.Future

/** Represents ReadableProperty[A] transformed to ReadableProperty[B]. */
private[properties]
class TransformedReadableProperty[A, B](
  override protected val origin: ReadableProperty[A],
  transformer: A => B
) extends ForwarderReadableProperty[B] {
  protected var lastValue: Option[A] = None
  protected var transformedValue: B = _
  protected var originListenerRegistration: Registration = _

  protected def originListener(originValue: A) : Unit = {
    lastValue = Some(originValue)
    transformedValue = transformer(originValue)
    fireValueListeners()
  }

  private def initOriginListener(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      listeners.clear()
      originListenerRegistration = origin.listen(originListener)
    }
  }

  private def killOriginListener(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
  }

  override protected def wrapListenerRegistration(reg: Registration): Registration =
    super.wrapListenerRegistration(new Registration {
      override def restart(): Unit = {
        initOriginListener()
        reg.restart()
      }

      override def cancel(): Unit = {
        reg.cancel()
        killOriginListener()
      }

      override def isActive: Boolean =
        reg.isActive
    })

  override def listen(valueListener: B => Any, initUpdate: Boolean = false): Registration = {
    initOriginListener()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: B => Any): Registration = {
    initOriginListener()
    super.listenOnce(valueListener)
  }

  override def get: B = {
    val originValue = origin.get
    if (lastValue.isEmpty || lastValue.get != originValue) {
      lastValue = Some(originValue)
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

  private def initOriginValidator(): Unit = {
    if (originValidatorRegistration == null || !originValidatorRegistration.isActive) {
      super.clearValidators()
      originValidatorRegistration = origin.addValidator(new Validator[A] {
        override def apply(element: A): Future[ValidationResult] = {
          import scala.concurrent.ExecutionContext.Implicits.global
          import Validator._

          val transformedValue = transformer(element)
          Future.sequence(
            validators.map(_.apply(transformedValue)).toSeq
          ).foldValidationResult
        }
      })
    }
  }

  override def addValidator(v: Validator[B]): Registration = {
    initOriginValidator()
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
