package io.udash.properties.seq

import io.udash.properties.{CrossCollections, MutableBufferRegistration}
import io.udash.properties.single.{ForwarderProperty, ForwarderReadableProperty, Property, ReadableProperty}
import io.udash.utils.Registration

trait ForwarderReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
  extends ForwarderReadableProperty[Seq[B]] with AbstractReadableSeqProperty[B, ElemType] {

  protected def origin: ReadableSeqProperty[A, OrigType]

  protected var originListenerRegistration: Registration = _
  private var originStructureListenerRegistration: Registration = _
  protected final val structureListeners = CrossCollections.createArray[Patch[ElemType] => Any]

  protected def originListener(originValue: Seq[A]): Unit = {}
  protected def originStructureListener(patch: Patch[OrigType]): Unit = {}

  protected def onListenerInit(): Unit = {}

  protected def initOriginListeners(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      listeners.clear()
      onListenerInit()
      originListenerRegistration = origin.listen(originListener)
    }
    if (originStructureListenerRegistration == null || !originStructureListenerRegistration.isActive) {
      structureListeners.clear()
      originStructureListenerRegistration = origin.listenStructure(originStructureListener)
    }
  }

  protected def killOriginListeners(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
    if (originStructureListenerRegistration != null && listeners.isEmpty) {
      originStructureListenerRegistration.cancel()
      originStructureListenerRegistration = null
    }
  }

  override def listenStructure(structureListener: (Patch[ElemType]) => Any): Registration = {
    initOriginListeners()
    structureListeners += structureListener
    wrapListenerRegistration(new MutableBufferRegistration(structureListeners, structureListener))
  }

  override def listen(valueListener: (Seq[B]) => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    wrapListenerRegistration(super.listen(valueListener, initUpdate))
  }

  override def listenOnce(valueListener: (Seq[B]) => Any): Registration = {
    initOriginListeners()
    wrapListenerRegistration(super.listenOnce(valueListener))
  }

  protected def wrapListenerRegistration(reg: Registration): Registration = new Registration {
    override def restart(): Unit = {
      initOriginListeners()
      reg.restart()
    }

    override def cancel(): Unit = {
      reg.cancel()
      killOriginListeners()
    }

    override def isActive: Boolean =
      reg.isActive
  }
}

trait ForwarderWithLocalCopy[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
  extends ForwarderReadableSeqProperty[A, B, ElemType, OrigType] {

  protected var transformedElements = CrossCollections.createArray[ElemType]

  protected def loadFromOrigin(): Seq[B]
  protected def elementsFromOrigin(): Seq[ElemType]
  protected def transformPatchAndUpdateElements(patch: Patch[OrigType]): Patch[ElemType]

  override def get: Seq[B] = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) loadFromOrigin()
    else transformedElements.map(_.get)
  }

  override def elemProperties: Seq[ElemType] = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) elementsFromOrigin()
    else transformedElements
  }

  override protected def onListenerInit(): Unit = {
    val fromOrigin = CrossCollections.toCrossArray(elementsFromOrigin())
    if (!(transformedElements.iterator.map(_.id) sameElements fromOrigin.iterator.map(_.id))) {
      fireElementsListeners[ElemType](Patch[ElemType](0, transformedElements, fromOrigin, fromOrigin.isEmpty), structureListeners)
      fireValueListeners()
    } else if (transformedElements.map(_.get) != fromOrigin.map(_.get)) {
      fireValueListeners()
    }
    transformedElements = fromOrigin
  }

  override protected def originListener(originValue: Seq[A]) : Unit = {
    fireValueListeners()
  }

  override protected def originStructureListener(patch: Patch[OrigType]) : Unit = {
    val transPatch = transformPatchAndUpdateElements(patch)
    val cpy = CrossCollections.copyArray(structureListeners)
    cpy.foreach(_.apply(transPatch))
    fireValueListeners()
  }
}


trait ForwarderSeqProperty[A, B, ElemType <: Property[B], OrigType <: Property[A]]
  extends ForwarderReadableSeqProperty[A, B, ElemType, OrigType] with ForwarderProperty[Seq[B]] with SeqProperty[B, ElemType] {

  protected def origin: SeqProperty[A, OrigType]

  override def clearListeners(): Unit = {
    structureListeners.clear()
    super.clearListeners()
  }
}
