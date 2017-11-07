package io.udash.utils

import scala.scalajs.js

private[udash] class JsArrayRegistration[ElementType](s: js.Array[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit = s.synchronized { s -= el }
  override def restart(): Unit = s.synchronized { s += el }
  override def isActive(): Boolean = s.synchronized { s.contains(el) }
}
