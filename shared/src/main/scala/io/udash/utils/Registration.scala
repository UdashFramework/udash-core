package io.udash.utils

/** Should be returned from every callback registration method in Udash. */
trait Registration {
  /** Removes registered callback */
  def cancel(): Unit
}
