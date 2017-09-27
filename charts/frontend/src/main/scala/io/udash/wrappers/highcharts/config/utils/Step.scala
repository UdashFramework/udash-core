package io.udash.wrappers.highcharts
package config
package utils

sealed class Step(val name: String)

object Step {
  val Left = new Step("left")
  val Center = new Step("center")
  val Right = new Step("right")
  case class Custom(override val name: String) extends Step(name)
}
