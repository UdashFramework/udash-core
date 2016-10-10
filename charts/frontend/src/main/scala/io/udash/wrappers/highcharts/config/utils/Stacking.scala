package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class Stacking(val name: String)

object Stacking {
  case object Disabled extends Stacking(null)
  case object Normal extends Stacking("normal")
  case object Percent extends Stacking("percent")
  case class Custom(override val name: String) extends Stacking(name)
}