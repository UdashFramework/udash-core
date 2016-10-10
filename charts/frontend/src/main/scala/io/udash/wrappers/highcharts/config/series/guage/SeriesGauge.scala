/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.guage

import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.utils.{Animation, Color}

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
class SeriesGauge extends GaugeSeries {
  override type Data = js.Array[SeriesGaugeData | Double]
  val `type`: String = "gauge"

  /**
    * Options for the dial or arrow pointer of the gauge.
    */
  val dial: js.UndefOr[SeriesGaugeDial] = js.undefined

  /**
    * The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-negative-color/" target="_blank">Spline, area and column</a> - <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-negativecolor/" target="_blank">arearange</a>.
    */
  val negativeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Options for the pivot or the center point of the gauge.
    */
  val pivot: js.UndefOr[SeriesGaugePivot] = js.undefined
}

object SeriesGauge {
  import scala.scalajs.js.JSConverters._

  /**
    * @param animation           <p>Enable or disable the initial animation when a series is displayed. The animation can also be set as a configuration object. Please note that this option only applies to the initial animation of the series itself. For other animations, see <a href="#chart.animation">chart.animation</a> and the animation parameter under the API methods.		The following properties are supported:</p>. <dl>.   <dt>duration</dt>.   <dd>The duration of the animation in milliseconds.</dd>. <dt>easing</dt>. <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>. </dl>. <p>. Due to poor performance, animation is disabled in old IE browsers for column charts and polar charts.</p>
    * @param animationLimit      For some series, there is a limit that shuts down initial animation by default when the total number of points in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    * @param className           A class name to apply to the series' graphical elements.
    * @param color               The main color or the series. In line type series it applies to the line and the point markers unless otherwise specified. In bar type series it applies to the bars unless a color is specified per point. The default value is pulled from the  <code>options.colors</code> array.
    * @param cursor              You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that the points and lines can be clicked.
    * @param data                An array of data points for the series. For the <code>gauge</code> series type, points can be given in the following ways:.  <ol>.  	<li>An array of numerical values. In this case, the numerical values will .  	be interpreted as <code>y</code> options.  Example:. <pre>data: [0, 5, 3, 5]</pre>.  	</li>.  <li><p>An array of objects with named values. The objects are.  	point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<gauge>.turboThreshold'>turboThreshold</a>, this option is not available.</p>. . <pre>data: [{.     y: 6,.     name: "Point2",.     color: "#00FF00". }, {.     y: 8,.     name: "Point1",.     color: "#FF00FF". }]</pre></li>.  </ol><p>The typical gauge only contains a single data value.</p>
    * @param dataLabels          Data labels for the gauge. For gauges, the data labels are enabled by default and shown in a bordered box below the point.
    * @param dial                Options for the dial or arrow pointer of the gauge.
    * @param enableMouseTracking Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points. For large datasets it improves performance.
    * @param getExtremesFromAll  Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis. By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    * @param id                  An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    * @param index               The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array, the visible Z index as well as the order in the legend.
    * @param keys                An array specifying which option maps to which key in the data point array. This makes it convenient to work with unstructured data arrays from different sources.
    * @param legendIndex         The sequential index of the series in the legend.  <div class="demo">Try it:  	<a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    * @param linkedTo            The <a href="#series.id">id</a> of another series to link to. Additionally, the value can be ":previous" to link to the previous series. When two series are linked, only the first one appears in the legend. Toggling the visibility of this also toggles the linked series.
    * @param name                The name of the series as shown in the legend, tooltip etc.
    * @param negativeColor       The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    * @param overshoot           Allow the dial to overshoot the end of the perimeter axis by this many degrees. Say if the gauge axis goes from 0 to 60, a value of 100, or 1000, will show 5 degrees beyond the end of the axis.
    * @param pivot               Options for the pivot or the center point of the gauge.
    * @param point               Properties for each single point
    * @param selected            Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    * @param showCheckbox        If true, a checkbox is displayed next to the legend item to allow selecting the series. The state of the checkbox is determined by the <code>selected</code> option.
    * @param showInLegend        Whether to display this particular series or series type in the legend. Defaults to false for gauge series.
    * @param stickyTracking      Sticky tracking of mouse events. When true, the <code>mouseOut</code> event.  on a series isn't triggered until the mouse moves over another series, or out.  of the plot area. When false, the <code>mouseOut</code> event on a series is.  triggered when the mouse leaves the area around the series' graph or markers..  This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the .  tooltip will be hidden when moving the mouse between series. Defaults to true for line and area type series, but to false for columns, pies etc.
    * @param threshold           The threshold, also called zero level or base level. For line type series this is only used in conjunction with <a href="#plotOptions.series.negativeColor">negativeColor</a>.
    * @param tooltip             A configuration object for the tooltip rendering of each single series. Properties are inherited from <a href="#tooltip">tooltip</a>, but only the following properties can be defined on a series level.
    * @param visible             Set the initial visibility of the series.
    * @param wrap                When this option is <code>true</code>, the dial will wrap around the axes. For instance, in a full-range gauge going from 0 to 360, a value of 400 will point to 40. When <code>wrap</code> is <code>false</code>, the dial stops at 360.
    * @param xAxis               When using dual or multiple x axes, this number defines which xAxis the particular series is connected to. It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    * @param yAxis               When using dual or multiple y axes, this number defines which yAxis the particular series is connected to. It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    * @param zIndex              Define the visual z index of the series.
    */
  def apply(animation: js.UndefOr[Animation] = js.undefined,
            animationLimit: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            cursor: js.UndefOr[String] = js.undefined,
            data: Seq[SeriesGaugeData | Double] = Seq.empty,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            dial: js.UndefOr[SeriesGaugeDial] = js.undefined,
            enableMouseTracking: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[SeriesEvents] = js.undefined,
            getExtremesFromAll: js.UndefOr[Boolean] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            index: js.UndefOr[Double] = js.undefined,
            keys: js.UndefOr[Seq[String]] = js.undefined,
            legendIndex: js.UndefOr[Double] = js.undefined,
            linkedTo: js.UndefOr[String] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            negativeColor: js.UndefOr[Color] = js.undefined,
            overshoot: js.UndefOr[Double] = js.undefined,
            pivot: js.UndefOr[SeriesGaugePivot] = js.undefined,
            point: js.UndefOr[SeriesPoint] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            showCheckbox: js.UndefOr[Boolean] = js.undefined,
            showInLegend: js.UndefOr[Boolean] = js.undefined,
            stickyTracking: js.UndefOr[Boolean] = js.undefined,
            threshold: js.UndefOr[Double] = js.undefined,
            tooltip: js.UndefOr[SeriesTooltip] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined,
            wrap: js.UndefOr[Boolean] = js.undefined,
            xAxis: js.UndefOr[Double | String] = js.undefined,
            yAxis: js.UndefOr[Double | String] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): SeriesGauge = {
    val animationOuter = animation.map(_.value)
    val animationLimitOuter = animationLimit
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val cursorOuter = cursor
    val dataOuter = data.toJSArray.asInstanceOf[js.UndefOr[SeriesGauge#Data]]
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val dialOuter = dial
    val enableMouseTrackingOuter = enableMouseTracking
    val eventsOuter = events
    val getExtremesFromAllOuter = getExtremesFromAll
    val idOuter = id
    val indexOuter = index
    val keysOuter = keys.map(_.toJSArray)
    val legendIndexOuter = legendIndex
    val linkedToOuter = linkedTo
    val nameOuter = name
    val negativeColorOuter = negativeColor.map(_.c)
    val overshootOuter = overshoot
    val pointOuter = point
    val pivotOuter = pivot
    val selectedOuter = selected
    val showCheckboxOuter = showCheckbox
    val showInLegendOuter = showInLegend
    val stickyTrackingOuter = stickyTracking
    val thresholdOuter = threshold
    val tooltipOuter = tooltip
    val visibleOuter = visible
    val wrapOuter = wrap
    val xAxisOuter = xAxis
    val yAxisOuter = yAxis
    val zIndexOuter = zIndex

    new SeriesGauge {
      override val animation = animationOuter
      override val animationLimit = animationLimitOuter
      override val color = colorOuter
      override val className = classNameOuter
      override val cursor = cursorOuter
      override val data = dataOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val dial = dialOuter
      override val enableMouseTracking = enableMouseTrackingOuter
      override val events = eventsOuter
      override val getExtremesFromAll = getExtremesFromAllOuter
      override val id = idOuter
      override val index = indexOuter
      override val keys = keysOuter
      override val legendIndex = legendIndexOuter
      override val linkedTo = linkedToOuter
      override val name = nameOuter
      override val negativeColor = negativeColorOuter
      override val overshoot = overshootOuter
      override val point = pointOuter
      override val pivot = pivotOuter
      override val selected = selectedOuter
      override val showCheckbox = showCheckboxOuter
      override val showInLegend = showInLegendOuter
      override val stickyTracking = stickyTrackingOuter
      override val threshold = thresholdOuter
      override val tooltip = tooltipOuter
      override val visible = visibleOuter
      override val wrap = wrapOuter
      override val xAxis = xAxisOuter
      override val yAxis = yAxisOuter
      override val zIndex = zIndexOuter
    }
  }
}
