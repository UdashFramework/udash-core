package io.udash.properties
package seq

import com.avsystem.commons.misc.Opt
import io.udash.properties.single._
import io.udash.utils.{CrossCollections, Registration}

import scala.collection.mutable

private[properties] abstract class BaseReadableSeqPropertyFromSingleValue[A, B: PropertyCreator, ElemType <: ReadableProperty[B]](
  origin: ReadableProperty[A], transformer: A => Seq[B]
) extends AbstractReadableSeqProperty[B, ElemType] {

  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] def parent: ReadableProperty[_] = null

  protected final val children = CrossCollections.createArray[Property[B]]
  private var originListenerRegistration: Registration = _
  protected var lastOriginValue: Opt[A] = Opt.empty

  override def get: Seq[B] = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) transformer(origin.get)
    else children.map(_.get)
  }

  override def listen(valueListener: Seq[B] => Any, initUpdate: Boolean): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override def listenOnce(valueListener: Seq[B] => Any): Registration = {
    initOriginListeners()
    super.listenOnce(valueListener)
  }

  override def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
    initOriginListeners()
    super.listenStructure(structureListener)
  }

  private def structureChanged(patch: Patch[ElemType]): Unit =
    CallbackSequencer().queue(
      s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
      () => structureListeners.foreach(_.apply(patch))
    )

  private def update(v: A): Unit = {
    lastOriginValue = Opt(v)

    val transformed = transformer(v)
    val current = children.map(_.get)

    def commonIdx(s1: Iterator[B], s2: Iterator[B]): Int =
      math.max(0,
        s1.zipAll(s2, null, null).zipWithIndex
          .indexWhere { case ((x, y), _) => x != y })

    val commonBegin = commonIdx(transformed.iterator, current.iterator)
    val commonEnd = commonIdx(transformed.reverseIterator, current.reverseIterator)

    val patch = if (transformed.size > current.size) {
      val added: Seq[CastableProperty[B]] = Seq.tabulate(transformed.size - current.size) { idx =>
        PropertyCreator[B].newProperty(transformed(current.size + idx), this)
      }
      CrossCollections.replace(children, commonBegin, 0, added: _*)
      Some(Patch[ElemType](commonBegin, Seq(), added.map(toElemProp), clearsProperty = false))
    } else if (transformed.size < current.size) {
      val removed = CrossCollections.slice(children, commonBegin, commonBegin + current.size - transformed.size)
      CrossCollections.replace(children, commonBegin, current.size - transformed.size)
      Some(Patch[ElemType](commonBegin, removed.map(toElemProp), Seq(), transformed.isEmpty))
    } else None

    CallbackSequencer().sequence {
      transformed.zip(children)
        .slice(commonBegin, math.max(commonBegin + transformed.size - current.size, transformed.size - commonEnd))
        .foreach { case (pv, p) => p.set(pv) }
      patch.foreach(structureChanged)
      valueChanged()
    }
  }

  protected def toElemProp(p: Property[B]): ElemType

  protected def updateIfNeeded(): Unit = {
    if (originListenerRegistration == null) {
      val originValue = origin.get
      if (!lastOriginValue.contains(originValue)) update(originValue)
    }
  }

  protected def initOriginListeners(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      listeners.clear()
      updateIfNeeded()
      originListenerRegistration = origin.listen(update)
    }
  }

  protected def killOriginListeners(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty
      && structureListeners.isEmpty && children.forall(_.listenersCount() == 0)) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
  }

  override protected[properties] def listenersUpdate(): Unit = {
    super.listenersUpdate()
    initOriginListeners()
    killOriginListeners()
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

private[properties] class ReadableSeqPropertyFromSingleValue[A, B : PropertyCreator](
  origin: ReadableProperty[A], transformer: A => Seq[B]
) extends BaseReadableSeqPropertyFromSingleValue[A, B, ReadableProperty[B]](origin, transformer) {
  override def elemProperties: Seq[ReadableProperty[B]] = {
    updateIfNeeded()
    children.map(_.readable)
  }

  protected def toElemProp(p: Property[B]): ReadableProperty[B] =
    p.readable
}

private[properties] class SeqPropertyFromSingleValue[A, B : PropertyCreator](
  origin: Property[A], transformer: A => Seq[B], revert: Seq[B] => A
) extends BaseReadableSeqPropertyFromSingleValue[A, B, Property[B]](origin, transformer)
  with AbstractSeqProperty[B, Property[B]] {

  protected def toElemProp(p: Property[B]): Property[B] = p

  override protected[properties] def valueChanged(): Unit = {
    CallbackSequencer().queue(s"revertSet:$id", () => {
      origin.set(revert(get))
    })
    super.valueChanged()
  }

  override def replace(idx: Int, amount: Int, values: B*): Unit = {
    val current = mutable.ListBuffer(get: _*)
    current.remove(idx, amount)
    current.insertAll(idx, values)
    origin.set(revert(current))
  }

  override def set(t: Seq[B], force: Boolean = false): Unit =
    origin.set(revert(t), force)

  override def setInitValue(t: Seq[B]): Unit =
    origin.setInitValue(revert(t))

  override def touch(): Unit =
    origin.touch()

  override def elemProperties: Seq[Property[B]] = {
    updateIfNeeded()
    children
  }
}
