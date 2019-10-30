package io.udash.properties
package seq

import com.avsystem.commons._
import io.udash.properties.single._
import io.udash.utils.{CrossCollections, Registration}

private[properties] abstract class BaseReadableSeqPropertyFromSingleValue[A, B: PropertyCreator, ElemType <: ReadableProperty[B]](
  origin: ReadableProperty[A], transformer: A => BSeq[B], listenChildren: Boolean
) extends AbstractReadableSeqProperty[B, ElemType] {

  override final val id: PropertyId = PropertyCreator.newID()
  override final protected[properties] def parent: ReadableProperty[_] = null

  private final val children = CrossCollections.createArray[Property[B]]
  private final val childrenRegistrations = MHashMap.empty[PropertyId, Registration]
  private final var originListenerRegistration: Registration = _
  private final var lastOriginValue: Opt[A] = Opt.empty

  override final def get: BSeq[B] = {
    if ((originListenerRegistration == null || !originListenerRegistration.isActive) && childrenRegistrations.isEmpty)
      transformer(origin.get)
    else children.map(_.get)
  }

  override final def listen(valueListener: BSeq[B] => Any, initUpdate: Boolean): Registration = {
    initOriginListeners()
    super.listen(valueListener, initUpdate)
  }

  override final def listenOnce(valueListener: BSeq[B] => Any): Registration = {
    initOriginListeners()
    super.listenOnce(valueListener)
  }

  override final def listenStructure(structureListener: Patch[ElemType] => Any): Registration = {
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
      if (listenChildren) childrenRegistrations ++= added.map(p => p.id -> p.listen(_ => valueChanged()))
      CrossCollections.replaceSeq(children, commonBegin, 0, added)
      Some(Patch[ElemType](commonBegin, Seq(), added.map(toElemProp), clearsProperty = false))
    } else if (transformed.size < current.size) {
      val removed = CrossCollections.slice(children, commonBegin, commonBegin + current.size - transformed.size)
      if (listenChildren) removed.foreach(p => childrenRegistrations.remove(p.id).get.cancel())
      CrossCollections.replace(children, commonBegin, current.size - transformed.size)
      Some(Patch[ElemType](commonBegin, removed.map(toElemProp).toSeq, Seq(), transformed.isEmpty))
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

  private def updateIfNeeded(): Unit = {
    if (originListenerRegistration == null) {
      val originValue = origin.get
      if (!lastOriginValue.contains(originValue)) update(originValue)
    }
  }

  private def initOriginListeners(): Unit = {
    if (originListenerRegistration == null || !originListenerRegistration.isActive) {
      listeners.clear()
      updateIfNeeded()
      originListenerRegistration = origin.listen(update)
    }
  }

  private def killOriginListeners(): Unit = {
    if (originListenerRegistration != null && listeners.isEmpty
      && structureListeners.isEmpty && children.forall(_.listenersCount() == 0)) {
      originListenerRegistration.cancel()
      originListenerRegistration = null
    }
  }

  override final protected[properties] def listenersUpdate(): Unit = {
    super.listenersUpdate()
    initOriginListeners()
    killOriginListeners()
  }

  override final protected def wrapListenerRegistration(reg: Registration): Registration =
    super.wrapListenerRegistration(new Registration {
      override def restart(): Unit = {
        initOriginListeners()
        if (listenChildren && childrenRegistrations.isEmpty) {
          childrenRegistrations ++= children.map(p => p.id -> p.listen(_ => valueChanged()))
        }
        reg.restart()
      }

      override def cancel(): Unit = {
        reg.cancel()
        if (listenChildren) {
          childrenRegistrations.valuesIterator.foreach(_.cancel())
          childrenRegistrations.clear()
        }
        killOriginListeners()
      }

      override def isActive: Boolean =
        reg.isActive
    })

  override final def elemProperties: BSeq[ElemType] = {
    updateIfNeeded()
    children.map(toElemProp)
  }
}

private[properties] final class ReadableSeqPropertyFromSingleValue[A, B: PropertyCreator](
  origin: ReadableProperty[A], transformer: A => BSeq[B]
) extends BaseReadableSeqPropertyFromSingleValue[A, B, ReadableProperty[B]](origin, transformer, listenChildren = false) {

  override protected def toElemProp(p: Property[B]): ReadableProperty[B] =
    p.readable
}

private[properties] final class SeqPropertyFromSingleValue[A, B: PropertyCreator](
  origin: Property[A], transformer: A => BSeq[B], revert: BSeq[B] => A
) extends BaseReadableSeqPropertyFromSingleValue[A, B, Property[B]](origin, transformer, listenChildren = true)
  with AbstractSeqProperty[B, Property[B]] {

  protected def toElemProp(p: Property[B]): Property[B] = p

  override protected[properties] def valueChanged(): Unit = {
    CallbackSequencer().queue(s"revertSet:$id", () => {
      origin.set(revert(get))
    })
    super.valueChanged()
  }

  override def replaceSeq(idx: Int, amount: Int, values: BSeq[B]): Unit = {
    import scala.collection.compat._

    val current = get.to(MListBuffer)
    current.remove(idx, amount)
    current.insertAll(idx, values)
    origin.set(revert(current))
  }

  override def set(t: BSeq[B], force: Boolean = false): Unit =
    origin.set(revert(t), force)

  override def setInitValue(t: BSeq[B]): Unit =
    origin.setInitValue(revert(t))

  override def touch(): Unit =
    origin.touch()

}
