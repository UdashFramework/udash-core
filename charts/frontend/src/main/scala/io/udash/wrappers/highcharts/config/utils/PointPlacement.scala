package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js.|

sealed class PointPlacement(val name: String | Double)

object PointPlacement {
  val OnTick = new PointPlacement("on")
  val BetweenPrev = new PointPlacement(-0.5)
  val BetweenNext = new PointPlacement(0.5)
  case class Custom(override val name: String | Double) extends PointPlacement(name)
}
