package io.udash.bootstrap
package button

import io.udash.css.CssStyle

sealed abstract class ButtonStyle(style: CssStyle) extends ClassModifier(BootstrapStyles.Button.btn, style)

object ButtonStyle {
  import BootstrapStyles.Button._

  case object Default extends ButtonStyle(btnDefault)
  case object Primary extends ButtonStyle(btnPrimary)
  case object Success extends ButtonStyle(btnSuccess)
  case object Info extends ButtonStyle(btnInfo)
  case object Warning extends ButtonStyle(btnWarning)
  case object Danger extends ButtonStyle(btnDanger)
  case object Link extends ButtonStyle(btnLink)
}

