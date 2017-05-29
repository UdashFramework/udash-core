package io.udash.bootstrap
package panel

import io.udash.css.CssStyle

sealed abstract class PanelStyle(style: CssStyle) extends ClassModifier(style)

object PanelStyle {
  import BootstrapStyles.Panel._

  case object Default extends PanelStyle(panelDefault)
  case object Primary extends PanelStyle(panelPrimary)
  case object Success extends PanelStyle(panelSuccess)
  case object Info extends PanelStyle(panelInfo)
  case object Warning extends PanelStyle(panelWarning)
  case object Danger extends PanelStyle(panelDanger)
}