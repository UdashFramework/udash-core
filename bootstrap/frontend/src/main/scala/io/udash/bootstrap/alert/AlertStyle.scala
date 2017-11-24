package io.udash.bootstrap
package alert

import io.udash.css.CssStyleName

final class AlertStyle(style: CssStyleName) extends ClassModifier(BootstrapStyles.Alert.alert, style)

object AlertStyle {
  import BootstrapStyles.Alert._

  final val Success = new AlertStyle(alertSuccess)
  final val Info = new AlertStyle(alertInfo)
  final val Warning = new AlertStyle(alertWarning)
  final val Danger = new AlertStyle(alertDanger)
  final val Link = new AlertStyle(alertLink)
}
