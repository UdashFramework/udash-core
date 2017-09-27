package io.udash.bootstrap
package modal

import io.udash.css.CssStyle

final class ModalSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq:_*) {
  def this(sizeStyle: CssStyle) = this(Some(sizeStyle))
}

object ModalSize {
  import BootstrapStyles.Modal._

  val Default = new ModalSize(None)
  val Large = new ModalSize(modalLarge)
  val Small = new ModalSize(modalSmall)
}
