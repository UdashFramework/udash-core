package io.udash.properties
package single

import com.avsystem.commons._
import io.udash.utils.Registration

/** Represents ReadableProperty[A] transformed to ReadableProperty[B]. */
private[properties] class TransformedReadableProperty[A, B](
  override protected val origin: ReadableProperty[A],
  transformer: A => B
) extends ForwarderReadableProperty[B] {
  protected var lastValue: Opt[A] = Opt.empty
  protected var transformedValue: B = _
  protected var originListenerRegistration: Registration = _

  protected def originListener(originValue: A) : Unit = {
    lastValue = Opt(originValue)
    transformedValue = transformer(originValue)
    valueChanged()
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
      lastValue = Opt(originValue)
      transformedValue = transformer(originValue)
    }
    transformedValue
  }
}

/** Represents Property[A] transformed to Property[B]. */
private[properties] class TransformedProperty[A, B](
  override protected val origin: Property[A],
  transformer: A => B, revert: B => A
) extends TransformedReadableProperty[A, B](origin, transformer) with ForwarderProperty[B] {

  override def set(t: B, force: Boolean = false): Unit =
    origin.set(revert(t), force)

  override def setInitValue(t: B): Unit =
    origin.setInitValue(revert(t))

  override def touch(): Unit =
    origin.touch()

  override def clearListeners(): Unit = {
    originListenerRegistration = null
    super.clearListeners()
    origin.clearListeners()
  }
}
