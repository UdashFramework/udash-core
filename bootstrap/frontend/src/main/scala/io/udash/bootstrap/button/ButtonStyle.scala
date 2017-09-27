package io.udash.bootstrap
package button

import io.udash.css.CssStyle

sealed class ButtonStyle(style: CssStyle) extends ClassModifier(BootstrapStyles.Button.btn, style)

object ButtonStyle {
  import BootstrapStyles.Button._

  val Default = new ButtonStyle(btnDefault)
  val Primary = new ButtonStyle(btnPrimary)
  val Success = new ButtonStyle(btnSuccess)
  val Info = new ButtonStyle(btnInfo)
  val Warning = new ButtonStyle(btnWarning)
  val Danger = new ButtonStyle(btnDanger)
  val Link = new ButtonStyle(btnLink)
}

