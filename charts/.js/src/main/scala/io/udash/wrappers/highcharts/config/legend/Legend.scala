/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package legend

import io.udash.wrappers.highcharts.api.{Point, Series}
import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.`|`


trait Legend extends js.Object {
  /**
    * <p>The horizontal alignment of the legend box within the chart area. Valid values are <code>left</code>, <code>center</code> and <code>right</code>.</p>
    *
    * <p>In the case that the legend is aligned in a corner position, the <code>layout</code> option will determine whether to place it above/below or on the side of the plot area.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/align/" target="_blank">Legend at the right of the chart</a>
    */
  val align: js.UndefOr[String] = js.undefined

  /**
    * The background color of the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/backgroundcolor/" target="_blank">Yellowish background</a>
    */
  val backgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the drawn border around the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/bordercolor/" target="_blank">Brown border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The border corner radius of the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/borderradius-default/" target="_blank">Square by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/borderradius-round/" target="_blank">5px rounded</a>
    */
  val borderRadius: js.UndefOr[Double] = js.undefined

  /**
    * The width of the drawn border around the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/borderwidth/" target="_blank">2px border width</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * Enable or disable the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/enabled-false/" target="_blank">Legend disabled</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * When the legend is floating, the plot area ignores it and is allowed to be placed below it.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/floating-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/floating-true/" target="_blank">true</a>.
    */
  val floating: js.UndefOr[Boolean] = js.undefined

