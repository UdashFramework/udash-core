package io.udash.properties.seq

import com.avsystem.commons.ISeq
import io.udash.properties.Properties.ReadableProperty
import io.udash.properties.{PropertyCreator, PropertyId}
import io.udash.utils.Registration

private[properties] class PropertySeqCombinedReadableSeqProperty[A](value: ISeq[ReadableProperty[A]])
  extends AbstractReadableSeqProperty[A, ReadableProperty[A]] {

  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  private val children = value.map(_.readable)
  private var originListenerRegistration: Registration = _

  private def killOriginListeners(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
  }

  private def initOriginListeners(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      listeners.clear()
      val registrations = value.map(_.listen(_ => fireValueListeners()))
      originListenerRegistration = new Registration {
        override def restart(): Unit = {
          registrations.foreach(_.restart())
        }

        override def cancel(): Unit = {
          registrations.foreach(_.cancel())
        }

        override def isActive: Boolean =
          registrations.forall(_.isActive)
      }
    }
  }

  override protected def wrapListenerRegistration(registration: Registration): Registration =
    super.wrapListenerRegistration(new Registration {
      override def restart(): Unit = {
        initOriginListeners()
        registration.restart()
      }

      override def cancel(): Unit = {
        registration.cancel()
        killOriginListeners()
      }

      override def isActive: Boolean =
        registration.isActive
    })

  override def listen(valueListener: Seq[A] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: Seq[A] => Any): Registration = {
    initOriginListeners()
    super.listenOnce(valueListener)
  }

  override def get: Seq[A] =
    children.map(_.get)

  override def elemProperties: Seq[ReadableProperty[A]] =
    children

  override def listenStructure(structureListener: Patch[ReadableProperty[A]] => Any): Registration = {
    new Registration {
      override def cancel(): Unit = {}
      override def restart(): Unit = {}
      override def isActive: Boolean = true
    }
  }
}
