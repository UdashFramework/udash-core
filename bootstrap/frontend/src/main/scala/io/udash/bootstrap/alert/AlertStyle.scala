package io.udash.bootstrap
package alert

import io.udash.css.CssStyleName

final class AlertStyle(style: CssStyleName) extends ClassModifier(BootstrapStyles.Alert.alert, style)

object AlertStyle {
  import BootstrapStyles.Alert._

  val Success = new AlertStyle(alertSuccess)
  val Info = new AlertStyle(alertInfo)
  val Warning = new AlertStyle(alertWarning)
  val Danger = new AlertStyle(alertDanger)
  val Link = new AlertStyle(alertLink)
}
