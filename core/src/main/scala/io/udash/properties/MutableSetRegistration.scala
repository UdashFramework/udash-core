package io.udash.properties

import com.avsystem.commons._
import io.udash.utils.Registration

private[udash] class MutableSetRegistration[ElementType](
  s: MSet[ElementType], el: ElementType,
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
