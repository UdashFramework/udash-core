/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.guage

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
trait SeriesGaugePivot extends js.Object {
  /**
    * The background color or fill of the pivot.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/gauge-pivot/" target="_blank">Pivot options demonstrated</a>
    */
  val backgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The border or stroke color of the pivot. In able to change this, the borderWidth must also be set to something other than the default 0.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/gauge-pivot/" target="_blank">Pivot options demonstrated</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The border or stroke width of the pivot.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/gauge-pivot/" target="_blank">Pivot options demonstrated</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * The pixel radius of the pivot.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/gauge-pivot/" target="_blank">Pivot options demonstrated</a>
    */
  val radius: js.UndefOr[Double] = js.undefined
}

object SeriesGaugePivot {
  import scala.scalajs.js.JSConverters._

  /**
    * @param backgroundColor The background color or fill of the pivot.
    * @param borderColor The border or stroke color of the pivot. In able to change this, the borderWidth must also be set to something other than the default 0.
    * @param borderWidth The border or stroke width of the pivot.
    * @param radius The pixel radius of the pivot.
    */
  def apply(backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            radius: js.UndefOr[Double] = js.undefined): SeriesGaugePivot = {
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val radiusOuter = radius

    new SeriesGaugePivot {
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val radius = radiusOuter
    }
  }
}
