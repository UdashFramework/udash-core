/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package legend

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.`|`


trait LegendNavigation extends js.Object {

  /**
    * The color for the active up or down arrow in the legend page navigation.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation/" target="_blank">
    *          Legend page navigation demonstrated</a>
    */
  val activeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * How to animate the pages when navigating up or down. A value of <code>true</code> applies the default navigation given in  the chart.animation option. Additional options can be given as an object containing values for easing and duration.  .
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation/" target="_blank">
    *          Legend page navigation demonstrated</a>
    */
  val animation: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * The pixel size of the up and down arrows in the legend paging navigation.  .
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation/" target="_blank">
    *          Legend page navigation demonstrated</a>
    */
  val arrowSize: js.UndefOr[Double] = js.undefined

  /**
    * Whether to enable the legend navigation. In most cases, disabling the navigation results in an unwanted overflow.
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * The color of the inactive up or down arrow in the legend page navigation.  .
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation/" target="_blank">
    *          Legend page navigation demonstrated</a>
    */
  val inactiveColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Text styles for the legend page navigation.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation/" target="_blank">
    *          Legend page navigation demonstrated</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined
}

object LegendNavigation {

  /**
    * @param activeColor   The color for the active up or down arrow in the legend page navigation.
    * @param animation     How to animate the pages when navigating up or down. A value of <code>true</code> applies the default navigation given in  the chart.animation option. Additional options can be given as an object containing values for easing and duration.  .
    * @param arrowSize     The pixel size of the up and down arrows in the legend paging navigation.  .
    * @param enabled       Whether to enable the legend navigation. In most cases, disabling the navigation results in an unwanted overflow.
    * @param inactiveColor The color of the inactive up or down arrow in the legend page navigation.  .
    * @param style         Text styles for the legend page navigation.
    */
  def apply(activeColor: js.UndefOr[Color] = js.undefined,
            animation: js.UndefOr[Boolean | js.Object] = js.undefined,
            arrowSize: js.UndefOr[Double] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            inactiveColor: js.UndefOr[Color] = js.undefined,
            style: js.UndefOr[String] = js.undefined): LegendNavigation = {
    val activeColorOuter = activeColor.map(_.c)
    val animationOuter = animation
    val arrowSizeOuter = arrowSize
    val enabledOuter = enabled
    val inactiveColorOuter = inactiveColor.map(_.c)
    val styleOuter = style.map(stringToStyleObject)

    new LegendNavigation {
      override val activeColor = activeColorOuter
      override val animation = animationOuter
      override val arrowSize = arrowSizeOuter
      override val enabled = enabledOuter
      override val inactiveColor = inactiveColorOuter
      override val style = styleOuter
    }
  }
}
