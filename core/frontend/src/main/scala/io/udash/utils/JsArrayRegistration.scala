package io.udash.utils

import scala.scalajs.js

private[udash] class JsArrayRegistration[ElementType](s: js.Array[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit = s -= el
  override def restart(): Unit = s += el
  override def isActive: Boolean = s.contains(el)
}
