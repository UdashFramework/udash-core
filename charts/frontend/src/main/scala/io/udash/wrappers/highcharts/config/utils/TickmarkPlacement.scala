package io.udash.wrappers.highcharts
package config
package utils

sealed class TickmarkPlacement(val name: String)

object TickmarkPlacement {
  val On = new TickmarkPlacement("on")
  val Between = new TickmarkPlacement("between")
  case class Custom(override val name: String) extends TickmarkPlacement(name)
}