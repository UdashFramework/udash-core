package io.udash.bootstrap
package panel

import io.udash.css.CssStyle

final class PanelStyle(style: CssStyle) extends ClassModifier(style)

object PanelStyle {
  import BootstrapStyles.Panel._

  val Default = new PanelStyle(panelDefault)
  val Primary = new PanelStyle(panelPrimary)
  val Success = new PanelStyle(panelSuccess)
  val Info = new PanelStyle(panelInfo)
  val Warning = new PanelStyle(panelWarning)
  val Danger = new PanelStyle(panelDanger)
}