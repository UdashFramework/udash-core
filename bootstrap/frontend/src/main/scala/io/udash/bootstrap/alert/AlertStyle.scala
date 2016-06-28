package io.udash.bootstrap
package alert

sealed abstract class AlertStyle(style: BootstrapStyles.BootstrapClass) extends ClassModifier(BootstrapStyles.Alert.alert, style)

object AlertStyle {

  import BootstrapStyles.Alert._

  case object Success extends AlertStyle(alertSuccess)

  case object Info extends AlertStyle(alertInfo)

  case object Warning extends AlertStyle(alertWarning)

  case object Danger extends AlertStyle(alertDanger)

  case object Link extends AlertStyle(alertLink)

}
