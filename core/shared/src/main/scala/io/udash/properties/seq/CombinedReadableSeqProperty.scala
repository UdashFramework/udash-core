package io.udash.properties.seq

import io.udash.properties.single.{CombinedProperty, ReadableProperty}
import io.udash.properties.{CrossCollections, PropertyCreator}
import io.udash.utils.Registration

import scala.collection.mutable

private[properties] class CombinedReadableSeqProperty[A, B, R: PropertyCreator](
  s: ReadableSeqProperty[A, _ <: ReadableProperty[A]], p: ReadableProperty[B],
  combiner: (A, B) => R
) extends CombinedProperty[Seq[A], B, Seq[R]](s, p, null, (x, y) => x.map(v => combiner(v, y)))
  with AbstractReadableSeqProperty[R, ReadableProperty[R]] {

  private var combinedChildren: mutable.Buffer[ReadableProperty[R]] = _
  private var originListenerRegistration: Registration = _

  protected def originStructureListener(originPatch: Patch[ReadableProperty[A]]): Unit = {
    val combinedNewChildren = originPatch.added.map(sub => sub.combine(p)(combiner))
    val mappedPatch: Patch[ReadableProperty[R]] = originPatch.copy(
      removed = originPatch.removed.indices.map(idx => combinedChildren(idx + originPatch.idx)),
      added = combinedNewChildren
    )
    CrossCollections.replace(combinedChildren, originPatch.idx, originPatch.removed.size, combinedNewChildren: _*)
    fireElementsListeners(mappedPatch, structureListeners)
  }

  private def initOriginListener(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      structureListeners.clear()
      val children: mutable.Buffer[ReadableProperty[R]] = CrossCollections.createArray
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

  override def elemProperties: Seq[ReadableProperty[R]] =
    if (combinedChildren != null) combinedChildren
    else s.elemProperties.map(_.combine(p)(combiner))

  override def listenStructure(structureListener: Patch[ReadableProperty[R]] => Any): Registration = {
    initOriginListener()
    super.listenStructure(structureListener)
  }
}
