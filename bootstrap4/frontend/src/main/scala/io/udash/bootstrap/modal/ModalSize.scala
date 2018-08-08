package io.udash.bootstrap
package modal

import io.udash.css.CssStyle

final class ModalSize(sizeStyle: Option[CssStyle]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyle) = this(Some(sizeStyle))
}

object ModalSize {
  import BootstrapStyles.Modal

  final val Default = new ModalSize(None)
  final val Large = new ModalSize(Modal.large)
  final val Small = new ModalSize(Modal.small)
}
