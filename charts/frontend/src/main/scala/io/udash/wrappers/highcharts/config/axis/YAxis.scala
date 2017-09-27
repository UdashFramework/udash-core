/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.{ThisFunction, `|`}


@js.annotation.ScalaJSDefined
class YAxis extends Axis[YAxis, YAxisEvents] {

  /**
    * In a polar chart, this is the angle of the Y axis in degrees, where 0 is up and 90 is right.
    * The angle determines the position of the axis line and the labels, though the coordinate system is unaffected.
    * Defaults to 0.
    *
    * @example <a href="http://jsfiddle.net/gh/get/library/pure/highcharts/highcharts/tree/master/samples/highcharts/yaxis/angle/" target="_blank">Dual axis polar chart</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/offset-centered/" target="_blank">Axes positioned in the center of the plot</a>
    */
  val angle: js.UndefOr[Double] = js.undefined

  /**
    * Polar charts only. Whether the grid lines should draw as a polygon with straight lines between categories, or as circles.
    * Can be either <code>circle</code> or <code>polygon</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/polar-spider/" target="_blank">Polygon grid lines</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/gridlineinterpolation/" target="_blank">circle and polygon</a>
    */
  val gridLineInterpolation: js.UndefOr[String] = js.undefined

  /**
    * Solid gauge only. Unless <a href="#yAxis.stops">stops</a> are set, the color to represent the maximum value of the Y axis.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/mincolor-maxcolor/" target="_blank">Min and max colors</a>
    */
  val maxColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Solid gauge only. Unless <a href="#yAxis.stops">stops</a> are set, the color to represent the minimum value of the Y axis.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/mincolor-maxcolor/" target="_blank">Min and max color</a>
    */
  val minColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * If <code>true</code>, the first series in a stack will be drawn on top in a positive, non-reversed Y axis.
    * If <code>false</code>, the first series is in the base of the stack.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/yaxis/reversedstacks-false/" target="_blank">Non-reversed stacks</a>.
    */
  val reversedStacks: js.UndefOr[Boolean] = js.undefined

  /**
    * The stack labels show the total value for each bar in a stacked column or bar chart. The label will be placed on top of
    * 	positive columns and below negative columns. In case of an inverted column chart or a bar chart the label is placed to 
    * 	the right of positive bars and to the left of negative bars.
    */
  val stackLabels: js.UndefOr[YAxisStackLabels] = js.undefined

  /**
    * <p>Solid gauge series only. Color stops for the solid gauge. Use this in cases where a linear gradient between a
    * <code>minColor</code> and <code>maxColor</code> is not sufficient. The stops is an array of tuples, where the first
    * item is a float between 0 and 1 assigning the relative position in the gradient, and the second item is the color.</p>
    * 
    * <p>For solid gauges, the Y axis also inherits the concept of <a href="http://api.highcharts.com/highmaps#colorAxis.dataClasses">data classes</a>
    * from the Highmaps color axis.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/gauge-solid/" target="_blank">True by default</a>
    */
  val stops: js.UndefOr[js.Array[js.Array[Double | String | js.Object]]] = js.undefined
}

object YAxis {
  import scala.scalajs.js.JSConverters._

  final class GridLineInterpolation(val name: String) extends AnyVal
  object GridLineInterpolation {
    val Circle = new GridLineInterpolation("circle")
    val Polygon = new GridLineInterpolation("polygon")
  }

