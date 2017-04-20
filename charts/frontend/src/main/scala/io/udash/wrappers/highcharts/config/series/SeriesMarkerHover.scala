/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
class BaseSeriesMarkerHover extends js.Object {

  /**
    * Enable or disable the point marker.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-hover-enabled/" target="_blank">Disabled hover state</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * The width of the point marker's outline.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-hover-linewidth/" target="_blank">3px line width</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  /**
    * The additional line width for a hovered point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-linewidthplus/" target="_blank">2 pixels wider on hover</a>
    */
  val lineWidthPlus: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesMarkerHover extends BaseSeriesMarkerHover {

  /**
    * The fill color of the marker in hover state.
    */
  val fillColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the point marker's outline. When <code>null</code>, the series' or point's color is used.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-hover-linecolor/" target="_blank">White fill color, black line color</a>
    */
  val lineColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The radius of the point marker. In hover state, it defaults to the normal state's radius + 2 as
    * per the <a href="#plotOptions.series.marker.states.hover.radiusPlus">radiusPlus</a> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-marker-states-hover-radius/" target="_blank">10px radius</a>
    */
  val radius: js.UndefOr[Double] = js.undefined

  /**
    * The number of pixels to increase the radius of the hovered point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-linewidthplus/" target="_blank">5 pixels greater radius on hover</a>
    */
  val radiusPlus: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesDataMarkerHover extends BaseSeriesMarkerHover {
  /**
    * Options for the halo appearing around the hovered point in line-type series as well as outside the hovered slice in pie charts.
    * By default the halo is filled by the current point or series color with an opacity of 0.25.
    *
    * The halo can be disabled by setting the halo option to false.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/halo/"="_blank">Halo options</a>
    */
  val halo: js.UndefOr[Boolean | SeriesHoverHalo] = js.undefined
}

object SeriesMarkerHover {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled       Enable or disable the point marker.
    * @param fillColor     The fill color of the marker in hover state.
    * @param lineColor     The color of the point marker's outline. When <code>null</code>, the series' or point's color is used.
    * @param lineWidth     The width of the point marker's outline.
    * @param lineWidthPlus The additional line width for a hovered point.
    * @param radius        The radius of the point marker. In hover state, it defaults to the normal state's radius + 2 as per the <a href="#plotOptions.series.marker.states.hover.radiusPlus">radiusPlus</a> option.
    * @param radiusPlus    The number of pixels to increase the radius of the hovered point.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            fillColor: js.UndefOr[Color] = js.undefined,
            lineColor: js.UndefOr[Color] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            lineWidthPlus: js.UndefOr[Double] = js.undefined,
            radius: js.UndefOr[Double] = js.undefined,
            radiusPlus: js.UndefOr[Double] = js.undefined): SeriesMarkerHover = {
    val enabledOuter = enabled
    val fillColorOuter = fillColor.map(_.c)
    val lineColorOuter = lineColor.map(_.c)
    val lineWidthOuter = lineWidth
    val lineWidthPlusOuter = lineWidthPlus
    val radiusOuter = radius
    val radiusPlusOuter = radiusPlus

    new SeriesMarkerHover {
      override val enabled = enabledOuter
      override val fillColor = fillColorOuter
      override val lineColor = lineColorOuter
      override val lineWidth = lineWidthOuter
      override val lineWidthPlus = lineWidthPlusOuter
      override val radius = radiusOuter
      override val radiusPlus = radiusPlusOuter
    }
  }
}

object SeriesDataMarkerHover {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled       Enable or disable the point marker.
    * @param lineWidth     The width of the point marker's outline.
    * @param lineWidthPlus The additional line width for a hovered point.
    * @param halo          Options for the halo appearing around the hovered point in line-type series as well as outside the hovered slice in pie charts.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            lineWidthPlus: js.UndefOr[Double] = js.undefined,
            halo: js.UndefOr[Boolean | SeriesHoverHalo] = js.undefined): SeriesDataMarkerHover = {
    val enabledOuter = enabled
    val lineWidthOuter = lineWidth
    val lineWidthPlusOuter = lineWidthPlus
    val haloOuter = halo

    new SeriesDataMarkerHover {
      override val enabled = enabledOuter
      override val lineWidth = lineWidthOuter
      override val lineWidthPlus = lineWidthPlusOuter
      override val halo = haloOuter
    }
  }
}
