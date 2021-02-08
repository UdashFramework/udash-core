package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.{CrossCollections, Registration}

private[properties] final class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean
) extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] {

  private var lastValue: MBuffer[ElemType] = _
  private val originListeners: MBuffer[Registration] = CrossCollections.createArray

  override protected def onListenerInit(): Unit = {
    super.onListenerInit()
    val originElements = origin.elemProperties
    lastValue = CrossCollections.toCrossArray(originElements.filter(el => matcher(el.get)))
    originElements.foreach { el => originListeners += el.listen(_ => elementChanged(el)) }
  }

  override protected def onListenerDestroy(): Unit = {
    super.onListenerDestroy()
    originListeners.foreach(_.cancel())
    originListeners.clear()
    lastValue = null
  }

  override protected def originStructureListener(patch: Patch[ElemType]): Unit = {
    // update origin elements listeners
    patch.removed.indices.foreach { i => originListeners(i + patch.idx).cancel() }
    val newListeners = patch.added.map { el => el.listen(_ => elementChanged(el)) }
    CrossCollections.replaceSeq(originListeners, patch.idx, patch.removed.size, newListeners)

    // update last value
    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get)) //todo
    if (added.nonEmpty || removed.nonEmpty) {
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      CrossCollections.replaceSeq(lastValue, idx, removed.size, added)
      fireElementsListeners(Patch[ElemType](idx, removed, added))
      valueChanged()
    }
  }

  private def elementChanged(p: ElemType): Unit = {
    val matches = matcher(p.get)
    val patch: Opt[Patch[ElemType]] =
      lastValue.indexOfOpt(p) match {
        case Opt(oldIdx) =>
          if (matches) {
            //value changed, but still matching
            valueChanged()
            Opt.Empty
          } else {
            //value stopped matching
            lastValue.remove(oldIdx, 1)
            Patch[ElemType](oldIdx, Seq(p), Seq.empty).opt
          }
        case Opt.Empty => {
          val originProps = origin.elemProperties
          val newIdx = originProps.slice(0, originProps.indexOf(p)).count(el => matcher(el.get)) //todo don't call matcher
          CrossCollections.replace(lastValue, newIdx, 0, p)
          Patch[ElemType](newIdx, Seq.empty, Seq(p))
        }.optIf(matches) //value started matching
      }

    patch.foreach { p =>
      fireElementsListeners(p)
      valueChanged()
    }
  }

  //todo different initialization scheme than forwarders
  override def elemProperties: BSeq[ElemType] =
    if (lastValue != null) lastValue.toVector
    else origin.elemProperties.filter(el => matcher(el.get))

  override def get: BSeq[A] =
    elemProperties.map(_.get)
}
