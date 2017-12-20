package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Color, DashStyle}

import scala.scalajs.js
import scala.scalajs.js.|

trait ColorAxis extends BaseAxis[ColorAxis, ColorAxisEvents] {

  /**
    * A class name that opens for styling the axis by CSS, especially in Highcharts styled mode. The class name is applied to group elements for the grid, axis elements and labels.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * Determines how to set each data class' color if no individual color is set. The default value, <code>tween</code>,
    * computes intermediate colors between <code>minColor</code> and <code>maxColor</code>.
    * The other possible value, <code>category</code>, pulls colors from the global or chart specific <a href="#colors">colors</a> array.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/dataclasscolor/" target="_blank">Category colors</a>
    */
  val dataClassColor: js.UndefOr[String] = js.undefined

  /**
    * An array of data classes or ranges for the choropleth map. If none given, the color axis is scalar and values
    * are distributed as a gradient between the minimum and maximum colors.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/demo/data-class-ranges/" target="_blank">Multiple ranges</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/demo/data-class-two-ranges/" target="_blank">two ranges</a>.
    */
  val dataClasses: js.UndefOr[js.Array[ColorAxisDataClasses]] = js.undefined

  /**
    * <p>The axis labels show the number for each tick.</p>
    * <p>For more live examples on label options, see <a href="/highcharts#xAxis.labels">xAxis.labels in the Highcharts API.</p>
    */
  val labels: js.UndefOr[ColorAxisLabel] = js.undefined

  /**
    * The triangular marker on a scalar color axis that points to the value of the hovered area. To disable the marker, set <code>marker: null</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/marker/" target="_blank">Black marker</a>
    */
  val marker: js.UndefOr[ColorAxisMarker] = js.undefined

  /**
    * <p>The color to represent the maximum of the color axis. Unless <a href="#colorAxis.dataClasses">dataClasses</a>
    * or <a href="#colorAxis.stops">stops</a> are set, the gradient ends at this value.</p>
    *
    * <p>If dataClasses are set, the color is based on minColor and maxColor unless a color is set for each data class,
    * or the <a href="#colorAxis.dataClassColor">dataClassColor</a> is set.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/mincolor-maxcolor/" target="_blank">Min and max colors on scalar (gradient) axis</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/mincolor-maxcolor-dataclasses/" target="_blank">on data classes</a>.
    */
  val maxColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * <p>The color to represent the minimum of the color axis. Unless <a href="#colorAxis.dataClasses">dataClasses</a>
    * or <a href="#colorAxis.stops">stops</a> are set, the gradient starts at this value.</p>
    *
    * <p>If dataClasses are set, the color is based on minColor and maxColor unless a color is set for each data class,
    * or the <a href="#colorAxis.dataClassColor">dataClassColor</a> is set.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/mincolor-maxcolor/" target="_blank">Min and max colors on scalar (gradient) axis</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/mincolor-maxcolor-dataclasses/" target="_blank">on data classes</a>.
    */
  val minColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Whether to force the axis to start on a tick. Use this option with the <code>maxPadding</code> option to control the axis start.
    */
  val startOnTick: js.UndefOr[Boolean] = js.undefined

  /**
    * Color stops for the gradient of a scalar color axis. Use this in cases where a linear gradient between
    * a <code>minColor</code> and <code>maxColor</code> is not sufficient. The stops is an array of tuples,
    * where the first item is a float between 0 and 1 assigning the relative position in the gradient, and the second item is the color.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/demo/heatmap/" target="_blank">Heatmap with three color stops</a>
    */
  val stops: js.UndefOr[js.Array[js.Array[Double | String | js.Object]]] = js.undefined
}

object ColorAxis {
  import scala.scalajs.js.JSConverters._

  final class DataClassColor(val value: String) extends AnyVal
  object DataClassColor {
    val Tween = new DataClassColor("tween")
    val Category = new DataClassColor("category")
  }

  final class InterpolationType(val value: String) extends AnyVal
  object InterpolationType {
    val Linear = new InterpolationType("linear")
    val Logarithmic = new InterpolationType("logarithmic")
  }

