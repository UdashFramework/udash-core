package io.udash.utils

import scala.collection.mutable
import scala.util.Try

/**
  * Helper class for callbacks handling.
  * The callbacks are executed in order of registration.
  * Each callback is executed once, it swallows exceptions thrown in callbacks.
  */
class CallbacksHandler[ItemType] {
  type CallbackType = PartialFunction[ItemType, Any]

  private val callbacks: mutable.Set[CallbackType] = mutable.LinkedHashSet.empty

  /** Registers callback and returns `Registration`.
    * Registration operations don't preserve callbacks order. */
  def register(callback: CallbackType): Registration = callbacks.synchronized {
    callbacks += callback
    new SetRegistration(callbacks, callback)
  }

  /** Calls each registered callback and swallows exceptions thrown in callbacks. */
  def fire(item: ItemType): Unit = callbacks.synchronized {
    callbacks.foreach { pf =>
      /** From PF docs:
        * Note that expression `pf.applyOrElse(x, default)` is equivalent to
        *  {{{ if(pf isDefinedAt x) pf(x) else default(x) }}}
        * except that `applyOrElse` method can be implemented more efficiently.
        */
      Try(pf.applyOrElse(item, (_: ItemType) => ()))
    }
  }
}
