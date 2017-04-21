package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class Layout(val name: String)

object Layout {
  case object Horizontal extends Layout("horizontal")
  case object Vertical extends Layout("vertical")
  case class Custom(override val name: String) extends Layout(name)
}
