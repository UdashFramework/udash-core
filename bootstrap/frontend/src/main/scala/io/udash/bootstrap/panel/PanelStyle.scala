package io.udash.bootstrap.panel

import io.udash.bootstrap.{BootstrapStyles, ClassModifier}

sealed abstract class PanelStyle(style: BootstrapStyles.BootstrapClass) extends ClassModifier(style)

object PanelStyle {

  import BootstrapStyles.Panel._

  case object Default extends PanelStyle(panelDefault)

  case object Primary extends PanelStyle(panelPrimary)

  case object Success extends PanelStyle(panelSuccess)

  case object Info extends PanelStyle(panelInfo)

  case object Warning extends PanelStyle(panelWarning)

  case object Danger extends PanelStyle(panelDanger)


}