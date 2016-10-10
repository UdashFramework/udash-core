package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|

sealed abstract class Shadow(val value: Boolean | js.Object)

object Shadow {
  case object Enabled extends Shadow(true)
  case object Disabled extends Shadow(false)
  case class Custom(color: js.UndefOr[Color] = js.undefined,
                    offsetX: js.UndefOr[Double] = js.undefined,
                    offsetY: js.UndefOr[Double] = js.undefined,
                    opacity: js.UndefOr[Double] = js.undefined,
                    width: js.UndefOr[Double] = js.undefined)
    extends Shadow(js.Dynamic.literal(
      color = color.map(_.c).asInstanceOf[js.Any],
      offsetX = offsetX,
      offsetY = offsetY,
      opacity = opacity,
      width = width
    ))
}