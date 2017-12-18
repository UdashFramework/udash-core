/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Align, VerticalAlign}

import scala.scalajs.js

trait AxisPlotLineOrBandLabel extends js.Object {

  /**
    * Horizontal alignment of the label. Can be one of "left", "center" or "right".
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-align-right/" target="_blank">Aligned to the right</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Rotation of the text label in degrees. Defaults to 0 for horizontal plot lines and 90 for vertical lines.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-verticalalign-middle/" target="_blank">Slanted text</a>
    */
  val rotation: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for the text label.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-style/" target="_blank">Blue and bold label</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The text itself. A subset of HTML is supported.
    */
  val text: js.UndefOr[String] = js.undefined

  /**
    * The text alignment for the label. While <code>align</code> determines where the texts anchor point is
    * placed within the plot band, <code>textAlign</code> determines how the text is aligned against its anchor point.
    * Possible values are "left", "center" and "right". Defaults to the same as the <code>align</code> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-textalign/" target="_blank">Text label in bottom position</a>
    */
  val textAlign: js.UndefOr[String] = js.undefined

  /**
    * <p>Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * Vertical alignment of the label relative to the plot band. Can be one of "top", "middle" or "bottom".
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-verticalalign-middle/" target="_blank">Vertically centered label</a>
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * Horizontal position relative the alignment. Default varies by orientation.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-align-right/" target="_blank">Aligned 10px from the right edge</a>
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * Vertical position of the text baseline relative to the alignment. Default varies by orientation.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-label-y/" target="_blank">Label below the plot line</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}

object AxisPlotLineOrBandLabel {
  import scala.scalajs.js.JSConverters._

  /**
    * @param align Horizontal alignment of the label. Can be one of "left", "center" or "right".
    * @param rotation Rotation of the text label in degrees. Defaults to 0 for horizontal plot lines and 90 for vertical lines.
    * @param style CSS styles for the text label.
    * @param text The text itself. A subset of HTML is supported.
    * @param textAlign The text alignment for the label. While <code>align</code> determines where the texts anchor point is placed within the plot band, <code>textAlign</code> determines how the text is aligned against its anchor point. Possible values are "left", "center" and "right". Defaults to the same as the <code>align</code> option.
    * @param useHTML <p>Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    * @param verticalAlign Vertical alignment of the label relative to the plot band. Can be one of "top", "middle" or "bottom".
    * @param x Horizontal position relative the alignment. Default varies by orientation.
    * @param y Vertical position of the text baseline relative to the alignment. Default varies by orientation.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            text: js.UndefOr[String] = js.undefined,
            textAlign: js.UndefOr[Align] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): AxisPlotLineOrBandLabel = {
    val alignOuter = align.map(_.name)
    val rotationOuter = rotation
    val styleOuter = style.map(stringToStyleObject)
    val textOuter = text
    val textAlignOuter = textAlign.map(_.name)
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val xOuter = x
    val yOuter = y

    new AxisPlotLineOrBandLabel {
      override val align = alignOuter
      override val rotation = rotationOuter
      override val style = styleOuter
      override val text = textOuter
      override val textAlign = textAlignOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
