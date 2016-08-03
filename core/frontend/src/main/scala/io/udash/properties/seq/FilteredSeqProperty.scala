package io.udash.properties.seq

import io.udash.properties.PropertyRegistration
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration

import scala.collection.mutable

private[properties]
class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]]
                         (override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean)
  extends ForwarderReadableSeqProperty[A, ElemType] {

  private def loadPropsFromOrigin() =
    origin.elemProperties.filter(el => matcher(el.get))

  private var filteredProps: Seq[ElemType] = loadPropsFromOrigin()

  private val structureListeners: mutable.Set[(Patch[ElemType]) => Any] = mutable.Set.empty

  private def elementChanged(p: ElemType)(v: A): Unit = {
    val props = loadPropsFromOrigin()
    val oldIdx = filteredProps.indexOf(p)
    val newIdx = props.indexOf(p)

    val patch = (oldIdx, newIdx) match {
      case (oi, -1) if oi != -1 =>
        filteredProps = filteredProps.slice(0, oi) ++ filteredProps.slice(oi + 1, filteredProps.size)
        Patch[ElemType](oi, Seq(p), Seq.empty, filteredProps.isEmpty)
      case (-1, ni) if ni != -1 =>
        filteredProps = (filteredProps.slice(0, ni) :+ p) ++ filteredProps.slice(ni, filteredProps.size)
        Patch[ElemType](ni, Seq.empty, Seq(p), filteredProps.isEmpty)
      case _ => null
    }

    if (newIdx != -1 || oldIdx != -1) fireValueListeners()
    if (patch != null) fireElementsListeners(patch, structureListeners)
  }

  private val registrations = mutable.HashMap.empty[ElemType, Registration]

  origin.elemProperties.foreach(p => registrations(p) = p.listen(elementChanged(p)))
  origin.listenStructure(patch => {
    patch.removed.foreach(p => if (registrations.contains(p)) {
      registrations(p).cancel()
      registrations.remove(p)
    })
    patch.added.foreach(p => registrations(p) = p.listen(elementChanged(p)))

    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get))
    if (added.nonEmpty || removed.nonEmpty) {
      val props = loadPropsFromOrigin()
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      val callbackProps = props.map(_.get)

      filteredProps = filteredProps.slice(0, idx) ++ added ++ filteredProps.slice(idx + removed.size, filteredProps.size)

      val filteredPatch = Patch[ElemType](idx, removed, added, filteredProps.isEmpty)

      fireValueListeners()
      fireElementsListeners(filteredPatch, structureListeners)
    }
  })

  override def listenStructure(l: (Patch[ElemType]) => Any): Registration = {
    structureListeners.add(l)
    new PropertyRegistration(structureListeners, l)
  }

  override def elemProperties: Seq[ElemType] =
    filteredProps

  override def get: Seq[A] =
    filteredProps.map(_.get)
}
