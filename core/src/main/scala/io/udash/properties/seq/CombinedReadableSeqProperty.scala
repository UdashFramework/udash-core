package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.PropertyCreator
import io.udash.properties.single.{CombinedProperty, ReadableProperty}
import io.udash.utils.{CrossCollections, Registration}

private[properties] class CombinedReadableSeqProperty[A, B, R: PropertyCreator](
  s: ReadableSeqProperty[A, _ <: ReadableProperty[A]], p: ReadableProperty[B],
  combiner: (A, B) => R
) extends CombinedProperty[BSeq[A], B, BSeq[R]](s, p, (x, y) => x.map(v => combiner(v, y)))
  with AbstractReadableSeqProperty[R, ReadableProperty[R]] {

  private var combinedChildren: MBuffer[ReadableProperty[R]] = _
  private var originListenerRegistration: Registration = _

  protected def originStructureListener(originPatch: Patch[ReadableProperty[A]]): Unit = {
    val combinedNewChildren = originPatch.added.map(sub => sub.combine(p)(combiner))
    val mappedPatch: Patch[ReadableProperty[R]] = originPatch.copy(
      removed = originPatch.removed.indices.map(idx => combinedChildren(idx + originPatch.idx)),
      added = combinedNewChildren
    )
    CrossCollections.replaceSeq(combinedChildren, originPatch.idx, originPatch.removed.size, combinedNewChildren)
    fireElementsListeners(mappedPatch, structureListeners)
  }

  private def initOriginListener(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      structureListeners.clear()
      val children: MBuffer[ReadableProperty[R]] = CrossCollections.createArray
      s.elemProperties.foreach(sub => children += sub.combine(p)(combiner))
      combinedChildren = children
      originListenerRegistration = s.listenStructure(originStructureListener)
    }
  }

  private def killOriginListener(): Unit = {
    if (originListenerRegistration != null && structureListeners.isEmpty) {
      originListenerRegistration.cancel()
      combinedChildren = null
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

  override def elemProperties: BSeq[ReadableProperty[R]] =
    if (combinedChildren != null) combinedChildren
    else s.elemProperties.map(_.combine(p)(combiner))

  override def listenStructure(structureListener: Patch[ReadableProperty[R]] => Any): Registration = {
    initOriginListener()
    super.listenStructure(structureListener)
  }
}
