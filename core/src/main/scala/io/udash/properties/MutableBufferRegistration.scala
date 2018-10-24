package io.udash.properties

import com.avsystem.commons.misc.Opt
import io.udash.utils.Registration

import scala.collection.mutable

private[udash] class MutableBufferRegistration[ElementType](
  s: mutable.Buffer[ElementType], el: ElementType,
  statusChangeListener: Opt[() => Unit]
) extends Registration {
  override def cancel(): Unit = {
    s -= el
    statusChangeListener.foreach(_.apply())
  }

  override def restart(): Unit = {
    s += el
    statusChangeListener.foreach(_.apply())
  }

  override def isActive: Boolean = s.contains(el)
}
