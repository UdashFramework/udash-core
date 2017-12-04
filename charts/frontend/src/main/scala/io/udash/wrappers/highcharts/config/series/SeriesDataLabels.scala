/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.highcharts.config.utils.{Align, Color, Shadow, VerticalAlign}

import scala.scalajs.js
import scala.scalajs.js.{ThisFunction, `|`}

@js.annotation.ScalaJSDefined
trait BaseSeriesDataLabels extends js.Object {
  /**
    * The background color or gradient for the data label. Defaults to <code>undefined</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val backgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The border color for the data label. Defaults to <code>undefined</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The border radius in pixels for the data label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val borderRadius: js.UndefOr[Double] = js.undefined

  /**
    * The border width in pixels for the data label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * The text color for the data labels. Defaults to <code>null</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-color/" target="_blank">Red data labels</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Whether to hide data labels that are outside the plot area. By default, the data label is moved inside the plot
    * area according to the <a href="#plotOptions.series.dataLabels.overflow">overflow</a> option.
    */
  val crop: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to defer displaying the data labels until the initial series animation has finished.
    */
  val defer: js.UndefOr[Boolean] = js.undefined

  /**
    * Enable or disable the data labels.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-enabled/" target="_blank">Data labels enabled</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for the data label.
    * Available variables are the same as for <code>formatter</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-format/" target="_blank">Add a unit</a>
    */
  val format: js.UndefOr[String] = js.undefined

  /**
    * Callback JavaScript function to format the data label. Note that if a <code>format</code> is defined, the format
    * takes precedence and the formatter is ignored. Available data are:
    * <table>
    * <tbody><tr>
    * <td><code>this.percentage</code></td>
    * <td>Stacked series and pies only. The point's percentage of the total.</td>
    * </tr>
    * <tr>
    * <td><code>this.point</code></td>
    * <td>The point object. The point name, if defined, is available
    * through <code>this.point.name</code>.</td>
    * </tr>
    * <tr>
    * <td><code>this.series</code>:</td>
    * <td>The series object. The series name is available
    * through <code>this.series.name</code>.</td>
    * </tr>
    * <tr>
    * <td><code>this.total</code></td>
    * <td>Stacked series only. The total value at this point's x value.</td>
    * </tr>
    * <tr>
    * <td><code>this.x</code>:</td>
    * <td>The x value.</td>
    * </tr>
    * <tr>
    * <td><code>this.y</code>:</td>
    * <td>The y value.</td>
    * </tr>
    * </tbody></table>
    */
  val formatter: js.UndefOr[js.ThisFunction0[SeriesDataLabels.FormatterData, String]] = js.undefined

  /**
    * For points with an extent, like columns, whether to align the data label inside the box or to the actual value point.
    * Defaults to <code>false</code> in most cases, <code>true</code> in stacked columns.
    */
  val inside: js.UndefOr[Boolean] = js.undefined

  /**
    * How to handle data labels that flow outside the plot area. The default is <code>justify</code>, which aligns them inside the plot area. For columns and bars, this means it will be moved inside the bar. To display data labels outside the plot area, set <code>crop</code> to <code>false</code> and <code>overflow</code> to <code>"none"</code>.
    */
  val overflow: js.UndefOr[String] = js.undefined

  /**
    * When either the <code>borderWidth</code> or the <code>backgroundColor</code> is set, this		is the padding within the box.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val padding: js.UndefOr[Double] = js.undefined

  /**
    * Whether to reserve space for the labels. This can be turned off when for example the labels are rendered inside the plot area instead of outside.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/labels-reservespace/" target="_blank">No reserved space, labels inside plot</a>.
    */
  val reserveSpace: js.UndefOr[Boolean] = js.undefined

  /**
    * Text rotation in degrees. Note that due to a more complex structure, backgrounds, borders and padding will be lost on a rotated data label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-rotation/" target="_blank">Vertical labels</a>
    */
  val rotation: js.UndefOr[Double] = js.undefined

