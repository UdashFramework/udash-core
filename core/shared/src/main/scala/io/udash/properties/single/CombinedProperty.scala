package io.udash.properties.single

import io.udash.properties.MutableBufferRegistration
import io.udash.utils.Registration

class CombinedProperty[A, B, R](
  override val origin: ReadableProperty[A], originTwo: ReadableProperty[B],
  override val parent: ReadableProperty[_], combiner: (A, B) => R
) extends ForwarderReadableProperty[R] {
  protected var lastValueOne: Option[A] = None
  protected var lastValueTwo: Option[B] = None
  protected var originListenerRegistrations: (Registration, Registration) = _

  protected def originListenerOne(originValue: A) : Unit = {
    lastValueOne = Some(originValue)
    fireValueListeners()
  }

  protected def originListenerTwo(originValue: B) : Unit = {
    lastValueTwo = Some(originValue)
    fireValueListeners()
  }

  private def initOriginListener(): Unit = {
    if (originListenerRegistrations == null || !originListenerRegistrations._1.isActive || !originListenerRegistrations._2.isActive) {
      listeners.clear()
      originListenerRegistrations = (origin.listen(originListenerOne), originTwo.listen(originListenerTwo))
    }
  }

  private def killOriginListener(): Unit = {
    if (originListenerRegistrations != null && listeners.isEmpty) {
      originListenerRegistrations._1.cancel()
      originListenerRegistrations._2.cancel()
      originListenerRegistrations = null
    }
  }

  private def wrapListenerRegistration(reg: Registration): Registration = new Registration {
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
  }

  override def listen(valueListener: R => Any, initUpdate: Boolean = false): Registration = {
    initOriginListener()
    wrapListenerRegistration(super.listen(valueListener, initUpdate))
  }

  override def listenOnce(valueListener: R => Any): Registration = {
    initOriginListener()
    val reg = wrapListenerRegistration(new MutableBufferRegistration(listeners, valueListener))
    oneTimeListeners += ((valueListener, () => reg.cancel()))
    reg
  }

  override def get: R = {
    val originValueOne = origin.get
    val originValueTwo = originTwo.get
    if (lastValueOne.isEmpty || lastValueTwo.isEmpty || lastValueOne.get != originValueOne || lastValueTwo.get != originValueTwo) {
      lastValueOne = Some(originValueOne)
      lastValueTwo = Some(originValueTwo)
    }
    combiner(originValueOne, originValueTwo)
  }
}
