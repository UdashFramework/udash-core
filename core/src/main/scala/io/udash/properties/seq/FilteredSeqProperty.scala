package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.{CrossCollections, Registration}

private[properties] final class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]](
  override protected val origin: ReadableSeqProperty[A, ElemType], matcher: A => Boolean
) extends ForwarderWithLocalCopy[A, A, ElemType, ElemType] {

  private val originListeners: MBuffer[Registration] = CrossCollections.createArray

  override protected def loadFromOrigin(): BSeq[A] = origin.get.filter(matcher)
  override protected def elementsFromOrigin(elemProperties: BSeq[ElemType]): BSeq[ElemType] =
    elemProperties.filter(p => matcher(p.get))

  override protected def onListenerInit(): Unit = {
    super.onListenerInit()
    lastOriginProperties.foreach { el => originListeners += el.listen(_ => elementChanged(el)) }
  }

  override protected def onListenerDestroy(): Unit = {
    super.onListenerDestroy()
    originListeners.foreach(_.cancel())
    originListeners.clear()
  }

  override protected def transformPatchAndUpdateElements(patch: Patch[ElemType]): Opt[Patch[ElemType]] = {
    patch.removed.indices.foreach { i => originListeners(i + patch.idx).cancel() }
    val newListeners = patch.added.map { el => el.listen(_ => elementChanged(el)) }
    CrossCollections.replaceSeq(originListeners, patch.idx, patch.removed.size, newListeners)

    // update last value
    val added = patch.added.filter(p => matcher(p.get))
    val removed = patch.removed.filter(p => matcher(p.get)) //todo
    if (added.nonEmpty || removed.nonEmpty) {
      val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
      CrossCollections.replaceSeq(transformedElements, idx, removed.size, added)
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
          val newIdx = originProps.slice(0, originProps.indexOf(p)).count(el => matcher(el.get)) //todo don't call matcher
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
