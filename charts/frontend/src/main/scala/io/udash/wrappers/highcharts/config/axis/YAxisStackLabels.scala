/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Align, VerticalAlign}

import scala.scalajs.js
import scala.scalajs.js.ThisFunction


trait YAxisStackLabels extends js.Object {

  /**
    * Defines the horizontal alignment of the stack total label.  Can be one of <code>"left"</code>, <code>"center"</code>
    * or <code>"right"</code>. The default value is calculated at runtime and depends on orientation and whether
    * the stack is positive or negative.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-align-left/" target="_blank">aligned to the left</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-align-center/" target="_blank">aligned in center</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-align-right/" target="_blank">aligned to the right</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Enable or disable the stack total labels.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-enabled/" target="_blank">Enabled stack total labels</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * A <a href="http://docs.highcharts.com/#formatting">format string</a> for the data label. Available variables are the same as for <code>formatter</code>.
    */
  val format: js.UndefOr[String] = js.undefined

  /**
    * Callback JavaScript function to format the label. The value is  given by <code>this.total</code>. Defaults to: 
    * <pre>function() {
    * 	return this.total;
    * }</pre>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-formatter/" target="_blank">Added units to stack total value</a>
    */
  val formatter: js.UndefOr[js.ThisFunction0[YAxisStackLabels.FormatterData, Any]] = js.undefined

  /**
    * Rotation of the labels in degrees.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-rotation/" target="_blank">Labels rotated 45°</a>
    */
  val rotation: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for the label.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-style/" target="_blank">Red stack total labels</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The text alignment for the label. While <code>align</code> determines where the texts anchor point is placed with
    * regards to the stack, <code>textAlign</code> determines how the text is aligned against its anchor point.
    * Possible values are <code>"left"</code>, <code>"center"</code> and <code>"right"</code>. The default value is
    * calculated at runtime and depends on orientation and whether the stack is positive or negative.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-textalign-left/" target="_blank">Label in center position but text-aligned left</a>
    */
  val textAlign: js.UndefOr[String] = js.undefined

  /**
    * Whether to <a href="http://docs.highcharts.com/#formatting">use HTML</a> to render the labels.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * Defines the vertical alignment of the stack total label. Can be one of <code>"top"</code>, <code>"middle"</code>
    * or <code>"bottom"</code>. The default value is calculated at runtime and depends on orientation and whether  the stack is positive or negative.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-verticalalign-top/" target="_blank">"Vertically aligned top"</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-verticalalign-middle/" target="_blank">"Vertically aligned middle"</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-verticalalign-bottom/" target="_blank">"Vertically aligned bottom"</a>
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * The x position offset of the label relative to the left of the stacked bar. The default value is calculated
    * at runtime and depends on orientation and whether the stack is positive or negative.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-x/" target="_blank">Stack total labels with x offset</a>
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The y position offset of the label relative to the tick position on the axis. The default value is calculated
    * at runtime and depends on orientation and whether  the stack is positive or negative.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/stacklabels-y/" target="_blank">Stack total labels with y offset</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}

object YAxisStackLabels {
  import scala.scalajs.js.JSConverters._

  @js.native
  trait FormatterData extends js.Object {
    def total: Double
  }

  /**
    * @param align Defines the horizontal alignment of the stack total label.  Can be one of <code>"left"</code>, <code>"center"</code> or <code>"right"</code>. The default value is calculated at runtime and depends on orientation and whether  the stack is positive or negative.
    * @param enabled Enable or disable the stack total labels.
    * @param format A <a href="http://docs.highcharts.com/#formatting">format string</a> for the data label. Available variables are the same as for <code>formatter</code>.
    * @param formatter Callback JavaScript function to format the label. The value is  given by <code>this.total</code>. Defaults to: . <pre>function() {. 	return this.total;. }</pre>
    * @param rotation Rotation of the labels in degrees.
    * @param style CSS styles for the label.
    * @param textAlign The text alignment for the label. While <code>align</code> determines where the texts anchor point is placed with regards to the stack, <code>textAlign</code> determines how the text is aligned against its anchor point. Possible values are <code>"left"</code>, <code>"center"</code> and <code>"right"</code>. The default value is calculated at runtime and depends on orientation and whether the stack is positive or negative.
    * @param useHTML Whether to <a href="http://docs.highcharts.com/#formatting">use HTML</a> to render the labels.
    * @param verticalAlign Defines the vertical alignment of the stack total label. Can be one of <code>"top"</code>, <code>"middle"</code> or <code>"bottom"</code>. The default value is calculated at runtime and depends on orientation and whether  the stack is positive or negative.
    * @param x The x position offset of the label relative to the left of the stacked bar. The default value is calculated at runtime and depends on orientation and whether the stack is positive or negative.
    * @param y The y position offset of the label relative to the tick position on the axis. The default value is calculated at runtime and depends on orientation and whether  the stack is positive or negative.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            format: js.UndefOr[String] = js.undefined,
            formatter: js.UndefOr[(FormatterData) => Any] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            textAlign: js.UndefOr[Align] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): YAxisStackLabels = {
    val alignOuter = align.map(_.name)
    val enabledOuter = enabled
    val formatOuter = format
    val formatterOuter = formatter.map(ThisFunction.fromFunction1[FormatterData, Any])
    val rotationOuter = rotation
    val styleOuter = style.map(stringToStyleObject)
    val textAlignOuter = textAlign.map(_.name)
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val xOuter = x
    val yOuter = y

    new YAxisStackLabels {
      override val align = alignOuter
      override val enabled = enabledOuter
      override val format = formatOuter
      override val formatter = formatterOuter
      override val rotation = rotationOuter
      override val style = styleOuter
      override val textAlign = textAlignOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
