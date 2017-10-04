/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Color, DashStyle}

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
trait AxisCrosshair extends js.Object {

  /**
    * A class name for the crosshair, especially as a hook for styling.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * The color of the crosshair. Defaults to <code>#C0C0C0</code> for numeric and datetime axes,
    * and <code>rgba(155,200,255,0.2)</code> for category axes, where the crosshair by default highlights the whole category.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/crosshair-customized/" target="_blank">Customized crosshairs</a>.
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The dash style for the crosshair. See DashStyle for possible values.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/crosshair-dotted/" target="_blank">Dotted crosshair</a>
    */
  val dashStyle: js.UndefOr[String] = js.undefined

  /**
    * Whether the crosshair should snap to the point or follow the pointer independent of points.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/crosshair-snap-false/" target="_blank">True by default</a>
    */
  val snap: js.UndefOr[Boolean] = js.undefined

  /**
    * The pixel width of the crosshair. Defaults to 1 for numeric or datetime axes, and for one category width for category axes.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/crosshair-customized/" target="_blank">Customized crosshairs</a>.
    */
  val width: js.UndefOr[Double] = js.undefined

  /**
    * The Z index of the crosshair. Higher Z indices allow drawing the crosshair on top of the series or behind the grid lines.
    */
  val zIndex: js.UndefOr[Int] = js.undefined
}

object AxisCrosshair {
  import scala.scalajs.js.JSConverters._

  /**
    * @param className A class name for the crosshair, especially as a hook for styling.
    * @param color The color of the crosshair. Defaults to <code>#C0C0C0</code> for numeric and datetime axes, and <code>rgba(155,200,255,0.2)</code> for category axes, where the crosshair by default highlights the whole category.
    * @param dashStyle The dash style for the crosshair. See <a href="#plotOptions.series.dashStyle">series.dashStyle</a> for possible values.
    * @param snap Whether the crosshair should snap to the point or follow the pointer independent of points.
    * @param width The pixel width of the crosshair. Defaults to 1 for numeric or datetime axes, and for one category width for category axes.
    * @param zIndex The Z index of the crosshair. Higher Z indices allow drawing the crosshair on top of the series or behind the grid lines.
    */
  def apply(className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            dashStyle: js.UndefOr[DashStyle] = js.undefined,
            snap: js.UndefOr[Boolean] = js.undefined,
            width: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): AxisCrosshair = {
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val dashStyleOuter = dashStyle.map(_.name)
    val snapOuter = snap
    val widthOuter = width
    val zIndexOuter = zIndex

    new AxisCrosshair {
      override val className = classNameOuter
      override val color = colorOuter
      override val dashStyle = dashStyleOuter
      override val snap = snapOuter
      override val width = widthOuter
      override val zIndex = zIndexOuter
    }
  }
}
