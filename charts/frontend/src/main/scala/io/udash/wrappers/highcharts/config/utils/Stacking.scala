package io.udash.wrappers.highcharts
package config
package utils

sealed class Stacking(val name: String)

object Stacking {
  val Disabled = new Stacking(null)
  val Normal = new Stacking("normal")
  val Percent = new Stacking("percent")
  case class Custom(override val name: String) extends Stacking(name)
}