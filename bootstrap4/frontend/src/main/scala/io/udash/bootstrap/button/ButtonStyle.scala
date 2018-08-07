package io.udash.bootstrap
package button

import io.udash.css.CssStyle

final class ButtonStyle(style: CssStyle) extends ClassModifier(BootstrapStyles.Button.btn, style)

object ButtonStyle {
  import BootstrapStyles.Button._

  final val Default = new ButtonStyle(btnDefault)
  final val Primary = new ButtonStyle(btnPrimary)
  final val Success = new ButtonStyle(btnSuccess)
  final val Info = new ButtonStyle(btnInfo)
  final val Warning = new ButtonStyle(btnWarning)
  final val Danger = new ButtonStyle(btnDanger)
  final val Link = new ButtonStyle(btnLink)
}

