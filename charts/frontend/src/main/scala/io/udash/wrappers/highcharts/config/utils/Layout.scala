package io.udash.wrappers.highcharts
package config
package utils

sealed class Layout(val name: String)

object Layout {
  val Horizontal = new Layout("horizontal")
  val Vertical = new Layout("vertical")
  case class Custom(override val name: String) extends Layout(name)
}
