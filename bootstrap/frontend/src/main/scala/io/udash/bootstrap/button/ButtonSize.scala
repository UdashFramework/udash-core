package io.udash.bootstrap
package button

import io.udash.css.CssStyleName

final class ButtonSize(sizeStyle: Option[CssStyleName]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyleName) =
    this(Some(sizeStyle))
}

object ButtonSize {
  import BootstrapStyles.Button._

  val Default = new ButtonSize(None)
  val Large = new ButtonSize(btnLg)
  val Small = new ButtonSize(btnSm)
  val ExtraSmall = new ButtonSize(btnXs)
}
