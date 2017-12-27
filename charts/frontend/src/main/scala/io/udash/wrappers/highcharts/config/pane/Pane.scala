/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package pane

import scala.scalajs.js
import scala.scalajs.js.`|`

trait Pane extends js.Object {

  /**
    * An object, or array of objects, for backgrounds.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-speedometer/" target="_blank">Multiple backgrounds on a gauge</a>.
    */
  val background: js.UndefOr[js.Array[PaneBackground]] = js.undefined

  /**
    * The center of a polar chart or angular gauge, given as an array of [x, y] positions. Positions can be given as integers that transform to pixels, or as percentages of the plot area size.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-vu-meter/" target="_blank">Two gauges with different center</a>
    */
  val center: js.UndefOr[js.Array[Double | String]] = js.undefined

  /**
    * The end angle of the polar X axis or gauge value axis, given in degrees where 0 is north. Defaults to <a href="#pane.startAngle">startAngle</a> + 360.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-vu-meter/" target="_blank">VU-meter with custom start and end angle</a>.
    */
  val endAngle: js.UndefOr[Double] = js.undefined

  /**
    * The size of the pane, either as a number defining pixels, or a percentage defining a percentage of the plot are.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-vu-meter/" target="_blank">Smaller size</a>
    */
  val size: js.UndefOr[Double | String] = js.undefined

  /**
    * The start angle of the polar X axis or gauge axis, given in degrees where 0 is north. Defaults to 0.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-vu-meter/" target="_blank">VU-meter with custom start and end angle</a>.
    */
  val startAngle: js.UndefOr[Double] = js.undefined
}

object Pane {
  import scala.scalajs.js.JSConverters._

  /**
    * @param background An object, or array of objects, for backgrounds.
    * @param center     The center of a polar chart or angular gauge, given as an array of [x, y] positions. Positions can be given as integers that transform to pixels, or as percentages of the plot area size.
    * @param endAngle   The end angle of the polar X axis or gauge value axis, given in degrees where 0 is north. Defaults to <a href="#pane.startAngle">startAngle</a> + 360.
    * @param size       The size of the pane, either as a number defining pixels, or a percentage defining a percentage of the plot are.
    * @param startAngle The start angle of the polar X axis or gauge axis, given in degrees where 0 is north. Defaults to 0.
    */
  def apply(background: js.UndefOr[Seq[PaneBackground]] = js.undefined,
            center: js.UndefOr[Seq[Double | String]] = js.undefined,
            endAngle: js.UndefOr[Double] = js.undefined,
            size: js.UndefOr[Double | String] = js.undefined,
            startAngle: js.UndefOr[Double] = js.undefined): Pane = {
    val backgroundOuter = background.map(_.toJSArray)
    val centerOuter = center.map(_.toJSArray)
    val endAngleOuter = endAngle
    val sizeOuter = size
    val startAngleOuter = startAngle

    new Pane {
      override val background = backgroundOuter
      override val center = centerOuter
      override val endAngle = endAngleOuter
      override val size = sizeOuter
      override val startAngle = startAngleOuter
    }
  }
}
