/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.TitleAlign

import scala.scalajs.js

trait AxisTitle extends js.Object {

  /**
    * Alignment of the title relative to the axis values. Possible values are "low", "middle" or "high".
    *
    * @example X axis title aligned <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-align-low/" target="_blank">"low"</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-align-center/" target="_blank">"middle" by default</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-align-high/" target="_blank">"high"</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/title-offset/" target="_blank">place the Y axis title on top of the axis</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Deprecated. Set the <code>text</code> to <code>null</code> to disable the title.
    */
  @deprecated("Deprecated in native HighCharts library. Set the `text` to `null` to disable the title.", "0.5.0")
  val enabled: js.UndefOr[String] = js.undefined

  /**
    * The pixel distance between the axis labels or line and the title. Defaults to 0 for horizontal axes, 10 for vertical
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-margin/" target="_blank">Y axis title margin of 60</a>
    */
  val margin: js.UndefOr[Double] = js.undefined

  /**
    * The distance of the axis title from the axis line. By default, this distance is
    * computed from the offset width of the labels, the labels' distance from  the axis and the title's margin.
    * However when the offset option is set, it overrides all this.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/title-offset/" target="_blank">Place the axis title on top of the axis</a>
    */
  val offset: js.UndefOr[Double] = js.undefined

  /**
    * The rotation of the text in degrees. 0 is horizontal, 270 is vertical reading from bottom to top.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/title-offset/" target="_blank">Horizontal</a> Y axis title
    */
  val rotation: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for the title. When titles are rotated they are rendered using vector graphic techniques and not all styles are applicable.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-style/" target="_blank">Red</a> Y axis title
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The actual text of the axis title. It can contain basic HTML text markup like &lt;b&gt;, &lt;i&gt; and spans with style.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/title-text/" target="_blank">Custom HTML</a> title for X axis
    */
  val text: js.UndefOr[String] = js.undefined

  /**
    * Horizontal pixel offset of the title position.
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * Vertical pixel offset of the title position.
    */
  val y: js.UndefOr[Double] = js.undefined
}

object AxisTitle {
  import scala.scalajs.js.JSConverters._

  /**
    * @param align Alignment of the title relative to the axis values. Possible values.  are "low", "middle" or "high".
    * @param margin The pixel distance between the axis labels or line and the title. Defaults to 0 for horizontal axes, 10 for vertical
    * @param offset The distance of the axis title from the axis line. By default, this distance is  computed from the offset width of the labels, the labels' distance from  the axis and the title's margin. However when the offset option is set, it overrides all this.
    * @param rotation The rotation of the text in degrees. 0 is horizontal, 270 is vertical reading from bottom to top.
    * @param style CSS styles for the title. When titles are rotated they are rendered using vector graphic techniques and not all styles are applicable.
    * @param text The actual text of the axis title. It can contain basic HTML text markup like &lt;b&gt;, &lt;i&gt; and spans with style.
    * @param x Horizontal pixel offset of the title position.
    * @param y Vertical pixel offset of the title position.
    */
  def apply(align: js.UndefOr[TitleAlign] = js.undefined,
            margin: js.UndefOr[Double] = js.undefined,
            offset: js.UndefOr[Double] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            text: js.UndefOr[String] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): AxisTitle = {
    val alignOuter = align.map(_.name)
    val marginOuter = margin
    val offsetOuter = offset
    val rotationOuter = rotation
    val styleOuter = style.map(stringToStyleObject)
    val textOuter = text
    val xOuter = x
    val yOuter = y

    new AxisTitle {
      override val align = alignOuter
      override val margin = marginOuter
      override val offset = offsetOuter
      override val rotation = rotationOuter
      override val style = styleOuter
      override val text = textOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
