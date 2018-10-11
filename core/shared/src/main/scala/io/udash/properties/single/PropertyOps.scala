package io.udash.properties.single

object PropertyOps {
  implicit class BooleanPropertyOps(private val underlying: Property[Boolean]) {
    /** Toggles the value of the underlying boolean-backed property.
      * @param force If true, the value change listeners will be fired even if value didn't change.
      * */
    def toggle(force: Boolean = true): Unit = underlying.set(!underlying.get, force)
  }
}
