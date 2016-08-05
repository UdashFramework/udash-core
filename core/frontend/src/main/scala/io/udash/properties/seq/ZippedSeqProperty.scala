package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.single.ReadableProperty
import io.udash.properties.{CallbackSequencer, ModelValue, PropertyCreator, PropertyRegistration}
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class ZippedReadableSeqProperty[A, B, O : ModelValue]
                               (s: ReadableSeqProperty[A, ReadableProperty[A]],
                                p: ReadableSeqProperty[B, ReadableProperty[B]],
                                combiner: (A, B) => O,
                                override val executionContext: ExecutionContext)
  extends ReadableSeqProperty[O, ReadableProperty[O]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  private val children = mutable.ListBuffer.empty[ReadableProperty[O]]
  private val structureListeners: mutable.Set[Patch[ReadableProperty[O]] => Any] = mutable.Set()

  protected final def appendChildren(toCombine: Seq[(ReadableProperty[A], ReadableProperty[B])]): Unit =
    toCombine.foreach { case (x, y) => children.append(x.combine(y, this)(combiner)) }

  protected def update(fromIdx: Int): Unit =
    appendChildren(s.elemProperties.zip(p.elemProperties).drop(fromIdx))

  private val originListener = (patch: Patch[ReadableProperty[_]]) => {
    val idx = patch.idx
    val removed = children.slice(patch.idx, children.size)
    children.remove(idx, children.size - idx)
    update(idx)
    val added = children.drop(patch.idx)
    if (added.nonEmpty || removed.nonEmpty) {
      val mappedPatch = Patch(patch.idx, removed, added, patch.clearsProperty)
      CallbackSequencer.queue(
        s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
        () => structureListeners.foreach(_.apply(mappedPatch))
      )
      valueChanged()
    }
  }

  update(0)
  s.listenStructure(originListener)
  p.listenStructure(originListener)

  override def get: Seq[O] =
    children.map(_.get)

  override def elemProperties: Seq[ReadableProperty[O]] =
    children

  override def listenStructure(l: (Patch[ReadableProperty[O]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}

class ZippedAllReadableSeqProperty[A, B, O : ModelValue]
                                  (s: ReadableSeqProperty[A, ReadableProperty[A]],
                                   p: ReadableSeqProperty[B, ReadableProperty[B]],
                                   combiner: (A, B) => O, defaultA: ReadableProperty[A], defaultB: ReadableProperty[B],
                                   override val executionContext: ExecutionContext)
  extends ZippedReadableSeqProperty(s, p, combiner, executionContext) {

  override protected def update(fromIdx: Int): Unit =
    appendChildren(s.elemProperties.zipAll(p.elemProperties, defaultA, defaultB).drop(fromIdx))
}

class ZippedWithIndexReadableSeqProperty[A](s: ReadableSeqProperty[A, ReadableProperty[A]],
                                            override val executionContext: ExecutionContext)
  extends ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  private val children = mutable.ListBuffer.empty[ReadableProperty[(A, Int)]]
  private val structureListeners: mutable.Set[Patch[ReadableProperty[(A, Int)]] => Any] = mutable.Set()

  protected final def appendChildren(toCombine: Seq[(ReadableProperty[A], Int)]): Unit =
    toCombine.foreach { case (x, y) => children.append(x.transform(v => (v, y))) }

  protected def update(fromIdx: Int): Unit =
    appendChildren(s.elemProperties.zipWithIndex.drop(fromIdx))

  protected val originListener = (patch: Patch[ReadableProperty[_]]) => {
    val idx = patch.idx
    val removed = children.slice(patch.idx, children.size)
    children.remove(idx, children.size - idx)
    update(idx)
    val added = children.drop(patch.idx)
    if (added.nonEmpty || removed.nonEmpty) {
      val mappedPatch = Patch(patch.idx, removed, added, patch.clearsProperty)
      CallbackSequencer.queue(
        s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
        () => structureListeners.foreach(_.apply(mappedPatch))
      )
      valueChanged()
    }
  }

  update(0)
  s.listenStructure(originListener)

  override def get: Seq[(A, Int)] =
    children.map(_.get)

  override def elemProperties: Seq[ReadableProperty[(A, Int)]] =
    children

  override def listenStructure(l: (Patch[ReadableProperty[(A, Int)]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}