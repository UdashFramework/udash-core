package io.udash.properties.seq

import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}
import io.udash.utils.{JsArrayRegistration, Registration}

import scala.scalajs.js.JSConverters._
import scala.scalajs.js

private[properties]
class TransformedReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
                                    (override protected val origin: ReadableSeqProperty[A, OrigType], transformer: A => B)
  extends ForwarderReadableSeqProperty[B, ElemType] {

  private var originListenerRegistration: Registration = null
  private var originStructureListenerRegistration: Registration = null

  protected val structureListeners: js.Array[Patch[ElemType] => Any] = js.Array()
  protected var transformedElements: js.Array[ReadableProperty[B]] = js.Array()

  override def get: Seq[B] = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive()) {
      origin.get.map(transformer)
    } else transformedElements.map(_.get).toSeq
  }

  override def elemProperties: Seq[ElemType] = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive()) {
      origin.elemProperties.map(transformElement)
    } else transformedElements.toSeq.asInstanceOf[Seq[ElemType]]
  }

  protected def originListener(originValue: Seq[A]) : Unit = {
    fireValueListeners()
  }

  private def initOriginListeners(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive()) {
      listeners.clear()
      transformedElements = origin.elemProperties.map(transformElement).toJSArray.asInstanceOf[js.Array[ReadableProperty[B]]]
      originListenerRegistration = origin.listen(originListener)
    }
    if (originStructureListenerRegistration == null || !originStructureListenerRegistration.isActive()) {
      structureListeners.clear()
      originStructureListenerRegistration = origin.listenStructure(originStructureListener)
    }
  }

  private def killOriginListeners(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
    if (originStructureListenerRegistration != null && listeners.isEmpty) {
      originStructureListenerRegistration.cancel()
      originStructureListenerRegistration = null
    }
  }

  protected def originStructureListener(patch: Patch[OrigType]) : Unit = {
    val transPatch = Patch[ElemType](
      patch.idx,
      patch.removed.map(transformElement),
      patch.added.map(transformElement),
      patch.clearsProperty
    )

    transformedElements.splice(patch.idx, patch.removed.length, transPatch.added: _*)
    val cpy = structureListeners.jsSlice()
    cpy.foreach(_.apply(transPatch))
    fireValueListeners()
  }

  override def listenStructure(structureListener: (Patch[ElemType]) => Any): Registration = {
    initOriginListeners()
    structureListeners += structureListener
    wrapListenerRegistration(new JsArrayRegistration(structureListeners, structureListener))
  }

  private def wrapListenerRegistration(reg: Registration): Registration = new Registration {
    override def restart(): Unit = {
      initOriginListeners()
      reg.restart()
    }

    override def cancel(): Unit = {
      reg.cancel()
      killOriginListeners()
    }

    override def isActive(): Boolean =
      reg.isActive()
  }

  override def listen(valueListener: (Seq[B]) => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    wrapListenerRegistration(super.listen(valueListener, initUpdate))
  }

  override def listenOnce(valueListener: (Seq[B]) => Any): Registration = {
    initOriginListeners()
    wrapListenerRegistration(super.listenOnce(valueListener))
  }

  protected def transformElement(el: OrigType): ElemType =
    el.transform(transformer).asInstanceOf[ElemType]
}

private[properties]
class TransformedSeqProperty[A, B](override protected val origin: SeqProperty[A, Property[A]],
                                   transformer: A => B, revert: B => A)
  extends TransformedReadableSeqProperty[A, B, Property[B], Property[A]](origin, transformer) with ForwarderSeqProperty[B, Property[B]] {

  override protected def transformElement(el: Property[A]): Property[B] =
    el.transform(transformer, revert)

  override def replace(idx: Int, amount: Int, values: B*): Unit =
    origin.replace(idx, amount, values.map(revert): _*)

  override def set(t: Seq[B], force: Boolean = false): Unit =
    origin.set(t.map(revert), force)

  override def setInitValue(t: Seq[B]): Unit =
    origin.setInitValue(t.map(revert))

  override def touch(): Unit =
    origin.touch()

  override def clearListeners(): Unit = {
    structureListeners.clear()
    super.clearListeners()
  }
}
