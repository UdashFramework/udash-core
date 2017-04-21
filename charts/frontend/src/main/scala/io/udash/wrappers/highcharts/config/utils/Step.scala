package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class Step(val name: String)

object Step {
  case object Left extends Step("left")
  case object Center extends Step("center")
  case object Right extends Step("right")
  case class Custom(override val name: String) extends Step(name)
}
