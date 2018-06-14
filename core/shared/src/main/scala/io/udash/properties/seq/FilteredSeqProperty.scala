package io.udash.properties.seq

import io.udash.properties.CrossCollections
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration

import scala.collection.mutable

private[properties]
class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean
) extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] {

  private var lastValue: Option[mutable.Buffer[ElemType]] = None
  private val originListeners: mutable.Buffer[Registration] = CrossCollections.createArray

  override protected def onListenerInit(): Unit = {
    super.onListenerInit()
    lastValue = Some(CrossCollections.toCrossArray(origin.elemProperties.filter(el => matcher(el.get))))
    origin.elemProperties.foreach { el => originListeners += el.listen(v => elementChanged(el, v)) }
  }

  override protected def onListenerDestroy(): Unit = {
    super.onListenerDestroy()
    originListeners.foreach(_.cancel())
    originListeners.clear()
    lastValue = None
  }

  override protected def originStructureListener(patch: Patch[ElemType]): Unit = {
    // update origin elements listeners
    patch.removed.indices.foreach { i => originListeners(i + patch.idx).cancel() }
    val newListeners = patch.added.map { el => el.listen(v => elementChanged(el, v)) }
    CrossCollections.replace(originListeners, patch.idx, patch.removed.size, newListeners: _*)

    // update last value
    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get))
    if (added.nonEmpty || removed.nonEmpty) {
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      CrossCollections.replace(lastValue.get, idx, removed.size, added: _*)

      val filteredPatch = Patch[ElemType](idx, removed, added, lastValue.get.isEmpty)
      fireValueListeners()
      fireElementsListeners(filteredPatch, structureListeners)
    }
  }

  private def elementChanged(p: ElemType, value: A): Unit = {
    val filteredProps = lastValue.get
    val oldIdx = filteredProps.indexOf(p)
    val matches = matcher(p.get)

    val patch = (oldIdx, matches) match {
      case (oi, false) if oi != -1 =>
        CrossCollections.replace(lastValue.get, oi, 1)
        Patch[ElemType](oi, Seq(p), Seq.empty, filteredProps.isEmpty)
      case (-1, true) =>
        val originProps = origin.elemProperties
        val ni = originProps.slice(0, originProps.indexOf(p)).count(el => matcher(el.get))
        CrossCollections.replace(filteredProps, ni, 0, p)
        Patch[ElemType](ni, Seq.empty, Seq(p), filteredProps.isEmpty)
      case _ => null
    }

    if (matches || oldIdx != -1) fireValueListeners()
    if (patch != null) fireElementsListeners(patch, structureListeners)
  }

  override def elemProperties: Seq[ElemType] =
    if (lastValue.nonEmpty) lastValue.get.toVector
    else origin.elemProperties.filter(el => matcher(el.get))

  override def get: Seq[A] =
    elemProperties.map(_.get)
}
