package io.udash.bootstrap
package alert

import io.udash.css.CssStyleName

sealed abstract class AlertStyle(style: CssStyleName) extends ClassModifier(BootstrapStyles.Alert.alert, style)

object AlertStyle {
  import BootstrapStyles.Alert._

  case object Success extends AlertStyle(alertSuccess)
  case object Info extends AlertStyle(alertInfo)
  case object Warning extends AlertStyle(alertWarning)
  case object Danger extends AlertStyle(alertDanger)
  case object Link extends AlertStyle(alertLink)
}