  /**
    * The shadow of the box. Works best with <code>borderWidth</code> or <code>backgroundColor</code>. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-box/" target="_blank">Data labels box options</a>
    */
  val shadow: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * The name of a symbol to use for the border around the label. Symbols are predefined functions on the Renderer object.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-shape/" target="_blank">A callout for annotations</a>
    */
  val shape: js.UndefOr[String] = js.undefined

  /**
    * Styles for the label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-style/" target="_blank">Bold labels</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * The vertical alignment of a data label. Can be one of <code>top</code>, <code>middle</code> or <code>bottom</code>. The default value depends on the data, for instance in a column chart, the label is above positive values and below negative values.
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * The Z index of the data labels. The default Z index puts it above the series. Use a Z index of 2 to display it behind the series.
    */
  val zIndex: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait SeriesDataLabels extends BaseSeriesDataLabels {
  /**
    * The alignment of the data label compared to the point.  If <code>right</code>, the right side of the label should be touching the point.
    * For points with an extent, like columns, the alignments also dictates how to align it inside the box, as given with
    * the <a href="#plotOptions.column.dataLabels.inside">inside</a> option. Can be one of "left", "center" or "right".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-align-left/" target="_blank">Left aligned</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Whether to allow data labels to overlap. To make the labels less sensitive for overlapping,
    * the <a href="#plotOptions.series.dataLabels.padding">dataLabels.padding</a> can be set to 0.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-allowoverlap-false/" target="_blank">Don't allow overlap</a>
    */
  val allowOverlap: js.UndefOr[Boolean] = js.undefined

  /**
    * The x position offset of the label relative to the point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-rotation/" target="_blank">Vertical and positioned</a>
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The y position offset of the label relative to the point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-rotation/" target="_blank">Vertical and positioned</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait SeriesRangeDataLabels extends BaseSeriesDataLabels {
  /**
    * The alignment of the data label compared to the point.  If <code>right</code>, the right side of the label
    * should be touching the point. For points with an extent, like columns, the alignments also dictates how to align it
    * inside the box, as given with the <a href="#plotOptions.column.dataLabels.inside">inside</a> option. Can be one of "left", "center" or "right".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-align-left/" target="_blank">Left aligned</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * Whether to allow data labels to overlap. To make the labels less sensitive for overlapping,
    * the <a href="#plotOptions.series.dataLabels.padding">dataLabels.padding</a> can be set to 0.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-allowoverlap-false/" target="_blank">Don't allow overlap</a>
    */
  val allowOverlap: js.UndefOr[Boolean] = js.undefined

  /**
    * X offset of the higher data labels relative to the point value.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-datalabels/" target="_blank">Data labels on range series</a>
    */
  val xHigh: js.UndefOr[Double] = js.undefined

  /**
    * X offset of the lower data labels relative to the point value.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-datalabels/" target="_blank">Data labels on range series</a>
    */
  val xLow: js.UndefOr[Double] = js.undefined

  /**
    * Y offset of the higher data labels relative to the point value.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-datalabels/" target="_blank">Data labels on range series</a>
    */
  val yHigh: js.UndefOr[Double] = js.undefined

  /**
    * Y offset of the lower data labels relative to the point value.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-datalabels/" target="_blank">Data labels on range series</a>
    */
  val yLow: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait SeriesConnectorDataLabels extends BaseSeriesDataLabels {
  /**
    * The color of the line connecting the data label to the pie slice. The default color is the same as the point's color.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-connectorcolor/" target="_blank">Blue connectors</a>
    */
  val connectorColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The distance from the data label to the connector.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-connectorpadding/" target="_blank">No padding</a>
    */
  val connectorPadding: js.UndefOr[Double] = js.undefined

  /**
    * The width of the line connecting the data label to the pie slice.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-connectorwidth-disabled/" target="_blank">Disable the connector</a>
    */
  val connectorWidth: js.UndefOr[Double] = js.undefined

  /**
    * The distance of the data label from the pie's edge. Negative numbers put the data label on top of the pie slices.
    * Connectors are only shown for data labels outside the pie.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-distance/" target="_blank">Data labels on top of the pie</a>
    */
  val distance: js.UndefOr[Double] = js.undefined

  /**
    * Whether to render the connector as a soft arc or a line with sharp break.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-softconnector-true/" target="_blank">Soft</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-datalabels-softconnector-false/" target="_blank">non soft</a> connectors.
    */
  val softConnector: js.UndefOr[Boolean] = js.undefined

  /**
    * The x position offset of the label relative to the point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-rotation/" target="_blank">Vertical and positioned</a>
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The y position offset of the label relative to the point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-datalabels-rotation/" target="_blank">Vertical and positioned</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}


object SeriesDataLabels {
  import scala.scalajs.js.JSConverters._

  @js.native
  trait FormatterData extends js.Object {
    def percentage: Double = js.native
    def point: api.Point = js.native
    def series: api.Series = js.native
    def total: Double = js.native
    def x: Double = js.native
    def y: Double = js.native
  }

  /**
    * @param align           The alignment of the data label compared to the point.  If <code>right</code>, the right side of the label should be touching the point. For points with an extent, like columns, the alignments also dictates how to align it inside the box, as given with the <a href="#plotOptions.column.dataLabels.inside">inside</a> option. Can be one of "left", "center" or "right".
    * @param allowOverlap    Whether to allow data labels to overlap. To make the labels less sensitive for overlapping, the <a href="#plotOptions.series.dataLabels.padding">dataLabels.padding</a> can be set to 0.
    * @param backgroundColor The background color or gradient for the data label. Defaults to <code>undefined</code>.
    * @param borderColor     The border color for the data label. Defaults to <code>undefined</code>.
    * @param borderRadius    The border radius in pixels for the data label.
    * @param borderWidth     The border width in pixels for the data label.
    * @param color           The text color for the data labels. Defaults to <code>null</code>.
    * @param crop            Whether to hide data labels that are outside the plot area. By default, the data label is moved inside the plot area according to the <a href="#plotOptions.series.dataLabels.overflow">overflow</a> option.
    * @param defer           Whether to defer displaying the data labels until the initial series animation has finished.
    * @param enabled         Enable or disable the data labels.
    * @param format          A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for the data label. Available variables are the same as for <code>formatter</code>.
    * @param formatter       Callback JavaScript function to format the data label. Note that if a <code>format</code> is defined, the format takes precedence and the formatter is ignored. Available data are:. <table>. <tbody><tr>.   <td><code>this.percentage</code></td>.   <td>Stacked series and pies only. The point's percentage of the total.</td>. </tr>. <tr>.   <td><code>this.point</code></td>.   <td>The point object. The point name, if defined, is available . through <code>this.point.name</code>.</td>. </tr>. <tr>.   <td><code>this.series</code>:</td>.   <td>The series object. The series name is available . through <code>this.series.name</code>.</td>. </tr>. <tr>.   <td><code>this.total</code></td>.   <td>Stacked series only. The total value at this point's x value.</td>. </tr>				. <tr>.   <td><code>this.x</code>:</td>.   <td>The x value.</td>. </tr>. <tr>.   <td><code>this.y</code>:</td>.   <td>The y value.</td>. </tr>. </tbody></table>
    * @param inside          For points with an extent, like columns, whether to align the data label inside the box or to the actual value point. Defaults to <code>false</code> in most cases, <code>true</code> in stacked columns.
    * @param overflow        How to handle data labels that flow outside the plot area. The default is <code>justify</code>, which aligns them inside the plot area. For columns and bars, this means it will be moved inside the bar. To display data labels outside the plot area, set <code>crop</code> to <code>false</code> and <code>overflow</code> to <code>"none"</code>.
    * @param padding         When either the <code>borderWidth</code> or the <code>backgroundColor</code> is set, this		is the padding within the box.
    * @param reserveSpace    Whether to reserve space for the labels. This can be turned off when for example the labels are rendered inside the plot area instead of outside.
    * @param rotation        Text rotation in degrees. Note that due to a more complex structure, backgrounds, borders and padding will be lost on a rotated data label.
    * @param shadow          The shadow of the box. Works best with <code>borderWidth</code> or <code>backgroundColor</code>. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param shape           The name of a symbol to use for the border around the label. Symbols are predefined functions on the Renderer object.
    * @param style           Styles for the label.
    * @param useHTML         Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    * @param verticalAlign   The vertical alignment of a data label. Can be one of <code>top</code>, <code>middle</code> or <code>bottom</code>. The default value depends on the data, for instance in a column chart, the label is above positive values and below negative values.
    * @param x               The x position offset of the label relative to the point.
    * @param y               The y position offset of the label relative to the point.
    * @param zIndex          The Z index of the data labels. The default Z index puts it above the series. Use a Z index of 2 to display it behind the series.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            allowOverlap: js.UndefOr[Boolean] = js.undefined,
            backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            crop: js.UndefOr[Boolean] = js.undefined,
            defer: js.UndefOr[Boolean] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            format: js.UndefOr[String] = js.undefined,
            formatter: js.UndefOr[(SeriesDataLabels.FormatterData) => String] = js.undefined,
            inside: js.UndefOr[Boolean] = js.undefined,
            overflow: js.UndefOr[String] = js.undefined,
            padding: js.UndefOr[Double] = js.undefined,
            reserveSpace: js.UndefOr[Boolean] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            shape: js.UndefOr[String] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Double] = js.undefined): SeriesDataLabels = {
    val alignOuter = align.map(_.name)
    val allowOverlapOuter = allowOverlap
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val colorOuter = color.map(_.c)
    val cropOuter = crop
    val deferOuter = defer
    val enabledOuter = enabled
    val formatOuter = format
    val formatterOuter = formatter.map(ThisFunction.fromFunction1[SeriesDataLabels.FormatterData, String])
    val insideOuter = inside
    val overflowOuter = overflow
    val paddingOuter = padding
    val reserveSpaceOuter = reserveSpace
    val rotationOuter = rotation
    val shadowOuter = shadow.map(_.value)
    val shapeOuter = shape
    val styleOuter = style.map(stringToStyleObject)
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val xOuter = x
    val yOuter = y
    val zIndexOuter = zIndex

    new SeriesDataLabels {
      override val align = alignOuter
      override val allowOverlap = allowOverlapOuter
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val color = colorOuter
      override val crop = cropOuter
      override val defer = deferOuter
      override val enabled = enabledOuter
      override val format = formatOuter
      override val formatter = formatterOuter
      override val inside = insideOuter
      override val overflow = overflowOuter
      override val padding = paddingOuter
      override val reserveSpace = reserveSpaceOuter
      override val rotation = rotationOuter
      override val shadow = shadowOuter
      override val shape = shapeOuter
      override val style = styleOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val x = xOuter
      override val y = yOuter
      override val zIndex = zIndexOuter
    }
  }
}

object SeriesRangeDataLabels {
  import scala.scalajs.js.JSConverters._

  /**
    * @param align           The alignment of the data label compared to the point.  If <code>right</code>, the right side of the label should be touching the point. For points with an extent, like columns, the alignments also dictates how to align it inside the box, as given with the <a href="#plotOptions.column.dataLabels.inside">inside</a> option. Can be one of "left", "center" or "right".
    * @param allowOverlap    Whether to allow data labels to overlap. To make the labels less sensitive for overlapping, the <a href="#plotOptions.series.dataLabels.padding">dataLabels.padding</a> can be set to 0.
    * @param backgroundColor The background color or gradient for the data label. Defaults to <code>undefined</code>.
    * @param borderColor     The border color for the data label. Defaults to <code>undefined</code>.
    * @param borderRadius    The border radius in pixels for the data label.
    * @param borderWidth     The border width in pixels for the data label.
    * @param color           The text color for the data labels. Defaults to <code>null</code>.
    * @param crop            Whether to hide data labels that are outside the plot area. By default, the data label is moved inside the plot area according to the <a href="#plotOptions.series.dataLabels.overflow">overflow</a> option.
    * @param defer           Whether to defer displaying the data labels until the initial series animation has finished.
    * @param enabled         Enable or disable the data labels.
    * @param format          A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for the data label. Available variables are the same as for <code>formatter</code>.
    * @param formatter       Callback JavaScript function to format the data label. Note that if a <code>format</code> is defined, the format takes precedence and the formatter is ignored. Available data are:. <table>. <tbody><tr>.   <td><code>this.percentage</code></td>.   <td>Stacked series and pies only. The point's percentage of the total.</td>. </tr>. <tr>.   <td><code>this.point</code></td>.   <td>The point object. The point name, if defined, is available . through <code>this.point.name</code>.</td>. </tr>. <tr>.   <td><code>this.series</code>:</td>.   <td>The series object. The series name is available . through <code>this.series.name</code>.</td>. </tr>. <tr>.   <td><code>this.total</code></td>.   <td>Stacked series only. The total value at this point's x value.</td>. </tr>				. <tr>.   <td><code>this.x</code>:</td>.   <td>The x value.</td>. </tr>. <tr>.   <td><code>this.y</code>:</td>.   <td>The y value.</td>. </tr>. </tbody></table>
    * @param inside          For points with an extent, like columns, whether to align the data label inside the box or to the actual value point. Defaults to <code>false</code> in most cases, <code>true</code> in stacked columns.
    * @param overflow        How to handle data labels that flow outside the plot area. The default is <code>justify</code>, which aligns them inside the plot area. For columns and bars, this means it will be moved inside the bar. To display data labels outside the plot area, set <code>crop</code> to <code>false</code> and <code>overflow</code> to <code>"none"</code>.
    * @param padding         When either the <code>borderWidth</code> or the <code>backgroundColor</code> is set, this		is the padding within the box.
    * @param reserveSpace    Whether to reserve space for the labels. This can be turned off when for example the labels are rendered inside the plot area instead of outside.
    * @param rotation        Text rotation in degrees. Note that due to a more complex structure, backgrounds, borders and padding will be lost on a rotated data label.
    * @param shadow          The shadow of the box. Works best with <code>borderWidth</code> or <code>backgroundColor</code>. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param shape           The name of a symbol to use for the border around the label. Symbols are predefined functions on the Renderer object.
    * @param style           Styles for the label.
    * @param useHTML         Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    * @param verticalAlign   The vertical alignment of a data label. Can be one of <code>top</code>, <code>middle</code> or <code>bottom</code>. The default value depends on the data, for instance in a column chart, the label is above positive values and below negative values.
    * @param xHigh           X offset of the higher data labels relative to the point value.
    * @param xLow            X offset of the lower data labels relative to the point value.
    * @param yHigh           Y offset of the higher data labels relative to the point value.
    * @param yLow            Y offset of the lower data labels relative to the point value.
    * @param zIndex          The Z index of the data labels. The default Z index puts it above the series. Use a Z index of 2 to display it behind the series.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            allowOverlap: js.UndefOr[Boolean] = js.undefined,
            backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            crop: js.UndefOr[Boolean] = js.undefined,
            defer: js.UndefOr[Boolean] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            format: js.UndefOr[String] = js.undefined,
            formatter: js.UndefOr[(SeriesDataLabels.FormatterData) => String] = js.undefined,
            inside: js.UndefOr[Boolean] = js.undefined,
            overflow: js.UndefOr[String] = js.undefined,
            padding: js.UndefOr[Double] = js.undefined,
            reserveSpace: js.UndefOr[Boolean] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            shape: js.UndefOr[String] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            xHigh: js.UndefOr[Double] = js.undefined,
            xLow: js.UndefOr[Double] = js.undefined,
            yHigh: js.UndefOr[Double] = js.undefined,
            yLow: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Double] = js.undefined): SeriesRangeDataLabels = {
    val alignOuter = align.map(_.name)
    val allowOverlapOuter = allowOverlap
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val colorOuter = color.map(_.c)
    val cropOuter = crop
    val deferOuter = defer
    val enabledOuter = enabled
    val formatOuter = format
    val formatterOuter = formatter.map(ThisFunction.fromFunction1[SeriesDataLabels.FormatterData, String])
    val insideOuter = inside
    val overflowOuter = overflow
    val paddingOuter = padding
    val reserveSpaceOuter = reserveSpace
    val rotationOuter = rotation
    val shadowOuter = shadow.map(_.value)
    val shapeOuter = shape
    val styleOuter = style.map(stringToStyleObject)
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val xHighOuter = xHigh
    val xLowOuter = xLow
    val yHighOuter = yHigh
    val yLowOuter = yLow
    val zIndexOuter = zIndex

    new SeriesRangeDataLabels {
      override val align = alignOuter
      override val allowOverlap = allowOverlapOuter
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val color = colorOuter
      override val crop = cropOuter
      override val defer = deferOuter
      override val enabled = enabledOuter
      override val format = formatOuter
      override val formatter = formatterOuter
      override val inside = insideOuter
      override val overflow = overflowOuter
      override val padding = paddingOuter
      override val reserveSpace = reserveSpaceOuter
      override val rotation = rotationOuter
      override val shadow = shadowOuter
      override val shape = shapeOuter
      override val style = styleOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val xHigh = xHighOuter
      override val xLow = xLowOuter
      override val yHigh = yHighOuter
      override val yLow = yLowOuter
      override val zIndex = zIndexOuter
    }
  }
}

object SeriesConnectorDataLabels {
  import scala.scalajs.js.JSConverters._

  /**
    * @param backgroundColor  The background color or gradient for the data label. Defaults to <code>undefined</code>.
    * @param borderColor      The border color for the data label. Defaults to <code>undefined</code>.
    * @param borderRadius     The border radius in pixels for the data label.
    * @param borderWidth      The border width in pixels for the data label.
    * @param color            The text color for the data labels. Defaults to <code>null</code>.
    * @param connectorColor   The color of the line connecting the data label to the pie slice. The default color is the same as the point's color.
    * @param connectorPadding The distance from the data label to the connector.
    * @param connectorWidth   The width of the line connecting the data label to the pie slice.
    * @param crop             Whether to hide data labels that are outside the plot area. By default, the data label is moved inside the plot area according to the <a href="#plotOptions.series.dataLabels.overflow">overflow</a> option.
    * @param defer            Whether to defer displaying the data labels until the initial series animation has finished.
    * @param distance         The distance of the data label from the pie's edge. Negative numbers put the data label on top of the pie slices. Connectors are only shown for data labels outside the pie.
    * @param enabled          Enable or disable the data labels.
    * @param format           A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for the data label. Available variables are the same as for <code>formatter</code>.
    * @param formatter        Callback JavaScript function to format the data label. Note that if a <code>format</code> is defined, the format takes precedence and the formatter is ignored. Available data are:. <table>. <tbody><tr>.   <td><code>this.percentage</code></td>.   <td>Stacked series and pies only. The point's percentage of the total.</td>. </tr>. <tr>.   <td><code>this.point</code></td>.   <td>The point object. The point name, if defined, is available . through <code>this.point.name</code>.</td>. </tr>. <tr>.   <td><code>this.series</code>:</td>.   <td>The series object. The series name is available . through <code>this.series.name</code>.</td>. </tr>. <tr>.   <td><code>this.total</code></td>.   <td>Stacked series only. The total value at this point's x value.</td>. </tr>				. <tr>.   <td><code>this.x</code>:</td>.   <td>The x value.</td>. </tr>. <tr>.   <td><code>this.y</code>:</td>.   <td>The y value.</td>. </tr>. </tbody></table>
    * @param inside           For points with an extent, like columns, whether to align the data label inside the box or to the actual value point. Defaults to <code>false</code> in most cases, <code>true</code> in stacked columns.
    * @param overflow         How to handle data labels that flow outside the plot area. The default is <code>justify</code>, which aligns them inside the plot area. For columns and bars, this means it will be moved inside the bar. To display data labels outside the plot area, set <code>crop</code> to <code>false</code> and <code>overflow</code> to <code>"none"</code>.
    * @param padding          When either the <code>borderWidth</code> or the <code>backgroundColor</code> is set, this		is the padding within the box.
    * @param reserveSpace     Whether to reserve space for the labels. This can be turned off when for example the labels are rendered inside the plot area instead of outside.
    * @param rotation         Text rotation in degrees. Note that due to a more complex structure, backgrounds, borders and padding will be lost on a rotated data label.
    * @param shadow           The shadow of the box. Works best with <code>borderWidth</code> or <code>backgroundColor</code>. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param shape            The name of a symbol to use for the border around the label. Symbols are predefined functions on the Renderer object.
    * @param softConnector    Whether to render the connector as a soft arc or a line with sharp break.
    * @param style            Styles for the label.
    * @param useHTML          Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the labels.
    * @param verticalAlign    The vertical alignment of a data label. Can be one of <code>top</code>, <code>middle</code> or <code>bottom</code>. The default value depends on the data, for instance in a column chart, the label is above positive values and below negative values.
    * @param x                The x position offset of the label relative to the point.
    * @param y                The y position offset of the label relative to the point.
    * @param zIndex           The Z index of the data labels. The default Z index puts it above the series. Use a Z index of 2 to display it behind the series.
    */
  def apply(backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            connectorColor: js.UndefOr[Color] = js.undefined,
            connectorPadding: js.UndefOr[Double] = js.undefined,
            connectorWidth: js.UndefOr[Double] = js.undefined,
            crop: js.UndefOr[Boolean] = js.undefined,
            defer: js.UndefOr[Boolean] = js.undefined,
            distance: js.UndefOr[Double] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            format: js.UndefOr[String] = js.undefined,
            formatter: js.UndefOr[(SeriesDataLabels.FormatterData) => String] = js.undefined,
            inside: js.UndefOr[Boolean] = js.undefined,
            overflow: js.UndefOr[String] = js.undefined,
            padding: js.UndefOr[Double] = js.undefined,
            reserveSpace: js.UndefOr[Boolean] = js.undefined,
            rotation: js.UndefOr[Double] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            shape: js.UndefOr[String] = js.undefined,
            softConnector: js.UndefOr[Boolean] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Double] = js.undefined): SeriesConnectorDataLabels = {
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val colorOuter = color.map(_.c)
    val connectorColorOuter = connectorColor.map(_.c)
    val connectorPaddingOuter = connectorPadding
    val connectorWidthOuter = connectorWidth
    val cropOuter = crop
    val deferOuter = defer
    val distanceOuter = distance
    val enabledOuter = enabled
    val formatOuter = format
    val formatterOuter = formatter.map(ThisFunction.fromFunction1[SeriesDataLabels.FormatterData, String])
    val insideOuter = inside
    val overflowOuter = overflow
    val paddingOuter = padding
    val reserveSpaceOuter = reserveSpace
    val rotationOuter = rotation
    val shadowOuter = shadow.map(_.value)
    val shapeOuter = shape
    val softConnectorOuter = softConnector
    val styleOuter = style.map(stringToStyleObject)
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val xOuter = x
    val yOuter = y
    val zIndexOuter = zIndex

    new SeriesConnectorDataLabels {
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val color = colorOuter
      override val connectorColor = connectorColorOuter
      override val connectorPadding = connectorPaddingOuter
      override val connectorWidth = connectorWidthOuter
      override val crop = cropOuter
      override val defer = deferOuter
      override val distance = distanceOuter
      override val enabled = enabledOuter
      override val format = formatOuter
      override val formatter = formatterOuter
      override val inside = insideOuter
      override val overflow = overflowOuter
      override val padding = paddingOuter
      override val reserveSpace = reserveSpaceOuter
      override val rotation = rotationOuter
      override val shadow = shadowOuter
      override val shape = shapeOuter
      override val softConnector = softConnectorOuter
      override val style = styleOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val x = xOuter
      override val y = yOuter
      override val zIndex = zIndexOuter
    }
  }
}