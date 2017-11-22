package io.udash.utils

import scala.collection.mutable

/** Should be returned from every callback registration method in Udash. */
trait Registration {
  /** Removes registered callback */
  def cancel(): Unit

  /** Registers callback again. */
  def restart(): Unit

  /** Returns `true`, if callback is active. */
  def isActive: Boolean
}

private[udash] class SetRegistration[ElementType](s: mutable.Set[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit = s.synchronized { s -= el }
  override def restart(): Unit = s.synchronized { s += el }
  override def isActive: Boolean = s.synchronized { s.contains(el) }
}