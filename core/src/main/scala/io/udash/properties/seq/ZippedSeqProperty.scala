package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.single.{CombinedProperty, ReadableProperty}
import io.udash.utils.{CrossCollections, Registration}

/**
 *
 * @param sources SeqProperties required for updating this property.
 *                When empty, the origin listeners will be reinitialized on all new registrations
 */
private[properties] abstract class ZippedSeqPropertyUtils[O](
  sources: ISeq[ReadableSeqProperty[_, _ <: ReadableProperty[_]]]
) extends AbstractReadableSeqProperty[O, ReadableProperty[O]] {

  override final protected[properties] def parent: ReadableProperty[_] = null

  private final val children = CrossCollections.createArray[ReadableProperty[O]]
  private final val sourceRegistrations = CrossCollections.createArray[Registration]
  private final val childrenRegistrations = CrossCollections.createArray[Registration]

  private val originStructureListener: Patch[ReadableProperty[_]] => Unit = { patch =>
    val removed = CrossCollections.slice(children, patch.idx, children.length)
    val added = updatedPart(patch.idx)
    if (added.nonEmpty || removed.nonEmpty) {
      CrossCollections.replaceSeq(children, patch.idx, removed.size, added)
      val mappedPatch = Patch(patch.idx, removed.toSeq, added, patch.clearsProperty)
      CallbackSequencer().queue(
        s"$hashCode:fireElementsListeners:${patch.hashCode()}",
        () => structureListeners.foreach(_.apply(mappedPatch))
      )
      valueChanged()
    }
  }

  protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]]

  private def initOriginListeners(): Unit = {
    if (sourceRegistrations.isEmpty) {
      val updated = updatedPart(0)
      children.appendAll(updated)
      childrenRegistrations.appendAll(updated.iterator.map(_.listen(_ => valueChanged())))
      sourceRegistrations.appendAll(sources.iterator.map(_.listenStructure(originStructureListener)))
    }
  }

  private def killOriginListeners(): Unit = {
    if (sourceRegistrations.nonEmpty && listenersCount() == 0 && structureListenersCount() == 0) {
      childrenRegistrations.foreach(_.cancel())
      sourceRegistrations.foreach(_.cancel())

      childrenRegistrations.clear()
      sourceRegistrations.clear()

      children.clear()
    }
  }

  override def get: BSeq[O] = {
    (if (children.nonEmpty) children else updatedPart(0)).map(_.get)
  }

  override def elemProperties: BSeq[ReadableProperty[O]] = {
    (if (children.nonEmpty) children else updatedPart(0)).toVector
  }

  override def listenStructure(structureListener: Patch[ReadableProperty[O]] => Any): Registration = {
    initOriginListeners()
    super.listenStructure(structureListener)
  }

  override def listen(valueListener: BSeq[O] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: BSeq[O] => Any): Registration = {
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

private[properties] final class ZippedReadableSeqProperty[A, B, O](
  s: ReadableSeqProperty[A, ReadableProperty[A]],
  p: ReadableSeqProperty[B, ReadableProperty[B]],
  combiner: (A, B) => O, defaults: Opt[(ReadableProperty[A], ReadableProperty[B])]
) extends ZippedSeqPropertyUtils[O](ISeq(s, p)) {

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]] = {
    val zip: (Iterator[ReadableProperty[A]], Iterator[ReadableProperty[B]]) => Iterator[(ReadableProperty[A], ReadableProperty[B])] = {
      defaults match {
        case Opt((defaultA, defaultB)) => _.zipAll(_, defaultA, defaultB)
        case Opt.Empty => _.zip(_)
      }
    }
    zip(s.elemProperties.iterator.drop(fromIdx), p.elemProperties.iterator.drop(fromIdx))
      .map { case (x, y) => x.combine(y)(combiner) }
      .toSeq
  }
}

private[properties] final class ZippedWithIndexReadableSeqProperty[A](s: ReadableSeqProperty[A, ReadableProperty[A]])
  extends ZippedSeqPropertyUtils[(A, Int)](ISeq(s)) {

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[(A, Int)]] =
    s.elemProperties.iterator.zipWithIndex.drop(fromIdx).map { case (x, y) => x.transform(v => (v, y)) }.toSeq
}