package io.udash.bootstrap
package collapse

sealed abstract class PanelType(style: BootstrapStyles.BootstrapClass) extends ClassModifier(style)

object PanelType {

  import BootstrapStyles.Panel._

  case object Default extends PanelType(panelDefault)

  case object Primary extends PanelType(panelPrimary)

  case object Success extends PanelType(panelSuccess)

  case object Info extends PanelType(panelInfo)

  case object Warning extends PanelType(panelWarning)

  case object Danger extends PanelType(panelDanger)


}