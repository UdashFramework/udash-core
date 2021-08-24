package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.{ForwarderReadableProperty, ReadableProperty}
import io.udash.utils.{CrossCollections, Registration}

private[properties] trait ForwarderReadableSeqProperty[A, B, ElemType <: ReadableProperty[B], OrigType <: ReadableProperty[A]]
  extends AbstractReadableSeqProperty[B, ElemType] with ForwarderReadableProperty[BSeq[B]] {

  protected def origin: ReadableSeqProperty[A, OrigType]

  protected final var transformedElements: MBuffer[ElemType] = CrossCollections.createArray[ElemType]
  private var originStructureListenerRegistration: Registration = _

  protected final def initialized: Boolean =
    originStructureListenerRegistration != null && originStructureListenerRegistration.isActive

  protected def getFromOrigin(): BSeq[B]
  protected def transformElements(elemProperties: BSeq[OrigType]): BSeq[ElemType]
  protected def transformPatch(patch: Patch[OrigType]): Opt[Patch[ElemType]]

  protected def originStructureListener(patch: Patch[OrigType]): Unit =
    transformPatch(patch).foreach { transformed =>
      CrossCollections.replaceSeq(transformedElements, transformed.idx, transformed.removed.length, transformed.added)
      fireElementsListeners(transformed)
      valueChanged()
    }

  protected def onListenerInit(originElems: BSeq[OrigType]): Unit = {
    transformedElements = CrossCollections.toCrossArray(transformElements(originElems))
  }

  protected def onListenerDestroy(): Unit = {}

  private def initOriginListener(): Unit =
    if (!initialized) {
      listeners.clear()
      onListenerInit(origin.elemProperties)
      structureListeners.clear()
      originStructureListenerRegistration = origin.listenStructure(originStructureListener)
    }

  private def killOriginListener(): Unit =
    if (initialized && listeners.isEmpty && structureListeners.isEmpty) {
      onListenerDestroy()
      originStructureListenerRegistration.cancel()
      originStructureListenerRegistration = null
    }

  override def get: BSeq[B] =
    if (initialized) transformedElements.map(_.get)
    else getFromOrigin()

  override def elemProperties: BSeq[ElemType] =
    if (initialized) transformedElements
    else transformElements(origin.elemProperties)

  override def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
    initOriginListener()
    super.listenStructure(structureListener)
  }

  override def listen(valueListener: BSeq[B] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListener()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: BSeq[B] => Any): Registration = {
    initOriginListener()
    super.listenOnce(valueListener)
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
}
