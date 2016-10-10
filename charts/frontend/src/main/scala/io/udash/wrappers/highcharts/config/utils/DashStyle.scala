package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class DashStyle(val name: String)

object DashStyle {
  case object Solid extends DashStyle("Solid")
  case object ShortDash extends DashStyle("ShortDash")
  case object ShortDot extends DashStyle("ShortDot")
  case object ShortDashDot extends DashStyle("ShortDashDot")
  case object ShortDashDotDot extends DashStyle("ShortDashDotDot")
  case object Dot extends DashStyle("Dot")
  case object Dash extends DashStyle("Dash")
  case object LongDash extends DashStyle("LongDash")
  case object DashDot extends DashStyle("DashDot")
  case object LongDashDot extends DashStyle("LongDashDot")
  case object LongDashDotDot extends DashStyle("LongDashDotDot")
  case class Custom(override val name: String) extends DashStyle(name)
}