/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package navigation

import io.udash.wrappers.highcharts.config.utils.{Align, Color, VerticalAlign}

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.annotation.ScalaJSDefined
class NavigationButtonOptions extends js.Object {

  /**
    * Alignment for the buttons.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-align/" target="_blank">Center aligned</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Whether to enable buttons.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-enabled/" target="_blank">Exporting module loaded but buttons disabled</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * Pixel height of the buttons.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val height: js.UndefOr[Double] = js.undefined

  /**
    * Fill color for the symbol within the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-symbolfill/" target="_blank">Blue symbol stroke for one of the buttons</a>
    */
  val symbolFill: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The pixel size of the symbol on the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val symbolSize: js.UndefOr[Double] = js.undefined

  /**
    * The color of the symbol's stroke or line.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-symbolstroke/" target="_blank">Blue symbol stroke</a>
    */
  val symbolStroke: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The pixel stroke width of the symbol on the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val symbolStrokeWidth: js.UndefOr[Double] = js.undefined

  /**
    * The x position of the center of the symbol inside the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val symbolX: js.UndefOr[Double] = js.undefined

  /**
    * The y position of the center of the symbol inside the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val symbolY: js.UndefOr[Double] = js.undefined

  /**
    * A text string to add to the individual button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/buttons-text/" target="_blank">Full text button</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/buttons-text-symbol/" target="_blank">combined symbol and text</a>
    */
  val text: js.UndefOr[String] = js.undefined

  /**
    * A configuration object for the button theme. The object accepts SVG properties like <code>stroke-width</code>, <code>stroke</code> and <code>fill</code>. Tri-state button styles are supported by the <code>states.hover</code> and <code>states.select</code> objects.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-theme/" target="_blank">Theming the buttons</a>
    */
  val theme: js.UndefOr[js.Object] = js.undefined

  /**
    * The vertical alignment of the buttons. Can be one of "top", "middle" or "bottom".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-verticalalign/" target="_blank">Buttons at lower right</a>
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * The pixel width of the button.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-height/" target="_blank">Bigger buttons</a>
    */
  val width: js.UndefOr[Double] = js.undefined

  /**
    * The vertical offset of the button's position relative to its <code>verticalAlign</code>.			 .
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/navigation/buttonoptions-verticalalign/" target="_blank">Buttons at lower right</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}

object NavigationButtonOptions {
  import scala.scalajs.js.JSConverters._

  /**
    * @param align             Alignment for the buttons.
    * @param enabled           Whether to enable buttons.
    * @param height            Pixel height of the buttons.
    * @param symbolFill        Fill color for the symbol within the button.
    * @param symbolSize        The pixel size of the symbol on the button.
    * @param symbolStroke      The color of the symbol's stroke or line.
    * @param symbolStrokeWidth The pixel stroke width of the symbol on the button.
    * @param symbolX           The x position of the center of the symbol inside the button.
    * @param symbolY           The y position of the center of the symbol inside the button.
    * @param text              A text string to add to the individual button.
    * @param theme             A configuration object for the button theme. The object accepts SVG properties like <code>stroke-width</code>, <code>stroke</code> and <code>fill</code>. Tri-state button styles are supported by the <code>states.hover</code> and <code>states.select</code> objects.
    * @param verticalAlign     The vertical alignment of the buttons. Can be one of "top", "middle" or "bottom".
    * @param width             The pixel width of the button.
    * @param y                 The vertical offset of the button's position relative to its <code>verticalAlign</code>.			 .
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            height: js.UndefOr[Double] = js.undefined,
            symbolFill: js.UndefOr[Color] = js.undefined,
            symbolSize: js.UndefOr[Double] = js.undefined,
            symbolStroke: js.UndefOr[Color] = js.undefined,
            symbolStrokeWidth: js.UndefOr[Double] = js.undefined,
            symbolX: js.UndefOr[Double] = js.undefined,
            symbolY: js.UndefOr[Double] = js.undefined,
            text: js.UndefOr[String] = js.undefined,
            theme: js.UndefOr[js.Object] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            width: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): NavigationButtonOptions = {
    val alignOuter = align.map(_.name)
    val enabledOuter = enabled
    val heightOuter = height
    val symbolFillOuter = symbolFill.map(_.c)
    val symbolSizeOuter = symbolSize
    val symbolStrokeOuter = symbolStroke.map(_.c)
    val symbolStrokeWidthOuter = symbolStrokeWidth
    val symbolXOuter = symbolX
    val symbolYOuter = symbolY
    val textOuter = text
    val themeOuter = theme
    val verticalAlignOuter = verticalAlign.map(_.name)
    val widthOuter = width
    val yOuter = y

    new NavigationButtonOptions {
      override val align = alignOuter
      override val enabled = enabledOuter
      override val height = heightOuter
      override val symbolFill = symbolFillOuter
      override val symbolSize = symbolSizeOuter
      override val symbolStroke = symbolStrokeOuter
      override val symbolStrokeWidth = symbolStrokeWidthOuter
      override val symbolX = symbolXOuter
      override val symbolY = symbolYOuter
      override val text = textOuter
      override val theme = themeOuter
      override val verticalAlign = verticalAlignOuter
      override val width = widthOuter
      override val y = yOuter
    }
  }
}
