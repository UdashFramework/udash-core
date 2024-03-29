package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.{CrossCollections, Registration}

private[properties] final class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean
) extends ForwarderReadableSeqProperty[A, A, ElemType, ElemType] {

  private val originElementListeners: MBuffer[Registration] = CrossCollections.createArray

  override protected def getFromOrigin(): BSeq[A] = origin.get.filter(matcher)
  override protected def transformElements(elemProperties: BSeq[ElemType]): BSeq[ElemType] =
    elemProperties.filter(p => matcher(p.get))

  override protected def onListenerInit(originElems: BSeq[ElemType]): Unit = {
    super.onListenerInit(originElems)
    originElems.foreach { el => originElementListeners += el.listen(_ => elementChanged(el)) }
  }

  override protected def onListenerDestroy(): Unit = {
    super.onListenerDestroy()
    originElementListeners.foreach(_.cancel())
    originElementListeners.clear()
  }

  override protected def transformPatch(patch: Patch[ElemType]): Opt[Patch[ElemType]] = {
    patch.removed.indices.foreach { i => originElementListeners(i + patch.idx).cancel() }
    val newListeners = patch.added.map { el => el.listen(_ => elementChanged(el)) }
    CrossCollections.replaceSeq(originElementListeners, patch.idx, patch.removed.size, newListeners)

    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get))
    if (added.nonEmpty || removed.nonEmpty) {
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      Patch[ElemType](idx, removed, added).opt
    } else Opt.Empty
  }

  private def elementChanged(p: ElemType): Unit = {
    val matches = matcher(p.get)
    val patch: Opt[Patch[ElemType]] =
      transformedElements.indexOfOpt(p) match {
        case Opt(oldIdx) =>
          if (matches) {
            //value changed, but still matching
            valueChanged()
            Opt.Empty
          } else {
            //value stopped matching
            transformedElements.remove(oldIdx, 1)
            Patch[ElemType](oldIdx, Seq(p), Seq.empty).opt
          }
        case Opt.Empty => {
          val originProps = origin.elemProperties
          val newIdx = originProps.slice(0, originProps.indexOf(p)).count(el => matcher(el.get))
          CrossCollections.replace(transformedElements, newIdx, 0, p)
          Patch[ElemType](newIdx, Seq.empty, Seq(p))
        }.optIf(matches) //value started matching
      }

    patch.foreach { p =>
      fireElementsListeners(p)
      valueChanged()
    }
  }

}
