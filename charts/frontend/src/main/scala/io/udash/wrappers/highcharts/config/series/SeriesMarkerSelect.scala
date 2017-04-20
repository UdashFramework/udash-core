/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
class SeriesMarkerSelect extends js.Object {
  /**
    * Enable or disable visible feedback for selection.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-select-enabled/" target="_blank">Disabled select state</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * The fill color of the point marker.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-select-fillcolor/" target="_blank">Solid red discs for selected points</a>
    */
  val fillColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the point marker's outline. When <code>null</code>, the series' or point's color is used.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-select-linecolor/" target="_blank">Red line color for selected points</a>
    */
  val lineColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The width of the point marker's outline.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-select-linewidth/" target="_blank">3px line width for selected points</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  /**
    * The radius of the point marker. In hover state, it defaults
    * to the normal state's radius + 2.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-select-radius/" target="_blank">10px radius for selected points</a>
    */
  val radius: js.UndefOr[Double] = js.undefined
}

object SeriesMarkerSelect {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled   Enable or disable visible feedback for selection.
    * @param fillColor The fill color of the point marker.
    * @param lineColor The color of the point marker's outline. When <code>null</code>, the series' or point's color is used.
    * @param lineWidth The width of the point marker's outline.
    * @param radius    The radius of the point marker. In hover state, it defaults to the normal state's radius + 2.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            fillColor: js.UndefOr[Color] = js.undefined,
            lineColor: js.UndefOr[Color] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            radius: js.UndefOr[Double] = js.undefined): SeriesMarkerSelect = {
    val enabledOuter = enabled
    val fillColorOuter = fillColor.map(_.c)
    val lineColorOuter = lineColor.map(_.c)
    val lineWidthOuter = lineWidth
    val radiusOuter = radius

    new SeriesMarkerSelect {
      override val enabled = enabledOuter
      override val fillColor = fillColorOuter
      override val lineColor = lineColorOuter
      override val lineWidth = lineWidthOuter
      override val radius = radiusOuter
    }
  }
}
