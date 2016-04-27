package io.udash.utils

/** Creates reversed partial function. */
object Bidirectional {
  def apply[A, B](pf: PartialFunction[A, B]): (PartialFunction[A, B], PartialFunction[B, A]) = macro io.udash.macros.BidirectionalMacro.impl[A, B]
}