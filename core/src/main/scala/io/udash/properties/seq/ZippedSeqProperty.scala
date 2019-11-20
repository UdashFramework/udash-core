package io.udash.properties.seq

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.{CrossCollections, Registration}

private[properties] abstract class ZippedSeqPropertyUtils[O] extends AbstractReadableSeqProperty[O, ReadableProperty[O]] {
  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  protected var children: MBuffer[ReadableProperty[O]] = _
  private val originStructureListener: Patch[ReadableProperty[_]] => Unit =
    (patch: Patch[ReadableProperty[_]]) => {
      val removed = CrossCollections.slice(children, patch.idx, children.length)
      val added = updatedPart(patch.idx)
      if (added.nonEmpty || removed.nonEmpty) {
        CrossCollections.replaceSeq(children, patch.idx, removed.size, added)
        val mappedPatch = Patch(patch.idx, removed.toSeq, added, patch.clearsProperty)
        CallbackSequencer().queue(
          s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
          () => structureListeners.foreach(_.apply(mappedPatch))
        )
        valueChanged()
      }
    }

  protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]]

  protected def initOriginListeners(structureListener: Patch[ReadableProperty[_]] => Unit = originStructureListener): Unit
  protected def killOriginListeners(): Unit

  override def get: BSeq[O] = {
    (if (children != null) children else updatedPart(0)).map(_.get)
  }

  override def elemProperties: BSeq[ReadableProperty[O]] = {
    (if (children != null) children else updatedPart(0)).toVector
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

private[properties] final class ZippedReadableSeqProperty[A, B, O: PropertyCreator](
  s: ReadableSeqProperty[A, ReadableProperty[A]],
  p: ReadableSeqProperty[B, ReadableProperty[B]],
  combiner: (A, B) => O, defaults: Opt[(ReadableProperty[A], ReadableProperty[B])]
) extends ZippedSeqPropertyUtils[O] {

  private var sRegistration: Registration = _
  private var pRegistration: Registration = _

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

  override protected def initOriginListeners(structureListener: Patch[ReadableProperty[_]] => Unit): Unit = {
    if (sRegistration == null || pRegistration == null) {
      children = CrossCollections.toCrossArray(updatedPart(0))
      sRegistration = s.listenStructure(structureListener)
      pRegistration = p.listenStructure(structureListener)
    }
  }

  override protected def killOriginListeners(): Unit = {
    if (sRegistration != null && pRegistration != null && listenersCount() == 0 && structureListenersCount() == 0) {
      sRegistration.cancel()
      pRegistration.cancel()
      children = null
      sRegistration = null
      pRegistration = null
    }
  }
}

private[properties] final class ZippedWithIndexReadableSeqProperty[A](s: ReadableSeqProperty[A, ReadableProperty[A]])
  extends ZippedSeqPropertyUtils[(A, Int)] {

  private var registration: Registration = _

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[(A, Int)]] =
    s.elemProperties.iterator.zipWithIndex.drop(fromIdx).map { case (x, y) => x.transform(v => (v, y)) }.toSeq

  override protected def initOriginListeners(structureListener: Patch[ReadableProperty[_]] => Unit): Unit = {
    if (registration == null || !registration.isActive) {
      children = CrossCollections.toCrossArray(updatedPart(0))
      registration = s.listenStructure(structureListener)
    }
  }

  override protected def killOriginListeners(): Unit = {
    if (registration != null && listenersCount() == 0 && structureListenersCount() == 0) {
      children = null
      registration.cancel()
      registration = null
    }
  }
}