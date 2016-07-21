package io.udash.properties
package seq

import java.util.UUID

import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.ExecutionContext

abstract class BaseReadableSeqPropertyFromSingleValue[A, B : ModelValue](origin: ReadableProperty[A], transformer: A => Seq[B],
                                                                         override val executionContext: ExecutionContext)
  extends ReadableSeqProperty[B, ReadableProperty[B]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: Property[_] = null
  protected val structureListeners: mutable.Set[Patch[Property[B]] => Any] = mutable.Set()

  val pc = implicitly[PropertyCreator[B]]
  protected val children = mutable.ListBuffer.empty[Property[B]]

  update(origin.get)
  origin.listen(update)

  private def structureChanged(patch: Patch[Property[B]]): Unit =
    CallbackSequencer.queue(
      s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
      () => structureListeners.foreach(_.apply(patch))
    )

  private def update(v: A): Unit = {
    val transformed = transformer(v)
    val current = get
    val commonBegin = {
      var tmp = 0
      while (tmp < current.size && tmp < transformed.size && current(tmp) == transformed(tmp)) tmp += 1
      tmp
    }
    val commonEnd = {
      var tmp = 0
      while (0 < current.size - tmp && 0 < transformed.size - tmp
        && current(current.size - tmp - 1) == transformed(transformed.size - tmp - 1)) tmp += 1
      tmp
    }

    val patch = if (transformed.size > current.size) {
      val added: Seq[CastableProperty[B]] = Seq.fill(transformed.size - current.size)(pc.newProperty(this)(executionContext))
      children.insertAll(commonBegin, added)
      Patch(commonBegin, Seq(), added, false)
    } else if (transformed.size < current.size) {
      val removed = children.slice(commonBegin, commonBegin + current.size - transformed.size)
      children.remove(commonBegin, current.size - transformed.size)
      Patch(commonBegin, removed, Seq(), transformed.isEmpty)
    } else null

    CallbackSequencer.sequence {
      transformed.zip(children)
        .slice(commonBegin, math.max(commonBegin + transformed.size - current.size, transformed.size - commonEnd))
        .foreach { case (pv, p) => p.set(pv) }
      if (patch != null) structureChanged(patch)
      valueChanged()
    }
  }

  override def get: Seq[B] =
    children.map(_.get)

  override def elemProperties: Seq[ReadableProperty[B]] =
    children
}

class ReadableSeqPropertyFromSingleValue[A, B : ModelValue](origin: ReadableProperty[A], transformer: A => Seq[B],
                                                            override val executionContext: ExecutionContext)
  extends BaseReadableSeqPropertyFromSingleValue(origin, transformer, executionContext) {
  /** Registers listener, which will be called on every property structure change. */
  override def listenStructure(l: (Patch[ReadableProperty[B]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}

class SeqPropertyFromSingleValue[A, B : ModelValue](origin: Property[A], transformer: A => Seq[B], revert: Seq[B] => A,
                                                    override val executionContext: ExecutionContext)
  extends BaseReadableSeqPropertyFromSingleValue[A, B](origin, transformer, executionContext) with SeqProperty[B, Property[B]] {

    override def replace(idx: Int, amount: Int, values: B*): Unit = {
      val current = mutable.ListBuffer(get:_*)
      current.remove(idx, amount)
      current.insertAll(idx, values)
      origin.set(revert(current))
    }

    override def set(t: Seq[B]): Unit =
      origin.set(revert(t))

    override def setInitValue(t: Seq[B]): Unit =
      origin.setInitValue(revert(t))

    override def elemProperties: Seq[Property[B]] =
      children

    override def listenStructure(l: (Patch[Property[B]]) => Any): Registration = {
      structureListeners += l
      new PropertyRegistration(structureListeners, l)
    }
  }