  /**
    * @param dataClassColor         Determines how to set each data class' color if no individual color is set. The default value, <code>tween</code>, computes intermediate colors between <code>minColor</code> and <code>maxColor</code>. The other possible value, <code>category</code>, pulls colors from the global or chart specific <a href="#colors">colors</a> array.
    * @param dataClasses            An array of data classes or ranges for the choropleth map. If none given, the color axis is scalar and values are distributed as a gradient between the minimum and maximum colors.
    * @param endOnTick              Whether to force the axis to end on a tick. Use this option with the <a href="#colorAxis.maxPadding">maxPadding</a> option to control the axis end.
    * @param events                 Event handlers for the axis.
    * @param gridLineColor          Color of the grid lines extending from the axis across the gradient.
    * @param gridLineDashStyle      The dash or dot style of the grid lines. For possible values, see <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this demonstration</a>.
    * @param gridLineWidth          The width of the grid lines extending from the axis across the gradient of a scalar color axis.
    * @param id                     An id for the axis. This can be used after render time to get a pointer to the axis object through <code>chart.get()</code>.
    * @param labels                 <p>The axis labels show the number for each tick.</p>. <p>For more live examples on label options, see <a href="/highcharts#xAxis.labels">xAxis.labels in the Highcharts API.</p>
    * @param lineColor              The color of the line marking the axis itself.
    * @param lineWidth              The width of the line marking the axis itself.
    * @param marker                 The triangular marker on a scalar color axis that points to the value of the hovered area. To disable the marker, set <code>marker: null</code>.
    * @param max                    The maximum value of the axis in terms of map point values. If <code>null</code>, the max value is automatically calculated. If the <code>endOnTick</code> option is true, the max value might be rounded up.
    * @param maxColor               <p>The color to represent the maximum of the color axis. Unless <a href="#colorAxis.dataClasses">dataClasses</a> or <a href="#colorAxis.stops">stops</a> are set, the gradient ends at this value.</p>. . <p>If dataClasses are set, the color is based on minColor and maxColor unless a color is set for each data class, or the <a href="#colorAxis.dataClassColor">dataClassColor</a> is set.</p>
    * @param maxPadding             Padding of the max value relative to the length of the axis. A padding of 0.05 will make a 100px axis 5px longer.
    * @param min                    The minimum value of the axis in terms of map point values. If <code>null</code>, the min value is automatically calculated. If the <code>startOnTick</code> option is true, the min value might be rounded down.
    * @param minColor               <p>The color to represent the minimum of the color axis. Unless <a href="#colorAxis.dataClasses">dataClasses</a> or <a href="#colorAxis.stops">stops</a> are set, the gradient starts at this value.</p>. . <p>If dataClasses are set, the color is based on minColor and maxColor unless a color is set for each data class, or the <a href="#colorAxis.dataClassColor">dataClassColor</a> is set.</p>
    * @param minPadding             Padding of the min value relative to the length of the axis. A padding of 0.05 will make a 100px axis 5px longer.
    * @param minorGridLineColor     Color of the minor, secondary grid lines.
    * @param minorGridLineDashStyle The dash or dot style of the minor grid lines. For possible values, see <a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this demonstration</a>.
    * @param minorGridLineWidth     Width of the minor, secondary grid lines.
    * @param minorTickColor         Color for the minor tick marks.
    * @param minorTickInterval      <p>Tick interval in scale units for the minor ticks. On a linear axis, if <code>"auto"</code>, .  the minor tick interval is calculated as a fifth of the tickInterval. If.  <code>null</code>, minor ticks are not shown.</p>.  <p>On logarithmic axes, the unit is the power of the value. For example, setting.  	the minorTickInterval to 1 puts one tick on each of 0.1, 1, 10, 100 etc. Setting.  	the minorTickInterval to 0.1 produces 9 ticks between 1 and 10, .  	10 and 100 etc. A minorTickInterval of "auto" on a log axis results in a best guess,.  	attempting to enter approximately 5 minor ticks between each major tick.</p>. . <p>If user settings dictate minor ticks to become too dense, they don't make sense, and will be ignored to prevent performance problems.</a>
    * @param minorTickLength        The pixel length of the minor tick marks.
    * @param minorTickPosition      The position of the minor tick marks relative to the axis line. Can be one of <code>inside</code> and <code>outside</code>.
    * @param minorTickWidth         The pixel width of the minor tick mark.
    * @param reversed               Whether to reverse the axis so that the highest number is closest to the origin. Defaults to <code>false</code> in a horizontal legend and <code>true</code> in a vertical legend, where the smallest value starts on top.
    * @param showFirstLabel         If labels are enabled, whether to show the first tick label.
    * @param showLastLabel          If labels are enabled, whether to show the last tick label.
    * @param startOnTick            Whether to force the axis to start on a tick. Use this option with the <code>maxPadding</code> option to control the axis start.
    * @param stops                  Color stops for the gradient of a scalar color axis. Use this in cases where a linear gradient between a <code>minColor</code> and <code>maxColor</code> is not sufficient. The stops is an array of tuples, where the first item is a float between 0 and 1 assigning the relative position in the gradient, and the second item is the color.
    * @param tickColor              Color for the main tick marks.
    * @param tickInterval           The interval of the tick marks in axis units. When <code>null</code>, the tick interval is computed to approximately follow the <code>tickPixelInterval</code>.
    * @param tickLength             The pixel length of the main tick marks.
    * @param tickPixelInterval      If <a href="#colorAxis.tickInterval">tickInterval</a> is <code>null</code> this option sets the approximate pixel interval of the tick marks.
    * @param tickPosition           The position of the major tick marks relative to the axis line. Can be one of <code>inside</code> and <code>outside</code>.
    * @param tickPositioner         A callback function returning array defining where the ticks are laid out on the axis. This overrides the default behaviour of <code>tickPixelInterval</code> and <code>tickInterval</code>.
    * @param tickPositions          An array defining where the ticks are laid out on the axis. This overrides the default behaviour of <code>tickPixelInterval</code> and <code>tickInterval</code>.
    * @param tickWidth              The pixel width of the major tick marks.
    * @param `type`                 The type of interpolation to use for the color axis. Can be <code>linear</code> or <code>logarithmic</code>.
    */
  def apply(className: js.UndefOr[String] = js.undefined,
            dataClassColor: js.UndefOr[DataClassColor] = js.undefined,
            dataClasses: js.UndefOr[Seq[ColorAxisDataClasses]] = js.undefined,
            endOnTick: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[ColorAxisEvents] = js.undefined,
            gridLineColor: js.UndefOr[Color] = js.undefined,
            gridLineDashStyle: js.UndefOr[DashStyle] = js.undefined,
            gridLineWidth: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            labels: js.UndefOr[ColorAxisLabel] = js.undefined,
            lineColor: js.UndefOr[Color] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            marker: js.UndefOr[ColorAxisMarker] = js.undefined,
            max: js.UndefOr[Double] = js.undefined,
            maxColor: js.UndefOr[Color] = js.undefined,
            maxPadding: js.UndefOr[Double] = js.undefined,
            min: js.UndefOr[Double] = js.undefined,
            minColor: js.UndefOr[Color] = js.undefined,
            minPadding: js.UndefOr[Double] = js.undefined,
            minorGridLineColor: js.UndefOr[Color] = js.undefined,
            minorGridLineDashStyle: js.UndefOr[String] = js.undefined,
            minorGridLineWidth: js.UndefOr[Double] = js.undefined,
            minorTickColor: js.UndefOr[Color] = js.undefined,
            minorTickInterval: js.UndefOr[String | Double] = js.undefined,
            minorTickLength: js.UndefOr[Double] = js.undefined,
            minorTickPosition: js.UndefOr[String] = js.undefined,
            minorTickWidth: js.UndefOr[Double] = js.undefined,
            reversed: js.UndefOr[Boolean] = js.undefined,
            showFirstLabel: js.UndefOr[Boolean] = js.undefined,
            showLastLabel: js.UndefOr[Boolean] = js.undefined,
            startOnTick: js.UndefOr[Boolean] = js.undefined,
            stops: js.UndefOr[Seq[(Double, Color)]] = js.undefined,
            tickColor: js.UndefOr[Color] = js.undefined,
            tickInterval: js.UndefOr[Double] = js.undefined,
            tickLength: js.UndefOr[Double] = js.undefined,
            tickPixelInterval: js.UndefOr[Double] = js.undefined,
            tickPosition: js.UndefOr[String] = js.undefined,
            tickPositioner: js.UndefOr[(Axis.PositionerEvent) => Any] = js.undefined,
            tickPositions: js.UndefOr[Seq[Double]] = js.undefined,
            tickWidth: js.UndefOr[Double] = js.undefined,
            `type`: js.UndefOr[InterpolationType] = js.undefined): ColorAxis = {
    val classNameOuter = className
    val dataClassColorOuter = dataClassColor.map(_.value)
    val dataClassesOuter = dataClasses.map(_.toJSArray)
    val endOnTickOuter = endOnTick
    val eventsOuter = events
    val gridLineColorOuter = gridLineColor.map(_.c)
    val gridLineDashStyleOuter = gridLineDashStyle.map(_.name)
    val gridLineWidthOuter = gridLineWidth
    val idOuter = id
    val labelsOuter = labels
    val lineColorOuter = lineColor.map(_.c)
    val lineWidthOuter = lineWidth
    val markerOuter = marker
    val maxOuter = max
    val maxColorOuter = maxColor.map(_.c)
    val maxPaddingOuter = maxPadding
    val minOuter = min
    val minColorOuter = minColor.map(_.c)
    val minPaddingOuter = minPadding
    val minorGridLineColorOuter = minorGridLineColor.map(_.c)
    val minorGridLineDashStyleOuter = minorGridLineDashStyle
    val minorGridLineWidthOuter = minorGridLineWidth
    val minorTickColorOuter = minorTickColor.map(_.c)
    val minorTickIntervalOuter = minorTickInterval
    val minorTickLengthOuter = minorTickLength
    val minorTickPositionOuter = minorTickPosition
    val minorTickWidthOuter = minorTickWidth
    val reversedOuter = reversed
    val showFirstLabelOuter = showFirstLabel
    val showLastLabelOuter = showLastLabel
    val startOnTickOuter = startOnTick
    val stopsOuter = stops.map(_.map(v => js.Array[Double | String | js.Object](v._1, v._2.c)).toJSArray)
    val tickColorOuter = tickColor.map(_.c)
    val tickIntervalOuter = tickInterval
    val tickLengthOuter = tickLength
    val tickPixelIntervalOuter = tickPixelInterval
    val tickPositionOuter = tickPosition
    val tickPositionerOuter = tickPositioner.map(js.Any.fromFunction1)
    val tickPositionsOuter = tickPositions.map(_.toJSArray)
    val tickWidthOuter = tickWidth
    val typeOuter = `type`.map(_.value)

    new ColorAxis {
      override val className = classNameOuter
      override val dataClassColor = dataClassColorOuter
      override val dataClasses = dataClassesOuter
      override val endOnTick = endOnTickOuter
      override val events = eventsOuter
      override val gridLineColor = gridLineColorOuter
      override val gridLineDashStyle = gridLineDashStyleOuter
      override val gridLineWidth = gridLineWidthOuter
      override val id = idOuter
      override val labels = labelsOuter
      override val lineColor = lineColorOuter
      override val lineWidth = lineWidthOuter
      override val marker = markerOuter
      override val max = maxOuter
      override val maxColor = maxColorOuter
      override val maxPadding = maxPaddingOuter
      override val min = minOuter
      override val minColor = minColorOuter
      override val minPadding = minPaddingOuter
      override val minorGridLineColor = minorGridLineColorOuter
      override val minorGridLineDashStyle = minorGridLineDashStyleOuter
      override val minorGridLineWidth = minorGridLineWidthOuter
      override val minorTickColor = minorTickColorOuter
      override val minorTickInterval = minorTickIntervalOuter
      override val minorTickLength = minorTickLengthOuter
      override val minorTickPosition = minorTickPositionOuter
      override val minorTickWidth = minorTickWidthOuter
      override val reversed = reversedOuter
      override val showFirstLabel = showFirstLabelOuter
      override val showLastLabel = showLastLabelOuter
      override val startOnTick = startOnTickOuter
      override val stops = stopsOuter
      override val tickColor = tickColorOuter
      override val tickInterval = tickIntervalOuter
      override val tickLength = tickLengthOuter
      override val tickPixelInterval = tickPixelIntervalOuter
      override val tickPosition = tickPositionOuter
      override val tickPositioner = tickPositionerOuter
      override val tickPositions = tickPositionsOuter
      override val tickWidth = tickWidthOuter
      override val `type` = typeOuter
    }
  }
}