package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class InOutPosition(val name: String)

object InOutPosition {
  case object Inside extends InOutPosition("inside")
  case object Outside extends InOutPosition("outside")
  case class Custom(override val name: String) extends InOutPosition(name)
}