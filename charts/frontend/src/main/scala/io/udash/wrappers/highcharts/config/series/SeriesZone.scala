/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.highcharts.config.utils.{Color, DashStyle}

import scala.scalajs.js
import scala.scalajs.js.|

trait SeriesZone extends js.Object {

  /**
    * A custom class name for the zone.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * Defines the color of the series.
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * A name for the dash style to use for the graph.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/color-zones-dashstyle-dot/" target="_blank">Dashed line indicates prognosis</a>
    */
  val dashStyle: js.UndefOr[String] = js.undefined

  /**
    * Defines the fill color for the series (in area type series)
    */
  val fillColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The value up to where the zone extends, if undefined the zones stretches to the last value in the series.
    */
  val value: js.UndefOr[Double] = js.undefined
}

object SeriesZone {

  /**
    * @param className A custom class name for the zone.
    * @param color     Defines the color of the series.
    * @param dashStyle A name for the dash style to use for the graph.
    * @param fillColor Defines the fill color for the series (in area type series)
    * @param value     The value up to where the zone extends, if undefined the zones stretches to the last value in the series.
    */
  def apply(className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            dashStyle: js.UndefOr[DashStyle] = js.undefined,
            fillColor: js.UndefOr[Color] = js.undefined,
            value: js.UndefOr[Double] = js.undefined): SeriesZone = {
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val dashStyleOuter = dashStyle.map(_.name)
    val fillColorOuter = fillColor.map(_.c)
    val valueOuter = value

    new SeriesZone {
      override val className = classNameOuter
      override val color = colorOuter
      override val dashStyle = dashStyleOuter
      override val fillColor = fillColorOuter
      override val value = valueOuter
    }
  }
}
