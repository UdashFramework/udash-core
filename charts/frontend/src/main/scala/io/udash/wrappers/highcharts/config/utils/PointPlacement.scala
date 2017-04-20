package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js.|

sealed abstract class PointPlacement(val name: String | Double)

object PointPlacement {
  case object OnTick extends PointPlacement("on")
  case object BetweenPrev extends PointPlacement(-0.5)
  case object BetweenNext extends PointPlacement(0.5)
  case class Custom(override val name: String | Double) extends PointPlacement(name)
}
