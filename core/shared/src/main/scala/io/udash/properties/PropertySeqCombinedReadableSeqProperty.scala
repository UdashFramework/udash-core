package io.udash.properties

import com.avsystem.commons.ISeq
import io.udash.properties.Properties.{Patch, Property, ReadableProperty}
import io.udash.properties.seq.AbstractReadableSeqProperty
import io.udash.utils.Registration

private[properties] final class PropertySeqCombinedReadableSeqProperty[A](value: ISeq[Property[A]])
  extends AbstractReadableSeqProperty[A, ReadableProperty[A]] {

  override val id: PropertyId = PropertyCreator.newID()
  override protected[properties] val parent: ReadableProperty[_] = null

  private val children = CrossCollections.createArray[ReadableProperty[A]]

  value.foreach(property => {
    children.+=(property.transform(value => value))
    property.listen(_ => valueChanged())
  })

  /** @return Current property value. */
  override def get: Seq[A] =
    children.map(_.get)

  /** @return Sequence of child properties. */
  override def elemProperties: Seq[ReadableProperty[A]] =
    children

  /** Registers listener, which will be called on every property structure change. */
  override def listenStructure(structureListener: Patch[ReadableProperty[A]] => Any): Registration = {
    new Registration {
      override def cancel(): Unit = {}
      override def restart(): Unit = {}
      override def isActive: Boolean = true
    }
  }
}
