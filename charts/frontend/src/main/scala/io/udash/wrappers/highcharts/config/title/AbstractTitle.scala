package io.udash.wrappers.highcharts
package config
package title

import scala.scalajs.js

@js.annotation.ScalaJSDefined
abstract class AbstractTitle extends js.Object {

  /**
    * The horizontal alignment of the title. Can be one of "left", "center" and "right".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/align/" target="_blank">Aligned to the plot area (x = 70px= margin left - spacing left)</a>
    **/
  val align: js.UndefOr[String] = js.undefined

  /**
    * When the title is floating, the plot area will not move to make space for it.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/zoomtype-none/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/floating/" target="_blank">true - title on top of the plot area</a>.
    */
  val floating: js.UndefOr[Boolean] = js.undefined

  /**
    * CSS styles for the title. Use this for font styling, but use <code>align</code>, <code>x</code> and <code>y</code> for text alignment.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/style/" target="_blank">Custom color and weight</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The title of the chart. To disable the title, set the <code>text</code> to <code>null</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/text/" target="_blank">Custom title</a>
    */
  val text: js.UndefOr[String] = js.undefined

  /**
    * Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the text.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * The vertical alignment of the title. Can be one of <code>"top"</code>, <code>"middle"</code> and <code>"bottom"</code>. When a value is given, the title behaves as if <a href="#title.floating">floating</a> were <code>true</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/verticalalign/" target="_blank">Chart title in bottom right corner</a>
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * Adjustment made to the title width, normally to reserve space for the exporting burger menu.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/widthadjust/" target="_blank">Wider menu, greater padding</a>
    */
  val widthAdjust: js.UndefOr[Double] = js.undefined

  /**
    * The x position of the title relative to the alignment within chart.spacingLeft and chart.spacingRight.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/align/" target="_blank">Aligned to the plot area (x = 70px
    *          = margin left - spacing left)</a>
    **/
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The y position of the title relative to the alignment within <a href="#chart.spacingTop">chart.spacingTop</a> and <a href="#chart.spacingBottom">chart.spacingBottom</a>. By default it depends on the font size.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/y/" target="_blank">Title inside the plot area</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}
