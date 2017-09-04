package io.udash.utils

import scala.collection.mutable

/** Should be returned from every callback registration method in Udash. */
trait Registration {
  /** Removes registered callback */
  def cancel(): Unit

  /** Registers callback again. */
  def restart(): Unit
}

private[udash] class SetRegistration[ElementType](s: mutable.Set[ElementType], el: ElementType) extends Registration {
  override def cancel(): Unit = s -= el
  override def restart(): Unit = s += el
}