package io.udash.properties
package seq

import com.avsystem.commons.misc.Opt
import io.udash.properties.single._
import io.udash.utils.{CrossCollections, Registration}

import scala.collection.mutable

private[properties] abstract class BaseReadableSeqPropertyFromSingleValue[A, B: PropertyCreator, ElemType <: ReadableProperty[B]](
  origin: ReadableProperty[A], transformer: A => Seq[B]
) extends AbstractReadableSeqProperty[B, ElemType] {

  override final val id: PropertyId = PropertyCreator.newID()
  override final protected[properties] def parent: ReadableProperty[_] = null

  private final val children = CrossCollections.createArray[Property[B]]
  private final val childrenRegistrations = mutable.HashMap.empty[PropertyId, Registration]
  private final var originListenerRegistration: Registration = _
  private final var lastOriginValue: Opt[A] = Opt.empty

  override def get: Seq[B] = {
    if ((originListenerRegistration == null || !originListenerRegistration.isActive) && childrenRegistrations.isEmpty)
      transformer(origin.get)
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
      childrenRegistrations ++= added.map(p => p.id -> p.listen(_ => valueChanged()))
      CrossCollections.replace(children, commonBegin, 0, added: _*)
      Some(Patch[ElemType](commonBegin, Seq(), added.map(toElemProp), clearsProperty = false))
    } else if (transformed.size < current.size) {
      val removed = CrossCollections.slice(children, commonBegin, commonBegin + current.size - transformed.size)
      removed.iterator.map(p => childrenRegistrations.remove(p.id).get).foreach(_.cancel())
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
        if (childrenRegistrations.isEmpty) {
          childrenRegistrations ++= children.map(p => p.id -> p.listen(_ => valueChanged()))
        }
        reg.restart()
      }

      override def cancel(): Unit = {
        reg.cancel()
        childrenRegistrations.valuesIterator.foreach(_.cancel())
        childrenRegistrations.clear()
        killOriginListeners()
      }

      override def isActive: Boolean =
        reg.isActive
    })

  override def elemProperties: Seq[ElemType] = {
    updateIfNeeded()
    children.map(toElemProp)
  }
}

private[properties] final class ReadableSeqPropertyFromSingleValue[A, B: PropertyCreator](
  origin: ReadableProperty[A], transformer: A => Seq[B]
) extends BaseReadableSeqPropertyFromSingleValue[A, B, ReadableProperty[B]](origin, transformer) {

  override protected def toElemProp(p: Property[B]): ReadableProperty[B] =
    p.readable
}

private[properties] final class SeqPropertyFromSingleValue[A, B: PropertyCreator](
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

}
