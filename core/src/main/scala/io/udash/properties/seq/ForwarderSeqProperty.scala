package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{ForwarderReadableProperty, ReadableProperty}
import io.udash.utils.{CrossCollections, Registration}

private[properties] trait ForwarderReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
  extends AbstractReadableSeqProperty[B, ElemType] with ForwarderReadableProperty[BSeq[B]] {

  protected def origin: ReadableSeqProperty[A, OrigType]

  private var originStructureListenerRegistration: Registration = _

  protected final def initialized: Boolean = originStructureListenerRegistration != null && originStructureListenerRegistration.isActive

  protected def originStructureListener(patch: Patch[OrigType]): Unit = {}
  protected def onListenerInit(): Unit = {}
  protected def onListenerDestroy(): Unit = {}

  protected def initOriginListeners(): Unit =
    if (!initialized) {
      listeners.clear()
      onListenerInit()
      structureListeners.clear()
      originStructureListenerRegistration = origin.listenStructure(originStructureListener)
    }

  protected def killOriginListeners(): Unit =
    if (initialized && listeners.isEmpty && structureListeners.isEmpty) {
      onListenerDestroy()
      originStructureListenerRegistration.cancel()
      originStructureListenerRegistration = null
    }

  override def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
    initOriginListeners()
    super.listenStructure(structureListener)
  }

  override def listen(valueListener: BSeq[B] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: BSeq[B] => Any): Registration = {
    initOriginListeners()
    super.listenOnce(valueListener)
  }

  override protected def wrapListenerRegistration(reg: Registration): Registration =
    super.wrapListenerRegistration(new Registration {
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
    })
}

private[properties] trait ForwarderWithLocalCopy[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
  extends ForwarderReadableSeqProperty[A, B, ElemType, OrigType] {

  protected final var transformedElements: MBuffer[ElemType] = CrossCollections.createArray[ElemType]

  protected def loadFromOrigin(): BSeq[B]
  protected def elementsFromOrigin(): BSeq[ElemType]
  protected def transformPatchAndUpdateElements(patch: Patch[OrigType]): Patch[ElemType]

  override def get: BSeq[B] =
    if (initialized) transformedElements.map(_.get)
    else loadFromOrigin()

  //todo this is the reason filters don't work - elem properties change every time when there are no listeners
  override def elemProperties: BSeq[ElemType] =
    if (initialized) transformedElements
    else elementsFromOrigin()

  override protected def onListenerInit(): Unit = {
    val fromOrigin = CrossCollections.toCrossArray(elementsFromOrigin())
    if (!(transformedElements.iterator sameElements fromOrigin.iterator) ||
      !(transformedElements.iterator.map(_.get) sameElements fromOrigin.iterator.map(_.get))) {
      val removed = transformedElements.toVector
      transformedElements = fromOrigin
      fireElementsListeners(Patch(0, removed, fromOrigin.toSeq))
      valueChanged()
    }
  }

  override protected def originStructureListener(patch: Patch[OrigType]): Unit = {
    val transPatch = transformPatchAndUpdateElements(patch)
    structureListeners.foreach(_.apply(transPatch))
    valueChanged()
  }
}
