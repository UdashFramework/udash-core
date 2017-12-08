package io.udash.properties

import io.udash.utils.Registration

private[udash]
class CrossRegistration[ElementType](s: CrossCollections.Array[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit = s -= el
  override def restart(): Unit = s += el
  override def isActive: Boolean = s.contains(el)
}
