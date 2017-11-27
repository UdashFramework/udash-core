package io.udash.wrappers.highcharts
package config
package utils

sealed class InOutPosition(val name: String)

object InOutPosition {
  val Inside = new InOutPosition("inside")
  val Outside = new InOutPosition("outside")
  case class Custom(override val name: String) extends InOutPosition(name)
}