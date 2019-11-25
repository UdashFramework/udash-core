package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.Properties.ReadableProperty
import io.udash.properties.{ImmutableProperty, PropertyCreator, PropertyId}
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
      val registrations = value.map(_.listen(_ => valueChanged()))
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

  override def listen(valueListener: BSeq[A] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: BSeq[A] => Any): Registration = {
    initOriginListeners()
    super.listenOnce(valueListener)
  }

  override def get: ISeq[A] =
    children.map(_.get)

  override def elemProperties: ISeq[ReadableProperty[A]] =
    children

  override def listenStructure(structureListener: Patch[ReadableProperty[A]] => Any): Registration =
    ImmutableProperty.NoOpRegistration
}
