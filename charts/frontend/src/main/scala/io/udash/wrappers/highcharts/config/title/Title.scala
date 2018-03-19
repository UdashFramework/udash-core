/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package title

import io.udash.wrappers.highcharts.config.utils.{Align, VerticalAlign}

import scala.scalajs.js


trait Title extends AbstractTitle {
  /**
    * The margin between the title and the plot area, or if a subtitle is present, the margin between the subtitle and the plot area.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/margin-50/" target="_blank">A chart title margin of 50</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/title/margin-subtitle/" target="_blank">the same margin applied with a subtitle</a>.
    */
  val margin: js.UndefOr[Double] = js.undefined
}

object Title {

  /**
    * @param align         The horizontal alignment of the title. Can be one of "left", "center" and "right".
    * @param floating      When the title is floating, the plot area will not move to make space for it.
    * @param margin        The margin between the title and the plot area, or if a subtitle is present, the margin between the subtitle and the plot area.
    * @param style         CSS styles for the title. Use this for font styling, but use <code>align</code>, <code>x</code> and <code>y</code> for text alignment.
    * @param text          The title of the chart. To disable the title, set the <code>text</code> to <code>null</code>.
    * @param useHTML       Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the text.
    * @param verticalAlign The vertical alignment of the title. Can be one of <code>"top"</code>, <code>"middle"</code> and <code>"bottom"</code>. When a value is given, the title behaves as if <a href="#title.floating">floating</a> were <code>true</code>.
    * @param widthAdjust   Adjustment made to the title width, normally to reserve space for the exporting burger menu.
    * @param x             The x position of the title relative to the alignment within chart.spacingLeft and chart.spacingRight.
    * @param y             The y position of the title relative to the alignment within <a href="#chart.spacingTop">chart.spacingTop</a> and <a href="#chart.spacingBottom">chart.spacingBottom</a>. By default it depends on the font size.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            floating: js.UndefOr[Boolean] = js.undefined,
            margin: js.UndefOr[Double] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            text: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            widthAdjust: js.UndefOr[Double] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): Title = {
    val alignOuter = align.map(_.name)
    val floatingOuter = floating
    val marginOuter = margin
    val styleOuter = style.map(stringToStyleObject)
    val textOuter = text
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val widthAdjustOuter = widthAdjust
    val xOuter = x
    val yOuter = y

    new Title {
      override val align = alignOuter
      override val floating = floatingOuter
      override val margin = marginOuter
      override val style = styleOuter
      override val text = textOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val widthAdjust = widthAdjustOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
