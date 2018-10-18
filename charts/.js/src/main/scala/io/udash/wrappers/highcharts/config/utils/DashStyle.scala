package io.udash.wrappers.highcharts
package config
package utils

sealed class DashStyle(val name: String)

object DashStyle {
  val Solid = new DashStyle("Solid")
  val ShortDash = new DashStyle("ShortDash")
  val ShortDot = new DashStyle("ShortDot")
  val ShortDashDot = new DashStyle("ShortDashDot")
  val ShortDashDotDot = new DashStyle("ShortDashDotDot")
  val Dot = new DashStyle("Dot")
  val Dash = new DashStyle("Dash")
  val LongDash = new DashStyle("LongDash")
  val DashDot = new DashStyle("DashDot")
  val LongDashDot = new DashStyle("LongDashDot")
  val LongDashDotDot = new DashStyle("LongDashDotDot")
  case class Custom(override val name: String) extends DashStyle(name)
}