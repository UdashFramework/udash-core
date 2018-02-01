package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js

trait Position extends js.Object {
  val align: js.UndefOr[String] = js.undefined
  val verticalAlign: js.UndefOr[String] = js.undefined
  val x: js.UndefOr[Double] = js.undefined
  val y: js.UndefOr[Double] = js.undefined
}

object Position {

  def apply(align: js.UndefOr[Align] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): Position = {
    val alignOuter = align.map(_.name)
    val xOuter = x
    val verticalAlignOuter = verticalAlign.map(_.name)
    val yOuter = y

    new Position {
      override val align = alignOuter
      override val x = xOuter
      override val verticalAlign = verticalAlignOuter
      override val y = yOuter
    }
  }

  implicit class PositionWrapper(p: Position) {
    def getAlign: Option[Align] =
      p.align.toOption.map(Align.ByName)

    def getVerticalAlign: Option[VerticalAlign] =
      p.verticalAlign.toOption.map(VerticalAlign.ByName)
  }
}