  /**
    * In a legend with horizontal layout, the itemDistance defines the pixel distance between each item.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/layout-horizontal/" target="_blank">50px item distance</a>
    */
  val itemDistance: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for each legend item when the corresponding series or point is hidden. Only a subset of CSS is supported,
    * notably those options related to text. Properties are inherited from <code>style</code> unless overridden here. Defaults to:
    * <pre>itemHiddenStyle: {
    * color: '#CCC'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/itemhiddenstyle/" target="_blank">Darker gray color</a>
    */
  val itemHiddenStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * CSS styles for each legend item in hover mode. Only a subset of CSS is supported, notably those options related to text. Properties are inherited from <code>style</code> unless overridden here. Defaults to:
    * <pre>itemHoverStyle: {
    * color: '#000'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/itemhoverstyle/" target="_blank">Red on hover</a>
    */
  val itemHoverStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * The pixel bottom margin for each legend item.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/padding-itemmargin/" target="_blank">
    *          Padding and item margins demonstrated</a>
    */
  val itemMarginBottom: js.UndefOr[Double] = js.undefined

  /**
    * The pixel top margin for each legend item.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/padding-itemmargin/" target="_blank">
    *          Padding and item margins demonstrated</a>
    */
  val itemMarginTop: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for each legend item. Only a subset of CSS is supported, notably those options related to text.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/itemstyle/" target="_blank">Bold black text</a>
    */
  val itemStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * The width for each legend item. This is useful in a horizontal layout with many items when you want the items to align vertically.  .
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/itemwidth-default/" target="_blank">Null by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/itemwidth-80/" target="_blank">80 for aligned legend items</a>
    */
  val itemWidth: js.UndefOr[Double] = js.undefined

  /**
    * A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for each legend label.
    * Available variables relates to properties on the series, or the point in case of pies.
    */
  val labelFormat: js.UndefOr[String] = js.undefined

  /**
    * Callback function to format each of the series' labels. The <em>this</em> keyword refers to the series object,
    * or the point object in case of pie charts. By default the series or point name is printed.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/labelformatter/" target="_blank">Add text</a>
    */
  val labelFormatter: js.UndefOr[js.ThisFunction0[js.Object, String]] = js.undefined

  /**
    * The layout of the legend items. Can be one of "horizontal" or "vertical".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/layout-horizontal/" target="_blank">Horizontal by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/layout-vertical/" target="_blank">vertical</a>
    */
  val layout: js.UndefOr[String] = js.undefined

  /**
    * Line height for the legend items. Deprecated as of 2.1. Instead, the line height for each  item can be set using itemStyle.lineHeight, and the padding between items using itemMarginTop and itemMarginBottom.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/lineheight/" target="_blank">Setting padding</a>.
    */
  val lineHeight: js.UndefOr[Double] = js.undefined

  /**
    * If the plot area sized is calculated automatically and the legend is not floating, the legend margin is the  space between the legend and the axis labels or plot area.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/margin-default/" target="_blank">12 pixels by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/margin-30/" target="_blank">30 pixels</a>.
    */
  val margin: js.UndefOr[Double] = js.undefined

  /**
    * Maximum pixel height for the legend. When the maximum height is extended, navigation will show.
    */
  val maxHeight: js.UndefOr[Double] = js.undefined

  /**
    * Options for the paging or navigation appearing when the legend is overflown. Navigation works well on screen, but not in static exported images. One way of working around that is to <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation-enabled-false/">increase the chart height in export</a>.
    */
  val navigation: js.UndefOr[LegendNavigation] = js.undefined

  /**
    * The inner padding of the legend box.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/padding-itemmargin/" target="_blank">
    *          Padding and item margins demonstrated</a>
    */
  val padding: js.UndefOr[Double] = js.undefined

  /**
    * Whether to reverse the order of the legend items compared to the order of the series or points as defined in the configuration object.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/reversed/" target="_blank">Stacked bar with reversed legend</a>
    */
  val reversed: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to show the symbol on the right side of the text rather than the left side.  This is common in Arabic and Hebraic.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/rtl/" target="_blank">Symbol to the right</a>
    */
  val rtl: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to apply a drop shadow to the legend. A <code>backgroundColor</code>
    * also needs to be applied for this to take effect. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/shadow/" target="_blank">White background and drop shadow</a>
    */
  val shadow: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * When this is true, the legend symbol width will be the same as the symbol height, which in turn defaults to the font size of the legend items.
    */
  val squareSymbol: js.UndefOr[Boolean] = js.undefined

  /**
    * CSS styles for the legend area. In the 1.x versions the position of the legend area was determined by CSS. In 2.x, the position is determined by properties like  <code>align</code>, <code>verticalAlign</code>, <code>x</code> and <code>y</code>, but the styles are still parsed for backwards compatibility.
    */
  @deprecated("Deprecated in native HighCharts", "0.5.0")
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The pixel height of the symbol for series types that use a rectangle in the legend. Defaults to the font size of legend items.
    */
  val symbolHeight: js.UndefOr[Double] = js.undefined

  /**
    * The pixel padding between the legend item symbol and the legend item text.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/symbolpadding/" target="_blank">Greater symbol width and padding</a>
    */
  val symbolPadding: js.UndefOr[Double] = js.undefined

  /**
    * The border radius of the symbol for series types that use a rectangle in the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/symbolradius/" target="_blank">Round symbols</a>
    */
  val symbolRadius: js.UndefOr[Double] = js.undefined

  /**
    * The pixel width of the legend item symbol.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/symbolwidth/" target="_blank">Greater symbol width and padding</a>
    */
  val symbolWidth: js.UndefOr[Double] = js.undefined

  /**
    * A title to be added on top of the legend.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/title/" target="_blank">Legend title</a>
    */
  val title: js.UndefOr[LegendTitle] = js.undefined

  /**
    * <p>Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the legend item texts. Prior to 4.1.7, when using HTML, <a href="#legend.navigation">legend.navigation</a> was disabled.</p>
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>The vertical alignment of the legend box. Can be one of <code>top</code>, <code>middle</code> or  <code>bottom</code>. Vertical position can be further determined by the <code>y</code> option.</p>
    *
    * <p>In the case that the legend is aligned in a corner position, the <code>layout</code> option will determine whether to place it above/below or on the side of the plot area.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/verticalalign/" target="_blank">Legend 100px from the top of the chart</a>
    */
  val verticalAlign: js.UndefOr[String] = js.undefined

  /**
    * The width of the legend box.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/width/" target="_blank">Aligned to the plot area</a>
    */
  val width: js.UndefOr[Double] = js.undefined

  /**
    * The x offset of the legend relative to its horizontal alignment <code>align</code> within chart.spacingLeft and chart.spacingRight. Negative x moves it to the left, positive x moves it to the right.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/width/" target="_blank">Aligned to the plot area</a>
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The vertical offset of the legend relative to it's vertical alignment <code>verticalAlign</code> within chart.spacingTop and chart.spacingBottom. Negative y moves it up, positive y moves it down.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/verticalalign/" target="_blank">Legend 100px from the top of the chart</a>
    */
  val y: js.UndefOr[Double] = js.undefined
}

object Legend {

  /**
    * @param align            <p>The horizontal alignment of the legend box within the chart area. Valid values are <code>left</code>, <code>center</code> and <code>right</code>.</p>. . <p>In the case that the legend is aligned in a corner position, the <code>layout</code> option will determine whether to place it above/below or on the side of the plot area.</p>
    * @param backgroundColor  The background color of the legend.
    * @param borderColor      The color of the drawn border around the legend.
    * @param borderRadius     The border corner radius of the legend.
    * @param borderWidth      The width of the drawn border around the legend.
    * @param enabled          Enable or disable the legend.
    * @param floating         When the legend is floating, the plot area ignores it and is allowed to be placed below it.
    * @param itemDistance     In a legend with horizontal layout, the itemDistance defines the pixel distance between each item.
    * @param itemHiddenStyle  CSS styles for each legend item when the corresponding series or point is hidden. Only a subset of CSS is supported, notably those options related to text. Properties are inherited from <code>style</code> unless overridden here. Defaults to:. <pre>itemHiddenStyle: {. 	color: '#CCC'. }</pre>
    * @param itemHoverStyle   CSS styles for each legend item in hover mode. Only a subset of CSS is supported, notably those options related to text. Properties are inherited from <code>style</code> unless overridden here. Defaults to:. <pre>itemHoverStyle: {. 	color: '#000'. }</pre>
    * @param itemMarginBottom The pixel bottom margin for each legend item.
    * @param itemMarginTop    The pixel top margin for each legend item.
    * @param itemStyle        CSS styles for each legend item. Only a subset of CSS is supported, notably those options related to text.
    * @param itemWidth        The width for each legend item. This is useful in a horizontal layout with many items when you want the items to align vertically.  .
    * @param labelFormat      A <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting">format string</a> for each legend label. Available variables relates to properties on the series, or the point in case of pies.
    * @param labelFormatter   Callback function to format each of the series' labels. The <em>this</em> keyword refers to the series object, or the point object in case of pie charts. By default the series or point name is printed.
    * @param layout           The layout of the legend items. Can be one of "horizontal" or "vertical".
    * @param lineHeight       Line height for the legend items. Deprecated as of 2.1. Instead, the line height for each  item can be set using itemStyle.lineHeight, and the padding between items using itemMarginTop and itemMarginBottom.
    * @param margin           If the plot area sized is calculated automatically and the legend is not floating, the legend margin is the  space between the legend and the axis labels or plot area.
    * @param maxHeight        Maximum pixel height for the legend. When the maximum height is extended, navigation will show.
    * @param navigation       Options for the paging or navigation appearing when the legend is overflown. Navigation works well on screen, but not in static exported images. One way of working around that is to <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/navigation-enabled-false/">increase the chart height in export</a>.
    * @param padding          The inner padding of the legend box.
    * @param reversed         Whether to reverse the order of the legend items compared to the order of the series or points as defined in the configuration object.
    * @param rtl              Whether to show the symbol on the right side of the text rather than the left side.  This is common in Arabic and Hebraic.
    * @param shadow           Whether to apply a drop shadow to the legend. A <code>backgroundColor</code>.  also needs to be applied for this to take effect. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param squareSymbol     When this is true, the legend symbol width will be the same as the symbol height, which in turn defaults to the font size of the legend items.
    * @param symbolHeight     The pixel height of the symbol for series types that use a rectangle in the legend. Defaults to the font size of legend items.
    * @param symbolPadding    The pixel padding between the legend item symbol and the legend item text.
    * @param symbolRadius     The border radius of the symbol for series types that use a rectangle in the legend.
    * @param symbolWidth      The pixel width of the legend item symbol.
    * @param title            A title to be added on top of the legend.
    * @param useHTML          <p>Whether to <a href="http://www.highcharts.com/docs/chart-concepts/labels-and-string-formatting#html">use HTML</a> to render the legend item texts. Prior to 4.1.7, when using HTML, <a href="#legend.navigation">legend.navigation</a> was disabled.</p>
    * @param verticalAlign    <p>The vertical alignment of the legend box. Can be one of <code>top</code>, <code>middle</code> or  <code>bottom</code>. Vertical position can be further determined by the <code>y</code> option.</p>. . <p>In the case that the legend is aligned in a corner position, the <code>layout</code> option will determine whether to place it above/below or on the side of the plot area.</p>
    * @param width            The width of the legend box.
    * @param x                The x offset of the legend relative to its horizontal alignment <code>align</code> within chart.spacingLeft and chart.spacingRight. Negative x moves it to the left, positive x moves it to the right.
    * @param y                The vertical offset of the legend relative to it's vertical alignment <code>verticalAlign</code> within chart.spacingTop and chart.spacingBottom. Negative y moves it up, positive y moves it down.
    */
  def apply(align: js.UndefOr[Align] = js.undefined,
            backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            floating: js.UndefOr[Boolean] = js.undefined,
            itemDistance: js.UndefOr[Double] = js.undefined,
            itemHiddenStyle: js.UndefOr[String] = js.undefined,
            itemHoverStyle: js.UndefOr[String] = js.undefined,
            itemMarginBottom: js.UndefOr[Double] = js.undefined,
            itemMarginTop: js.UndefOr[Double] = js.undefined,
            itemStyle: js.UndefOr[String] = js.undefined,
            itemWidth: js.UndefOr[Double] = js.undefined,
            labelFormat: js.UndefOr[String] = js.undefined,
            labelFormatter: js.UndefOr[(Either[Series, Point] => String)] = js.undefined,
            layout: js.UndefOr[Layout] = js.undefined,
            lineHeight: js.UndefOr[Double] = js.undefined,
            margin: js.UndefOr[Double] = js.undefined,
            maxHeight: js.UndefOr[Double] = js.undefined,
            navigation: js.UndefOr[LegendNavigation] = js.undefined,
            padding: js.UndefOr[Double] = js.undefined,
            reversed: js.UndefOr[Boolean] = js.undefined,
            rtl: js.UndefOr[Boolean] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            squareSymbol: js.UndefOr[Boolean] = js.undefined,
            symbolHeight: js.UndefOr[Double] = js.undefined,
            symbolPadding: js.UndefOr[Double] = js.undefined,
            symbolRadius: js.UndefOr[Double] = js.undefined,
            symbolWidth: js.UndefOr[Double] = js.undefined,
            title: js.UndefOr[LegendTitle] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            verticalAlign: js.UndefOr[VerticalAlign] = js.undefined,
            width: js.UndefOr[Double] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): Legend = {
    val alignOuter = align.map(_.name)
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val enabledOuter = enabled
    val floatingOuter = floating
    val itemDistanceOuter = itemDistance
    val itemHiddenStyleOuter = itemHiddenStyle.map(stringToStyleObject)
    val itemHoverStyleOuter = itemHoverStyle.map(stringToStyleObject)
    val itemMarginBottomOuter = itemMarginBottom
    val itemMarginTopOuter = itemMarginTop
    val itemStyleOuter = itemStyle.map(stringToStyleObject)
    val itemWidthOuter = itemWidth
    val labelFormatOuter = labelFormat
    val labelFormatterOuter = labelFormatter.map(f => js.ThisFunction.fromFunction1(
      (arg: js.Object) =>
        // This is something like v: Point =>
        if (arg.hasOwnProperty("series")) f(Right(arg.asInstanceOf[Point]))
        else f(Left(arg.asInstanceOf[Series]))
    ))
    val layoutOuter = layout.map(_.name)
    val lineHeightOuter = lineHeight
    val marginOuter = margin
    val maxHeightOuter = maxHeight
    val navigationOuter = navigation
    val paddingOuter = padding
    val reversedOuter = reversed
    val rtlOuter = rtl
    val shadowOuter = shadow.map(_.value)
    val squareSymbolOuter = squareSymbol
    val symbolHeightOuter = symbolHeight
    val symbolPaddingOuter = symbolPadding
    val symbolRadiusOuter = symbolRadius
    val symbolWidthOuter = symbolWidth
    val titleOuter = title
    val useHTMLOuter = useHTML
    val verticalAlignOuter = verticalAlign.map(_.name)
    val widthOuter = width
    val xOuter = x
    val yOuter = y

    new Legend {
      override val align = alignOuter
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val enabled = enabledOuter
      override val floating = floatingOuter
      override val itemDistance = itemDistanceOuter
      override val itemHiddenStyle = itemHiddenStyleOuter
      override val itemHoverStyle = itemHoverStyleOuter
      override val itemMarginBottom = itemMarginBottomOuter
      override val itemMarginTop = itemMarginTopOuter
      override val itemStyle = itemStyleOuter
      override val itemWidth = itemWidthOuter
      override val labelFormat = labelFormatOuter
      override val labelFormatter = labelFormatterOuter
      override val layout = layoutOuter
      override val lineHeight = lineHeightOuter
      override val margin = marginOuter
      override val maxHeight = maxHeightOuter
      override val navigation = navigationOuter
      override val padding = paddingOuter
      override val reversed = reversedOuter
      override val rtl = rtlOuter
      override val shadow = shadowOuter
      override val squareSymbol = squareSymbolOuter
      override val symbolHeight = symbolHeightOuter
      override val symbolPadding = symbolPaddingOuter
      override val symbolRadius = symbolRadiusOuter
      override val symbolWidth = symbolWidthOuter
      override val title = titleOuter
      override val useHTML = useHTMLOuter
      override val verticalAlign = verticalAlignOuter
      override val width = widthOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
