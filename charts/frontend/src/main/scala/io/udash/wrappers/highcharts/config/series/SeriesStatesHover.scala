/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
class SeriesStatesHover extends js.Object {

  /**
    * Enable separate styles for the hovered series to visualize that the user hovers either the series itself or the legend.			.
    *
    * @example Disable hover on <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-enabled/" target="_blank">line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-enabled-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-enabled-pie/" target="_blank">pie</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * Options for the halo appearing around the hovered point in line-type series as well as outside the hovered slice in pie charts. By default the halo is filled by the current point or series color with an opacity of 0.25. The halo can be disabled by setting the <code>halo</code> option to <code>false</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/halo/" target="_blank">Halo options</a>
    */
  val halo: js.UndefOr[SeriesHoverHalo] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesAreaStatesHover extends SeriesStatesHover {
  /**
    * Pixel with of the graph line.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-linewidth/" target="_blank">5px line on hover</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  /**
    * The additional line width for the graph of a hovered series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-states-hover-linewidthplus/" target="_blank">5 pixels wider</a>
    */
  val lineWidthPlus: js.UndefOr[Double] = js.undefined

  /**
    * In Highcharts 1.0, the appearance of all markers belonging to the hovered series. For settings on the hover state of the individual point, see <a href="#plotOptions.series.marker.states.hover">marker.states.hover</a>.
    */
  @deprecated("In Highcharts 1.0, the appearance of all markers belonging to the hovered series.", "0.5.0")
  val marker: js.UndefOr[SeriesMarker] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesBarStatesHover extends SeriesStatesHover {
  /**
    * A specific border color for the hovered point. Defaults to inherit the normal state border color.
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * How much to brighten the point on interaction. Requires the main color to be defined in hex or rgb(a) format. Defaults to `0.1`.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-states-hover-brightness/" target="_blank">Brighten by 0.5</a>
    */
  val brightness: js.UndefOr[Double] = js.undefined

  /**
    * A specific color for the hovered point. Defaults to `undefined`.
    */
  val color: js.UndefOr[String | js.Object] = js.undefined
}

object SeriesAreaStatesHover {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled       Enable separate styles for the hovered series to visualize that the user hovers either the series itself or the legend.			.
    * @param halo          Options for the halo appearing around the hovered point in line-type series as well as outside the hovered slice in pie charts. By default the halo is filled by the current point or series color with an opacity of 0.25. The halo can be disabled by setting the <code>halo</code> option to <code>false</code>.
    * @param lineWidth     Pixel with of the graph line.
    * @param lineWidthPlus The additional line width for the graph of a hovered series.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            halo: js.UndefOr[SeriesHoverHalo] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            lineWidthPlus: js.UndefOr[Double] = js.undefined): SeriesAreaStatesHover = {
    val enabledOuter = enabled
    val haloOuter = halo
    val lineWidthOuter = lineWidth
    val lineWidthPlusOuter = lineWidthPlus

    new SeriesAreaStatesHover {
      override val enabled = enabledOuter
      override val halo = haloOuter
      override val lineWidth = lineWidthOuter
      override val lineWidthPlus = lineWidthPlusOuter
    }
  }
}

object SeriesBarStatesHover {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled       Enable separate styles for the hovered series to visualize that the user hovers either the series itself or the legend.			.
    * @param halo          Options for the halo appearing around the hovered point in line-type series as well as outside the hovered slice in pie charts. By default the halo is filled by the current point or series color with an opacity of 0.25. The halo can be disabled by setting the <code>halo</code> option to <code>false</code>.
    * @param borderColor   A specific border color for the hovered point. Defaults to inherit the normal state border color.
    * @param brightness    How much to brighten the point on interaction. Requires the main color to be defined in hex or rgb(a) format. Defaults to `0.1`.
    * @param color        A specific color for the hovered point. Defaults to `undefined`.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            halo: js.UndefOr[SeriesHoverHalo] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            brightness: js.UndefOr[Double] = js.undefined,
            color: js.UndefOr[Color] = js.undefined): SeriesBarStatesHover = {
    val enabledOuter = enabled
    val haloOuter = halo
    val borderColorOuter = borderColor.map(_.c)
    val brightnessOuter = brightness
    val colorOuter = color.map(_.c)

    new SeriesBarStatesHover {
      override val enabled = enabledOuter
      override val halo = haloOuter
      override val borderColor = borderColorOuter
      override val brightness = brightnessOuter
      override val color = colorOuter
    }
  }
}
