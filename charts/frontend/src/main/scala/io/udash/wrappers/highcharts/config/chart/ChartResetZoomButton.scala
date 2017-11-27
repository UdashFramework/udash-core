/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package chart

import io.udash.wrappers.highcharts.config.utils.Position

import scala.scalajs.js

@js.annotation.ScalaJSDefined
trait ChartResetZoomButton extends js.Object {
  /**
    * The position of the button. This is an object that can hold the properties <code>align</code>,
    * <code>verticalAlign</code>, <code>x</code> and <code>y</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/resetzoombutton-position/" target="_blank">Above the plot area</a>
    */
  val position: js.UndefOr[Position] = js.undefined

  /**
    * What frame the button should be placed related to. Can be either "plot" or "chart".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/resetzoombutton-relativeto/" target="_blank">Relative to the chart</a>
    */
  val relativeTo: js.UndefOr[String] = js.undefined

  /**
    * A collection of attributes for the button. The object takes SVG attributes like
    * <code>fill</code>, <code>stroke</code>, <code>stroke-width</code> or <code>r</code>,
    * the border radius. The theme also supports <code>style</code>, a collection of CSS properties for the text.
    * Equivalent attributes for the hover state are given in <code>theme.states.hover</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/resetzoombutton-theme/" target="_blank">Theming the button</a>
    */
  val theme: js.UndefOr[js.Object] = js.undefined
}

object ChartResetZoomButton {
  import scala.scalajs.js.JSConverters._

  /**
    * @param position   The position of the button. This is an object that can hold the properties <code>align</code>, <code>verticalAlign</code>, <code>x</code> and <code>y</code>.
    * @param relativeTo What frame the button should be placed related to. Can be either "plot" or "chart".
    * @param theme      A collection of attributes for the button. The object takes SVG attributes like  <code>fill</code>, <code>stroke</code>, <code>stroke-width</code> or <code>r</code>, the border radius. The theme also supports <code>style</code>, a collection of CSS properties for the text. Equivalent attributes for the hover state are given in <code>theme.states.hover</code>.
    */
  def apply(position: js.UndefOr[Position] = js.undefined,
            relativeTo: js.UndefOr[String] = js.undefined,
            theme: js.UndefOr[String] = js.undefined): ChartResetZoomButton = {
    val positionOuter = position
    val relativeToOuter = relativeTo
    val themeOuter = theme.map(stringToStyleObject)

    new ChartResetZoomButton {
      override val position = positionOuter
      override val relativeTo = relativeToOuter
      override val theme = themeOuter
    }
  }
}