  /**
    * @param allowDecimals Whether to allow decimals in this axis' ticks. When counting integers, like persons or hits on a web page, decimals should be avoided in the labels.
    * @param alternateGridColor When using an alternate grid color, a band is painted across the plot area between every other grid line.
    * @param angle In a polar chart, this is the angle of the Y axis in degrees, where 0 is up and 90 is right.
    * @param breaks An array defining breaks in the axis, the sections defined will be left out and all the points shifted closer to each other. Requires that the broken-axis.js module is loaded.
    * @param categories <p>If categories are present for the xAxis, names are used instead of numbers for that axis. Since Highcharts 3.0, categories can also be extracted by giving each point a <a href="#series.data">name</a> and setting axis <a href="#xAxis.type">type</a> to <code>category</code>. However, if you have multiple series, best practice remains defining the <code>categories</code> array.</p>. . <p>Example:. <pre>categories: ['Apples', 'Bananas', 'Oranges']</pre>. 		 Defaults to <code>null</code>. </p>
    * @param ceiling The highest allowed value for automatically computed axis extremes.
    * @param crosshair Configure a crosshair that follows either the mouse pointer or the hovered point.
    * @param dateTimeLabelFormats For a datetime axis, the scale will automatically adjust to the appropriate unit.  This member gives the default string representations used for each unit. For intermediate values, different units may be used, for example the <code>day</code> unit can be used on midnight and <code>hour</code> unit be used for intermediate values on the same axis. For an overview of the replacement codes, see <a href="#Highcharts.dateFormat">dateFormat</a>.. . Defaults to:. <pre>{. 	millisecond: '%H:%M:%S.%L',. 	second: '%H:%M:%S',. 	minute: '%H:%M',. 	hour: '%H:%M',. 	day: '%e. %b',. 	week: '%e. %b',. 	month: '%b \'%y',. 	year: '%Y'. }</pre>
    * @param endOnTick Whether to force the axis to end on a tick. Use this option with the <code>maxPadding</code> option to control the axis end.
    * @param events Event handlers for the axis.
    * @param floor The lowest allowed value for automatically computed axis extremes.
    * @param gridLineColor Color of the grid lines extending the ticks across the plot area.
    * @param gridLineDashStyle The dash or dot style of the grid lines. For possible values, see <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this demonstration</a>.
    * @param gridLineInterpolation Polar charts only. Whether the grid lines should draw as a polygon with straight lines between categories, or as circles.
    * @param gridLineWidth The width of the grid lines extending the ticks across the plot area.
    * @param gridZIndex The Z index of the grid lines.
    * @param id An id for the axis. This can be used after render time to get a pointer to the axis object through <code>chart.get()</code>.
    * @param labels The axis labels show the number or category for each tick.
    * @param lineColor The color of the line marking the axis itself.
    * @param lineWidth The width of the line marking the axis itself.
    * @param linkedTo Index of another axis that this axis is linked to. When an axis is linked to a master axis, it will take the same extremes as the master, but as assigned by min or max or by setExtremes. It can be used to show additional info, or to ease reading the chart by duplicating the scales.
    * @param max <p>The maximum value of the axis. If <code>null</code>, the max value is automatically calculated. If the <code>endOnTick</code> option is true, the <code>max</code> value might be rounded up.</p>. . <p>If a <a href="#yAxis.tickAmount">tickAmount</a> is set, the axis may be extended beyond the set max in order to reach the given number of ticks. The same may happen in a chart with multiple axes, determined by  <a class="internal" href="#chart">chart.alignTicks</a>, where a <code>tickAmount</code> is applied internally.</p>
    * @param maxColor Solid gauge only. Unless stops are set, the color to represent the maximum value of the Y axis.
    * @param maxPadding Padding of the max value relative to the length of the axis. A padding of 0.05 will make a 100px axis 5px longer. This is useful when you don't want the highest data value to appear on the edge of the plot area. When the axis' <code>max</code> option is set or a max extreme is set using <code>axis.setExtremes()</code>, the maxPadding will be ignored.
    * @param min The minimum value of the axis. If <code>null</code> the min value is automatically calculated. If the <code>startOnTick</code> option is true, the <code>min</code> value might be rounded down.
    * @param minColor Solid gauge only. Unless stops are set, the color to represent the minimum value of the Y axis.
    * @param minPadding Padding of the min value relative to the length of the axis. A padding of 0.05 will make a 100px axis 5px longer. This is useful when you don't want the lowest data value to appear on the edge of the plot area. When the axis' <code>min</code> option is set or a min extreme is set using <code>axis.setExtremes()</code>, the minPadding will be ignored.
    * @param minRange <p>The minimum range to display on this axis. The entire axis will not be allowed to span over a smaller interval than this. For example, for a datetime axis the main unit is milliseconds. If minRange is set to 3600000, you can't zoom in more than to one hour.</p> . . <p>The default minRange for the x axis is five times the smallest interval between any of the data points.</p> . . <p>On a logarithmic axis, the unit for the minimum range is the power. So a minRange of 	1 means that the axis can be zoomed to 10-100, 100-1000, 1000-10000 etc.</p>. . <p>Note that the <code>minPadding</code>, <code>maxPadding</code>, <code>startOnTick</code> and <code>endOnTick</code> settings also affect how the extremes of the axis are computed.</p>
    * @param minTickInterval The minimum tick interval allowed in axis values. For example on zooming in on an axis with daily data, this can be used to prevent the axis from showing hours. Defaults to the closest distance between two points on the axis.
    * @param minorGridLineColor Color of the minor, secondary grid lines.
    * @param minorGridLineDashStyle The dash or dot style of the minor grid lines. For possible values, see <a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this demonstration</a>.
    * @param minorGridLineWidth Width of the minor, secondary grid lines.
    * @param minorTickColor Color for the minor tick marks.
    * @param minorTickInterval <p>Tick interval in scale units for the minor ticks. On a linear axis, if <code>"auto"</code>, .  the minor tick interval is calculated as a fifth of the tickInterval. If.  <code>null</code>, minor ticks are not shown.</p>.  <p>On logarithmic axes, the unit is the power of the value. For example, setting.  	the minorTickInterval to 1 puts one tick on each of 0.1, 1, 10, 100 etc. Setting.  	the minorTickInterval to 0.1 produces 9 ticks between 1 and 10, .  	10 and 100 etc. A minorTickInterval of "auto" on a log axis results in a best guess,.  	attempting to enter approximately 5 minor ticks between each major tick.</p>. . <p>If user settings dictate minor ticks to become too dense, they don't make sense, and will be ignored to prevent performance problems.</a>. . <p>On axes using <a href="#xAxis.categories">categories</a>, minor ticks are not supported.</p>
    * @param minorTickLength The pixel length of the minor tick marks.
    * @param minorTickPosition The position of the minor tick marks relative to the axis line. Can be one of <code>inside</code> and <code>outside</code>.
    * @param minorTickWidth The pixel width of the minor tick mark.
    * @param offset The distance in pixels from the plot area to the axis line. A positive offset moves the axis with it's line, labels and ticks away from the plot area. This is typically used when two or more axes are displayed on the same side of the plot. With multiple axes the offset is dynamically adjusted to avoid collision, this can be overridden by setting offset explicitly.
    * @param opposite Whether to display the axis on the opposite side of the normal. The normal is on the left side for vertical axes and bottom for horizontal, so the opposite sides will be right and top respectively. This is typically used with dual or multiple axes.
    * @param plotBands <p>An array of colored bands stretching across the plot area marking an interval on the axis.</p>. . <p>In a gauge, a plot band on the Y axis (value axis) will stretch along the perimeter of the gauge.</p>
    * @param plotLines An array of lines stretching across the plot area, marking a specific value on one of the axes.
    * @param reversed Whether to reverse the axis so that the highest number is closest to the origin. If the chart is inverted, the x axis is reversed by default.
    * @param reversedStacks If true, the first series in a stack will be drawn on top in a positive, non-reversed Y axis. If false, the first series is in the base of the stack.
    * @param showEmpty Whether to show the axis line and title when the axis has no data.
    * @param showFirstLabel Whether to show the first tick label.
    * @param showLastLabel Whether to show the last tick label.
    * @param stackLabels The stack labels show the total value for each bar in a stacked column or bar chart. The label will be placed on top of positive columns and below negative columns. In case of an inverted column chart or a bar chart the label is placed to the right of positive bars and to the left of negative bars.
    * @param startOfWeek For datetime axes, this decides where to put the tick between weeks. 0 = Sunday, 1 = Monday.
    * @param startOnTick Whether to force the axis to start on a tick. Use this option with the <code>minPadding</code> option to control the axis start.
    * @param stops Solid gauge series only. Color stops for the solid gauge. Use this in cases where a linear gradient between a minColor and maxColor is not sufficient. The stops is an array of tuples, where the first item is a float between 0 and 1 assigning the relative position in the gradient, and the second item is the color.
    * @param tickAmount <p>The amount of ticks to draw on the axis. This opens up for aligning the ticks of multiple charts or panes within a chart. This option overrides the <code>tickPixelInterval</code> option.</p>. <p>This option only has an effect on linear axes. Datetime, logarithmic or category axes are not affected.</p>
    * @param tickColor Color for the main tick marks.
    * @param tickInterval <p>The interval of the tick marks in axis units. When <code>null</code>, the tick interval.  is computed to approximately follow the <a href="#xAxis.tickPixelInterval">tickPixelInterval</a> on linear and datetime axes..  On categorized axes, a <code>null</code> tickInterval will default to 1, one category. .  Note that datetime axes are based on milliseconds, so for .  example an interval of one day is expressed as <code>24 * 3600 * 1000</code>.</p>.  <p>On logarithmic axes, the tickInterval is based on powers, so a tickInterval of 1 means.  	one tick on each of 0.1, 1, 10, 100 etc. A tickInterval of 2 means a tick of 0.1, 10, 1000 etc..  	A tickInterval of 0.2 puts a tick on 0.1, 0.2, 0.4, 0.6, 0.8, 1, 2, 4, 6, 8, 10, 20, 40 etc.</p>. . <p>If the tickInterval is too dense for labels to be drawn, Highcharts may remove ticks.</p>. . <p>If the chart has multiple axes, the <a href="#chart.alignTicks">alignTicks</a> option may interfere with the <code>tickInterval</code> setting.</p>
    * @param tickLength The pixel length of the main tick marks.
    * @param tickPixelInterval If tickInterval is <code>null</code> this option sets the approximate pixel interval of the.  tick marks. Not applicable to categorized axis. Defaults to <code>72</code> .  for the Y axis and <code>100</code> for	the X axis.
    * @param tickPosition The position of the major tick marks relative to the axis line. Can be one of <code>inside</code> and <code>outside</code>.
    * @param tickPositioner A callback function returning array defining where the ticks are laid out on the axis. This overrides the default behaviour of <a href="#xAxis.tickPixelInterval">tickPixelInterval</a> and <a href="#xAxis.tickInterval">tickInterval</a>. The automatic tick positions are accessible through <code>this.tickPositions</code> and can be modified by the callback.
    * @param tickPositions An array defining where the ticks are laid out on the axis. This overrides the default behaviour of <a href="#xAxis.tickPixelInterval">tickPixelInterval</a> and <a href="#xAxis.tickInterval">tickInterval</a>.
    * @param tickWidth The pixel width of the major tick marks.
    * @param tickmarkPlacement For categorized axes only. If <code>on</code> the tick mark is placed in the center of  the category, if <code>between</code> the tick mark is placed between categories. The default is <code>between</code> if the <code>tickInterval</code> is 1, else <code>on</code>.
    * @param title The axis title, showing next to the axis line.
    * @param `type` The type of axis. Can be one of <code>linear</code>, <code>logarithmic</code>, <code>datetime</code> or <code>category</code>. In a datetime axis, the numbers are given in milliseconds, and tick marks are placed 		on appropriate values like full hours or days. In a category axis, the <a href="#series<line>.data.name">point names</a> of the chart's series are used for categories, if not a <a href="#xAxis.categories">categories</a> array is defined.
    * @param units Datetime axis only. An array determining what time intervals the ticks are allowed to fall on. Each array item is an array where the first value is the time unit and the  second value another array of allowed multiples. Defaults to:. <pre>units: [[. 	'millisecond', // unit name. 	[1, 2, 5, 10, 20, 25, 50, 100, 200, 500] // allowed multiples. ], [. 	'second',. 	[1, 2, 5, 10, 15, 30]. ], [. 	'minute',. 	[1, 2, 5, 10, 15, 30]. ], [. 	'hour',. 	[1, 2, 3, 4, 6, 8, 12]. ], [. 	'day',. 	[1]. ], [. 	'week',. 	[1]. ], [. 	'month',. 	[1, 3, 6]. ], [. 	'year',. 	null. ]]</pre>
    * @param visible Whether axis, including axis title, line, ticks and labels, should be visible.
    */
  def apply(angle: js.UndefOr[Double] = js.undefined,
            allowDecimals: js.UndefOr[Boolean] = js.undefined,
            alternateGridColor: js.UndefOr[Color] = js.undefined,
            breaks: js.UndefOr[Seq[AxisBreak]] = js.undefined,
            categories: js.UndefOr[Seq[String]] = js.undefined,
            ceiling: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            crosshair: js.UndefOr[AxisCrosshair] = js.undefined,
            dateTimeLabelFormats: js.UndefOr[DateTimeLabelFormats] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            endOnTick: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[YAxisEvents] = js.undefined,
            floor: js.UndefOr[Double] = js.undefined,
            gridLineColor: js.UndefOr[Color] = js.undefined,
            gridLineDashStyle: js.UndefOr[DashStyle] = js.undefined,
            gridLineInterpolation: js.UndefOr[GridLineInterpolation] = js.undefined,
            gridLineWidth: js.UndefOr[Double] = js.undefined,
            gridZIndex: js.UndefOr[Int] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            labels: js.UndefOr[YAxisLabel] = js.undefined,
            lineColor: js.UndefOr[Color] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            linkedTo: js.UndefOr[Int] = js.undefined,
            max: js.UndefOr[Double] = js.undefined,
            maxColor: js.UndefOr[Color] = js.undefined,
            maxPadding: js.UndefOr[Double] = js.undefined,
            min: js.UndefOr[Double] = js.undefined,
            minColor: js.UndefOr[Color] = js.undefined,
            minPadding: js.UndefOr[Double] = js.undefined,
            minRange: js.UndefOr[Double] = js.undefined,
            minTickInterval: js.UndefOr[Double] = js.undefined,
            minorGridLineColor: js.UndefOr[Color] = js.undefined,
            minorGridLineDashStyle: js.UndefOr[DashStyle] = js.undefined,
            minorGridLineWidth: js.UndefOr[Double] = js.undefined,
            minorTickColor: js.UndefOr[Color] = js.undefined,
            minorTickInterval: js.UndefOr[String | Double] = js.undefined,
            minorTickLength: js.UndefOr[Double] = js.undefined,
            minorTickPosition: js.UndefOr[InOutPosition] = js.undefined,
            minorTickWidth: js.UndefOr[Double] = js.undefined,
            offset: js.UndefOr[Double] = js.undefined,
            opposite: js.UndefOr[Boolean] = js.undefined,
            plotBands: js.UndefOr[Seq[YAxisPlotBand]] = js.undefined,
            plotLines: js.UndefOr[Seq[AxisPlotLine]] = js.undefined,
            reversed: js.UndefOr[Boolean] = js.undefined,
            reversedStacks: js.UndefOr[Boolean] = js.undefined,
            showEmpty: js.UndefOr[Boolean] = js.undefined,
            showFirstLabel: js.UndefOr[Boolean] = js.undefined,
            showLastLabel: js.UndefOr[Boolean] = js.undefined,
            softMax: js.UndefOr[Double] = js.undefined,
            softMin: js.UndefOr[Double] = js.undefined,
            stackLabels: js.UndefOr[YAxisStackLabels] = js.undefined,
            startOfWeek: js.UndefOr[DayOfWeek] = js.undefined,
            startOnTick: js.UndefOr[Boolean] = js.undefined,
            stops: js.UndefOr[Seq[(Double, Color)]] = js.undefined,
            tickAmount: js.UndefOr[Int] = js.undefined,
            tickColor: js.UndefOr[Color] = js.undefined,
            tickInterval: js.UndefOr[Double] = js.undefined,
            tickLength: js.UndefOr[Double] = js.undefined,
            tickPixelInterval: js.UndefOr[Double] = js.undefined,
            tickPosition: js.UndefOr[InOutPosition] = js.undefined,
            tickPositioner: js.UndefOr[(Axis.PositionerEvent) => Any] = js.undefined,
            tickPositions: js.UndefOr[Seq[Double]] = js.undefined,
            tickWidth: js.UndefOr[Double] = js.undefined,
            tickmarkPlacement: js.UndefOr[TickmarkPlacement] = js.undefined,
            title: js.UndefOr[AxisTitle] = js.undefined,
            `type`: js.UndefOr[Axis.Type] = js.undefined,
            units: js.UndefOr[Seq[(String, Seq[Int])]] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined): YAxis = {
    val angleOuter = angle
    val allowDecimalsOuter = allowDecimals
    val alternateGridColorOuter = alternateGridColor.map(_.c)
    val breaksOuter = breaks.map(_.toJSArray)
    val categoriesOuter = categories.map(_.toJSArray)
    val ceilingOuter = ceiling
    val classNameOuter = className
    val crosshairOuter = crosshair
    val dateTimeLabelFormatsOuter = dateTimeLabelFormats.map(DateTimeLabelFormats.toJSDict)
    val descriptionOuter = description
    val endOnTickOuter = endOnTick
    val eventsOuter = events
    val floorOuter = floor
    val gridLineColorOuter = gridLineColor.map(_.c)
    val gridLineDashStyleOuter = gridLineDashStyle.map(_.name)
    val gridLineInterpolationOuter = gridLineInterpolation.map(_.name)
    val gridLineWidthOuter = gridLineWidth
    val gridZIndexOuter = gridZIndex
    val idOuter = id
    val labelsOuter = labels
    val lineColorOuter = lineColor.map(_.c)
    val lineWidthOuter = lineWidth
    val linkedToOuter = linkedTo
    val maxOuter = max
    val maxColorOuter = maxColor.map(_.c)
    val maxPaddingOuter = maxPadding
    val minOuter = min
    val minColorOuter = minColor.map(_.c)
    val minPaddingOuter = minPadding
    val minRangeOuter = minRange
    val minTickIntervalOuter = minTickInterval
    val minorGridLineColorOuter = minorGridLineColor.map(_.c)
    val minorGridLineDashStyleOuter = minorGridLineDashStyle.map(_.name)
    val minorGridLineWidthOuter = minorGridLineWidth
    val minorTickColorOuter = minorTickColor.map(_.c)
    val minorTickIntervalOuter = minorTickInterval
    val minorTickLengthOuter = minorTickLength
    val minorTickPositionOuter = minorTickPosition.map(_.name)
    val minorTickWidthOuter = minorTickWidth
    val offsetOuter = offset
    val oppositeOuter = opposite
    val plotBandsOuter: js.UndefOr[js.Array[AxisPlotBand]] = plotBands.map(_.map(_.asInstanceOf[AxisPlotBand]).toJSArray)
    val plotLinesOuter = plotLines.map(_.toJSArray)
    val reversedOuter = reversed
    val reversedStacksOuter = reversedStacks
    val showEmptyOuter = showEmpty
    val showFirstLabelOuter = showFirstLabel
    val showLastLabelOuter = showLastLabel
    val softMaxOuter = softMax
    val softMinOuter = softMin
    val stackLabelsOuter = stackLabels
    val startOfWeekOuter = startOfWeek.map(_.id)
    val startOnTickOuter = startOnTick
    val stopsOuter = stops.map(_.map(i => js.Array[Double | String | js.Object](i._1, i._2.c)).toJSArray)
    val tickAmountOuter = tickAmount
    val tickColorOuter = tickColor.map(_.c)
    val tickIntervalOuter = tickInterval
    val tickLengthOuter = tickLength
    val tickPixelIntervalOuter = tickPixelInterval
    val tickPositionOuter = tickPosition.map(_.name)
    val tickPositionerOuter = tickPositioner.map(ThisFunction.fromFunction1[Axis.PositionerEvent, Any])
    val tickPositionsOuter = tickPositions.map(_.toJSArray)
    val tickWidthOuter = tickWidth
    val tickmarkPlacementOuter = tickmarkPlacement.map(_.name)
    val titleOuter = title
    val typeOuter = `type`.map(_.name)
    val unitsOuter = units.map(_.map(t => js.Array[js.Any](t._1, t._2)).toJSArray)
    val visibleOuter = visible

    new YAxis {
      override val angle = angleOuter
      override val allowDecimals = allowDecimalsOuter
      override val alternateGridColor = alternateGridColorOuter
      override val breaks = breaksOuter
      override val categories = categoriesOuter
      override val ceiling = ceilingOuter
      override val className = classNameOuter
      override val crosshair = crosshairOuter
      override val dateTimeLabelFormats = dateTimeLabelFormatsOuter
      override val description = descriptionOuter
      override val endOnTick = endOnTickOuter
      override val events = eventsOuter
      override val floor = floorOuter
      override val gridLineColor = gridLineColorOuter
      override val gridLineDashStyle = gridLineDashStyleOuter
      override val gridLineInterpolation = gridLineInterpolationOuter
      override val gridLineWidth = gridLineWidthOuter
      override val gridZIndex = gridZIndexOuter
      override val id = idOuter
      override val labels = labelsOuter
      override val lineColor = lineColorOuter
      override val lineWidth = lineWidthOuter
      override val linkedTo = linkedToOuter
      override val max = maxOuter
      override val maxColor = maxColorOuter
      override val maxPadding = maxPaddingOuter
      override val min = minOuter
      override val minColor = minColorOuter
      override val minPadding = minPaddingOuter
      override val minRange = minRangeOuter
      override val minTickInterval = minTickIntervalOuter
      override val minorGridLineColor = minorGridLineColorOuter
      override val minorGridLineDashStyle = minorGridLineDashStyleOuter
      override val minorGridLineWidth = minorGridLineWidthOuter
      override val minorTickColor = minorTickColorOuter
      override val minorTickInterval = minorTickIntervalOuter
      override val minorTickLength = minorTickLengthOuter
      override val minorTickPosition = minorTickPositionOuter
      override val minorTickWidth = minorTickWidthOuter
      override val offset = offsetOuter
      override val opposite = oppositeOuter
      override val plotBands = plotBandsOuter
      override val plotLines = plotLinesOuter
      override val reversed = reversedOuter
      override val reversedStacks = reversedStacksOuter
      override val showEmpty = showEmptyOuter
      override val showFirstLabel = showFirstLabelOuter
      override val showLastLabel = showLastLabelOuter
      override val softMax = softMaxOuter
      override val softMin = softMinOuter
      override val stackLabels = stackLabelsOuter
      override val startOfWeek = startOfWeekOuter
      override val startOnTick = startOnTickOuter
      override val stops = stopsOuter
      override val tickAmount = tickAmountOuter
      override val tickColor = tickColorOuter
      override val tickInterval = tickIntervalOuter
      override val tickLength = tickLengthOuter
      override val tickPixelInterval = tickPixelIntervalOuter
      override val tickPosition = tickPositionOuter
      override val tickPositioner = tickPositionerOuter
      override val tickPositions = tickPositionsOuter
      override val tickWidth = tickWidthOuter
      override val tickmarkPlacement = tickmarkPlacementOuter
      override val title = titleOuter
      override val `type` = typeOuter
      override val units = unitsOuter
      override val visible = visibleOuter
    }
  }
}