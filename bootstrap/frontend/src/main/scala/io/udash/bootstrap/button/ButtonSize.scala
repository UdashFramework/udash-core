package io.udash.bootstrap
package button

import io.udash.css.CssStyleName

final class ButtonSize(sizeStyle: Option[CssStyleName]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyleName) =
    this(Some(sizeStyle))
}

object ButtonSize {
  import BootstrapStyles.Button._

  final val Default = new ButtonSize(None)
  final val Large = new ButtonSize(btnLg)
  final val Small = new ButtonSize(btnSm)
  final val ExtraSmall = new ButtonSize(btnXs)
}
