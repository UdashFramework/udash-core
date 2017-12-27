package io.udash.properties.seq

import io.udash.properties.{CrossCollections, MutableBufferRegistration}
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration

import scala.collection.mutable

private[properties]
class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]]
                         (override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean)
  extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] {

  private def loadPropsFromOrigin(): mutable.Buffer[ElemType] =
    CrossCollections.toCrossArray(origin.elemProperties.filter(el => matcher(el.get)))

  private val filteredProps: mutable.Buffer[ElemType] = loadPropsFromOrigin()

  private def elementChanged(p: ElemType)(v: A): Unit = {
    val props = loadPropsFromOrigin()
    val oldIdx = filteredProps.indexOf(p)
    val newIdx = props.indexOf(p)

    val patch = (oldIdx, newIdx) match {
      case (oi, -1) if oi != -1 =>
        CrossCollections.replace(filteredProps, oi, 1)
        Patch[ElemType](oi, Seq(p), Seq.empty, filteredProps.isEmpty)
      case (-1, ni) if ni != -1 =>
        CrossCollections.replace(filteredProps, ni, 0, p)
        Patch[ElemType](ni, Seq.empty, Seq(p), filteredProps.isEmpty)
      case _ => null
    }

    if (newIdx != -1 || oldIdx != -1) fireValueListeners()
    if (patch != null) fireElementsListeners(patch, structureListeners)
  }

  private val registrations = mutable.HashMap.empty[ElemType, Registration]

  origin.elemProperties.foreach(p => registrations(p) = p.listen(elementChanged(p)))
  origin.listenStructure { patch =>
    patch.removed.foreach(p => if (registrations.contains(p)) {
      registrations(p).cancel()
      registrations.remove(p)
    })
    patch.added.foreach(p => registrations(p) = p.listen(elementChanged(p)))

    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get))
    if (added.nonEmpty || removed.nonEmpty) {
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))

      CrossCollections.replace(filteredProps, idx, removed.size, added:_*)

      val filteredPatch = Patch[ElemType](idx, removed, added, filteredProps.isEmpty)

      fireValueListeners()
      fireElementsListeners(filteredPatch, structureListeners)
    }
  }

  override def listenStructure(structureListener: (Patch[ElemType]) => Any): Registration = {
    structureListeners += structureListener
    new MutableBufferRegistration(structureListeners, structureListener)
  }

  override def elemProperties: Seq[ElemType] =
    filteredProps

  override def get: Seq[A] =
    filteredProps.map(_.get)
}
