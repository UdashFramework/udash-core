package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class TickmarkPlacement(val name: String)

object TickmarkPlacement {
  case object On extends TickmarkPlacement("on")
  case object Between extends TickmarkPlacement("between")
  case class Custom(override val name: String) extends TickmarkPlacement(name)
}