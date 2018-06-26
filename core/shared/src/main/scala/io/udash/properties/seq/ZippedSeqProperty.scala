package io.udash.properties.seq

import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration

import scala.collection.mutable

private[properties] abstract class ZippedSeqPropertyUtils[O] extends AbstractReadableSeqProperty[O, ReadableProperty[O]] {
  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  protected var children: mutable.Buffer[ReadableProperty[O]] = _
  protected final val originListener: Patch[ReadableProperty[_]] => Unit =
    (patch: Patch[ReadableProperty[_]]) => {
      val idx = patch.idx
      val els = children
      val removed = CrossCollections.slice(els, patch.idx, els.length)
      val added = updatedPart(idx)
      CrossCollections.replace(els, idx, els.length - idx, added:_*)
      if (added.nonEmpty || removed.nonEmpty) {
        val mappedPatch = Patch(patch.idx, removed, added, patch.clearsProperty)
        CallbackSequencer().queue(
          s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
          () => structureListeners.foreach(_.apply(mappedPatch))
        )
        valueChanged()
      }
    }

  protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]]

  protected def initOriginListeners(): Unit
  protected def killOriginListeners(): Unit

  override def get: Seq[O] = {
    (if (children != null) children else updatedPart(0)).map(_.get)
  }

  override def elemProperties: Seq[ReadableProperty[O]] = {
    (if (children != null) children else updatedPart(0)).toVector
  }

  override def listenStructure(structureListener: Patch[ReadableProperty[O]] => Any): Registration = {
    initOriginListeners()
    super.listenStructure(structureListener)
  }

  override def listen(valueListener: Seq[O] => Any, initUpdate: Boolean = false): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: Seq[O] => Any): Registration = {
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

private[properties] class ZippedReadableSeqProperty[A, B, O: PropertyCreator](
  s: ReadableSeqProperty[A, ReadableProperty[A]],
  p: ReadableSeqProperty[B, ReadableProperty[B]],
  combiner: (A, B) => O
) extends ZippedSeqPropertyUtils[O] {

  private var sRegistration: Registration = _
  private var pRegistration: Registration = _

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]] = {
    s.elemProperties.drop(fromIdx)
      .zip(p.elemProperties.drop(fromIdx))
      .map { case (x, y) => x.combine(y, this)(combiner) }
  }

  override protected def initOriginListeners(): Unit = {
    if (sRegistration == null || pRegistration == null) {
      children = CrossCollections.toCrossArray(updatedPart(0))
      sRegistration = s.listenStructure(originListener)
      pRegistration = p.listenStructure(originListener)
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

private[properties] class ZippedAllReadableSeqProperty[A, B, O: PropertyCreator](
  s: ReadableSeqProperty[A, ReadableProperty[A]],
  p: ReadableSeqProperty[B, ReadableProperty[B]],
  combiner: (A, B) => O, defaultA: ReadableProperty[A], defaultB: ReadableProperty[B]
) extends ZippedReadableSeqProperty(s, p, combiner) {

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[O]] = {
    s.elemProperties.drop(fromIdx)
      .zipAll(p.elemProperties.drop(fromIdx), defaultA, defaultB)
      .map { case (x, y) => x.combine(y, this)(combiner) }
  }
}

private[properties] class ZippedWithIndexReadableSeqProperty[A](s: ReadableSeqProperty[A, ReadableProperty[A]])
  extends ZippedSeqPropertyUtils[(A, Int)] {

  private var registration: Registration = _

  override protected def updatedPart(fromIdx: Int): Seq[ReadableProperty[(A, Int)]] = {
    s.elemProperties.zipWithIndex.drop(fromIdx)
      .map { case (x, y) => x.transform(v => (v, y)) }
  }

  override protected def initOriginListeners(): Unit = {
    if (registration == null || !registration.isActive) {
      children = CrossCollections.toCrossArray(updatedPart(0))
      registration = s.listenStructure(originListener)
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