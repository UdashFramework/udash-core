package io.udash.bootstrap
package button

import io.udash.css.CssStyleName

sealed abstract class ButtonSize(sizeStyle: Option[CssStyleName]) extends ClassModifier(sizeStyle.toSeq: _*) {
  def this(sizeStyle: CssStyleName) =
    this(Some(sizeStyle))
}

object ButtonSize {
  import BootstrapStyles.Button._

  case object Default extends ButtonSize(None)
  case object Large extends ButtonSize(btnLg)
  case object Small extends ButtonSize(btnSm)
  case object ExtraSmall extends ButtonSize(btnXs)
}
