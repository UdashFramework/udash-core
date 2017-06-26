package io.udash.bootstrap
package modal

import io.udash.css.CssStyle

sealed abstract class ModalSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq:_*) {
  def this(sizeStyle: CssStyle) = this(Some(sizeStyle))
}

object ModalSize {
  import BootstrapStyles.Modal._

  case object Default extends ModalSize(None)
  case object Large extends ModalSize(modalLarge)
  case object Small extends ModalSize(modalSmall)
}
