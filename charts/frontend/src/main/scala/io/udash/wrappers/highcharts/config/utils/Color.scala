package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js
import scala.scalajs.js.|

case class Color(c: String | js.Object)

object Color {
  def apply(red: Int, green: Int, blue: Int): Color = {
    require(Seq(red, green, blue).forall(i => i >= 0 && i <= 255))
    Color(s"#${Integer.toHexString(red)}${Integer.toHexString(green)}${Integer.toHexString(blue)}")
  }

  def apply(red: Int, green: Int, blue: Int, alpha: Double): Color = {
    require(Seq(red, green, blue).forall(i => i >= 0 && i <= 255))
    require(alpha >= 0 && alpha <= 1)
    Color(s"rgba($red, $green, $blue, $alpha)")
  }

  def gradient(start: (Double, Double), stop: (Double, Double), stops: Seq[(Double, Color)]): Color = {
    import scala.scalajs.js.JSConverters._
    Color(js.Dynamic.literal(
      linearGradient = js.Dynamic.literal(x1 = start._1, x2 = stop._1, y1 = start._2, y2 = stop._2),
      stops = stops.map(v => js.Array(v._1, v._2.c)).toJSArray
    ))
  }

  def radialGradient(cx: Double, cy: Double, r: Double, stops: Seq[(Double, Color)]): Color = {
    import scala.scalajs.js.JSConverters._
    Color(js.Dynamic.literal(
      radialGradient = js.Dynamic.literal(cx = cx, cy = cy, r = r),
      stops = stops.map(v => js.Array(v._1, v._2.c)).toJSArray
    ))
  }
}
