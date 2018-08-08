package io.udash.bootstrap
package button

import io.udash.css.CssStyleName

final class ButtonSize(sizeStyle: Option[CssStyleName]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyleName) =
    this(Some(sizeStyle))
}

object ButtonSize {
  import BootstrapStyles.Button

  final val Default = new ButtonSize(None)
  final val Large = new ButtonSize(Button.large)
  final val Small = new ButtonSize(Button.small)
}
