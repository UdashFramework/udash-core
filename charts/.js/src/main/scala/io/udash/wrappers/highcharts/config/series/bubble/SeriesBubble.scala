/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.bubble

import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.`|`

trait SeriesBubble extends FreePointsSeries {
  override type Data = js.Array[SeriesBubbleData | js.Array[String | Double]]
  override type DataLabels = SeriesDataLabels
  override type States = SeriesAreaStates

  /**
    * Whether to display negative sized bubbles. The threshold is given by the <a href="#plotOptions.bubble.zThreshold">zThreshold</a> option,
    * and negative bubbles can be visualized by setting <a href="#plotOptions.bubble.negativeColor">negativeColor</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-negative/" target="_blank">Negative bubbles</a>
    */
  val displayNegative: js.UndefOr[Boolean] = js.undefined

  /**
    * Maximum bubble size. Bubbles will automatically size between the <code>minSize</code> and <code>maxSize</code>
    * to reflect the <code>z</code> value of each bubble. Can be either pixels (when no unit is given),
    * or a percentage of the smallest one of the plot width and height.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-size/" target="_blank">Bubble size</a>
    */
  val maxSize: js.UndefOr[String] = js.undefined

  /**
    * Minimum bubble size. Bubbles will automatically size between the <code>minSize</code> and <code>maxSize</code>
    * to reflect the <code>z</code> value of each bubble. Can be either pixels (when no unit is given),
    * or a percentage of the smallest one of the plot width and height.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-size/" target="_blank">Bubble size</a>
    */
  val minSize: js.UndefOr[String] = js.undefined

  /**
    * Whether the bubble's value should be represented by the area or the width of the bubble. The default, <code>area</code>,
    * corresponds best to the human perception of the size of each bubble.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-sizeby/" target="_blank">Comparison of area and size</a>
    */
  val sizeBy: js.UndefOr[String] = js.undefined

  /**
    * When this is true, the absolute value of z determines the size of the bubble.
    * This means that with the default <code>zThreshold</code> of 0, a bubble of value -1 will have the same size as
    * a bubble of value 1, while a bubble of value 0 will have a smaller size according to <code>minSize</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-sizebyabsolutevalue/" target="_blank">Size by absolute value, various thresholds</a>.
    */
  val sizeByAbsoluteValue: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>When this is true, the series will not cause the Y axis to cross the zero plane
    * (or <a href="#plotOptions.series.threshold">threshold</a> option) unless the data actually crosses the plane.</p>
    *
    * <p>For example, if <code>softThreshold</code> is <code>false</code>, a series of 0, 1, 2, 3 will make the Y axis show negative values according to the <code>minPadding</code> option. If <code>softThreshold</code> is <code>true</code>, the Y axis starts at 0.</p>
    */
  val softThreshold: js.UndefOr[Boolean] = js.undefined

  /**
    * The threshold, also called zero level or base level. For line type series this is only used in conjunction
    * with <a href="#plotOptions.series.negativeColor">negativeColor</a>.
    */
  val threshold: js.UndefOr[Double] = js.undefined

  /**
    * The minimum for the Z value range. Defaults to the highest Z value in the data.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-zmin-zmax/" target="_blank">Z has a possible range of 0-100</a>
    */
  val zMax: js.UndefOr[Double] = js.undefined

  /**
    * The minimum for the Z value range. Defaults to the lowest Z value in the data.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-zmin-zmax/" target="_blank">Z has a possible range of 0-100</a>
    */
  val zMin: js.UndefOr[Double] = js.undefined

  /**
    * When <a href="#plotOptions.bubble.displayNegative">displayNegative</a> is <code>false</code>, bubbles with lower Z values
    * are skipped. When <code>displayNegative</code> is <code>true</code> and a <a href="#plotOptions.bubble.negativeColor">negativeColor</a>
    * is given, points with lower Z is colored.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-negative/" target="_blank">Negative bubbles</a>
    */
  val zThreshold: js.UndefOr[Double] = js.undefined
}

object SeriesBubble {
  import scala.scalajs.js.JSConverters._

  /**
    * @param allowPointSelect    Allow this series' points to be selected by clicking on the markers, bars or pie slices.
    * @param animation           <p>Enable or disable the initial animation when a series is displayed. The animation can also be set as a configuration object. Please note that this option only applies to the initial animation of the series itself. For other animations, see <a href="#chart.animation">chart.animation</a> and the animation parameter under the API methods.		The following properties are supported:</p>. <dl>.   <dt>duration</dt>.   <dd>The duration of the animation in milliseconds.</dd>. <dt>easing</dt>. <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>. </dl>. <p>. Due to poor performance, animation is disabled in old IE browsers for column charts and polar charts.</p>
    * @param animationLimit      For some series, there is a limit that shuts down initial animation by default when the total number of points in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    * @param className           A class name to apply to the series' graphical elements.
    * @param color               The main color or the series. In line type series it applies to the line and the point markers unless otherwise specified. In bar type series it applies to the bars unless a color is specified per point. The default value is pulled from the  <code>options.colors</code> array.
    * @param cropThreshold       When the series contains less points than the crop threshold, all points are drawn,  even if the points fall outside the visible plot area at the current zoom. The advantage of drawing all points (including markers and columns), is that animation is performed on updates. On the other hand, when the series contains more points than the crop threshold, the series data is cropped to only contain points that fall within the plot area. The advantage of cropping away invisible points is to increase performance on large series.
    * @param cursor              You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that the points and lines can be clicked.
    * @param dashStyle           A name for the dash style to use for the graph. Applies only to series type having a graph, like <code>line</code>, <code>spline</code>, <code>area</code> and <code>scatter</code> in  case it has a <code>lineWidth</code>. The value for the <code>dashStyle</code> include:. 		    <ul>. 		    	<li>Solid</li>. 		    	<li>ShortDash</li>. 		    	<li>ShortDot</li>. 		    	<li>ShortDashDot</li>. 		    	<li>ShortDashDotDot</li>. 		    	<li>Dot</li>. 		    	<li>Dash</li>. 		    	<li>LongDash</li>. 		    	<li>DashDot</li>. 		    	<li>LongDashDot</li>. 		    	<li>LongDashDotDot</li>. 		    </ul>
    * @param data                An array of data points for the series. For the <code>bubble</code> series type, points can be given in the following ways:.  <ol>.  	<li><p>An array of arrays with 3 or 2 values. In this case, the values correspond to <code>x,y,z</code>. If the first value is a string, it is.  	applied as the name of the point, and the <code>x</code> value is inferred. The <code>x</code> value can also be omitted, in which case the inner arrays should be of length 2. Then the <code>x</code> value is automatically calculated, either starting at 0 and incremented by 1, or from <code>pointStart</code> .  	and <code>pointInterval</code> given in the series options.</p>. <pre>data: [.     [0, 1, 2], .     [1, 5, 5], .     [2, 0, 2]. ]</pre></li>. . . <li><p>An array of objects with named values. The objects are.  	point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<bubble>.turboThreshold'>turboThreshold</a>, this option is not available.</p>. . <pre>data: [{.     x: 1,.     y: 1,.     z: 1,.     name: "Point2",.     color: "#00FF00". }, {.     x: 1,.     y: 5,.     z: 4,.     name: "Point1",.     color: "#FF00FF". }]</pre></li>.  </ol>
    * @param displayNegative     Whether to display negative sized bubbles. The threshold is given by the <a href="#plotOptions.bubble.zThreshold">zThreshold</a> option, and negative bubbles can be visualized by setting <a href="#plotOptions.bubble.negativeColor">negativeColor</a>.
    * @param enableMouseTracking Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points. For large datasets it improves performance.
    * @param getExtremesFromAll  Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis. By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    * @param id                  An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    * @param index               The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array, the visible Z index as well as the order in the legend.
    * @param keys                An array specifying which option maps to which key in the data point array. This makes it convenient to work with unstructured data arrays from different sources.
    * @param legendIndex         The sequential index of the series in the legend.  <div class="demo">Try it:  	<a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    * @param lineWidth           The width of the line connecting the data points.
    * @param linkedTo            The <a href="#series.id">id</a> of another series to link to. Additionally, the value can be ":previous" to link to the previous series. When two series are linked, only the first one appears in the legend. Toggling the visibility of this also toggles the linked series.
    * @param maxSize             Maximum bubble size. Bubbles will automatically size between the <code>minSize</code> and <code>maxSize</code> to reflect the <code>z</code> value of each bubble. Can be either pixels (when no unit is given), or a percentage of the smallest one of the plot width and height.
    * @param minSize             Minimum bubble size. Bubbles will automatically size between the <code>minSize</code> and <code>maxSize</code> to reflect the <code>z</code> value of each bubble. Can be either pixels (when no unit is given), or a percentage of the smallest one of the plot width and height.
    * @param name                The name of the series as shown in the legend, tooltip etc.
    * @param negativeColor       When a point's Z value is below the <a href="#plotOptions.bubble.zThreshold">zThreshold</a> setting, this color is used.
    * @param point               Properties for each single point
    * @param pointInterval       <p>If no x values are given for the points in a series, pointInterval defines.  the interval of the x values. For example, if a series contains one value.  every decade starting from year 0, set pointInterval to 10.</p>. <p>Since Highcharts 4.1, it can be combined with <code>pointIntervalUnit</code> to draw irregular intervals.</p>
    * @param pointIntervalUnit   On datetime series, this allows for setting the <a href="plotOptions.series.pointInterval">pointInterval</a> to irregular time units, <code>day</code>, <code>month</code> and <code>year</code>. A day is usually the same as 24 hours, but pointIntervalUnit also takes the DST crossover into consideration when dealing with local time. Combine this option with <code>pointInterval</code> to draw weeks, quarters, 6 months, 10 years etc.
    * @param pointStart          If no x values are given for the points in a series, pointStart defines on what value to start. For example, if a series contains one yearly value starting from 1945, set pointStart to 1945.
    * @param selected            Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    * @param shadow              Whether to apply a drop shadow to the graph line. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param showCheckbox        If true, a checkbox is displayed next to the legend item to allow selecting the series. The state of the checkbox is determined by the <code>selected</code> option.
    * @param showInLegend        Whether to display this particular series or series type in the legend. The default value is <code>true</code> for standalone series, <code>false</code> for linked series.
    * @param sizeBy              Whether the bubble's value should be represented by the area or the width of the bubble. The default, <code>area</code>, corresponds best to the human perception of the size of each bubble.
    * @param sizeByAbsoluteValue When this is true, the absolute value of z determines the size of the bubble. This means that with the default <code>zThreshold</code> of 0, a bubble of value -1 will have the same size as a bubble of value 1, while a bubble of value 0 will have a smaller size according to <code>minSize</code>.
    * @param softThreshold       <p>When this is true, the series will not cause the Y axis to cross the zero plane (or <a href="#plotOptions.series.threshold">threshold</a> option) unless the data actually crosses the plane.</p>. . <p>For example, if <code>softThreshold</code> is <code>false</code>, a series of 0, 1, 2, 3 will make the Y axis show negative values according to the <code>minPadding</code> option. If <code>softThreshold</code> is <code>true</code>, the Y axis starts at 0.</p>
    * @param states              A wrapper object for all the series options in specific states.
    * @param stickyTracking      Sticky tracking of mouse events. When true, the <code>mouseOut</code> event.  on a series isn't triggered until the mouse moves over another series, or out.  of the plot area. When false, the <code>mouseOut</code> event on a series is.  triggered when the mouse leaves the area around the series' graph or markers..  This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the .  tooltip will be hidden when moving the mouse between series.
    * @param threshold           The threshold, also called zero level or base level. For line type series this is only used in conjunction with <a href="#plotOptions.series.negativeColor">negativeColor</a>.
    * @param tooltip             A configuration object for the tooltip rendering of each single series. Properties are inherited from <a href="#tooltip">tooltip</a>, but only the following properties can be defined on a series level.
    * @param visible             Set the initial visibility of the series.
    * @param xAxis               When using dual or multiple x axes, this number defines which xAxis the particular series is connected to. It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    * @param yAxis               When using dual or multiple y axes, this number defines which yAxis the particular series is connected to. It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    * @param zIndex              Define the visual z index of the series.
    * @param zMax                The minimum for the Z value range. Defaults to the highest Z value in the data.
    * @param zMin                The minimum for the Z value range. Defaults to the lowest Z value in the data.
    * @param zThreshold          When <a href="#plotOptions.bubble.displayNegative">displayNegative</a> is <code>false</code>, bubbles with lower Z values are skipped. When <code>displayNegative</code> is <code>true</code> and a <a href="#plotOptions.bubble.negativeColor">negativeColor</a> is given, points with lower Z is colored.
    * @param zoneAxis            Defines the Axis on which the zones are applied.
    * @param zones               An array defining zones within a series. Zones can be applied to the X axis, Y axis or Z axis for bubbles, according to the <code>zoneAxis</code> option.
    */
  def apply(allowPointSelect: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            animationLimit: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            cropThreshold: js.UndefOr[Double] = js.undefined,
            cursor: js.UndefOr[String] = js.undefined,
            dashStyle: js.UndefOr[DashStyle] = js.undefined,
            data: Seq[js.Array[Double] | SeriesBubbleData] = Seq.empty,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            displayNegative: js.UndefOr[Boolean] = js.undefined,
            enableMouseTracking: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[SeriesEvents] = js.undefined,
            getExtremesFromAll: js.UndefOr[Boolean] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            index: js.UndefOr[Double] = js.undefined,
            keys: js.UndefOr[Seq[String]] = js.undefined,
            legendIndex: js.UndefOr[Double] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            linkedTo: js.UndefOr[String] = js.undefined,
            marker: js.UndefOr[SeriesMarker] = js.undefined,
            maxSize: js.UndefOr[String] = js.undefined,
            minSize: js.UndefOr[String] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            negativeColor: js.UndefOr[Color] = js.undefined,
            point: js.UndefOr[SeriesPoint] = js.undefined,
            pointInterval: js.UndefOr[Double] = js.undefined,
            pointIntervalUnit: js.UndefOr[PointIntervalUnit] = js.undefined,
            pointStart: js.UndefOr[Double] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            showCheckbox: js.UndefOr[Boolean] = js.undefined,
            showInLegend: js.UndefOr[Boolean] = js.undefined,
            sizeBy: js.UndefOr[String] = js.undefined,
            sizeByAbsoluteValue: js.UndefOr[Boolean] = js.undefined,
            softThreshold: js.UndefOr[Boolean] = js.undefined,
            states: js.UndefOr[SeriesAreaStates] = js.undefined,
            stickyTracking: js.UndefOr[Boolean] = js.undefined,
            threshold: js.UndefOr[Double] = js.undefined,
            tooltip: js.UndefOr[SeriesTooltip] = js.undefined,
            turboThreshold: js.UndefOr[Double] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined,
            xAxis: js.UndefOr[Int | String] = js.undefined,
            yAxis: js.UndefOr[Int | String] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined,
            zMax: js.UndefOr[Double] = js.undefined,
            zMin: js.UndefOr[Double] = js.undefined,
            zThreshold: js.UndefOr[Double] = js.undefined,
            zoneAxis: js.UndefOr[String] = js.undefined,
            zones: js.UndefOr[Seq[SeriesZone]] = js.undefined): SeriesBubble = {
    val allowPointSelectOuter = allowPointSelect
    val animationOuter = animation.map(_.value)
    val animationLimitOuter = animationLimit
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val cropThresholdOuter = cropThreshold
    val cursorOuter = cursor
    val dashStyleOuter = dashStyle.map(_.name)
    val dataOuter = data.toJSArray.asInstanceOf[js.UndefOr[SeriesBubble#Data]]
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val displayNegativeOuter = displayNegative
    val enableMouseTrackingOuter = enableMouseTracking
    val eventsOuter = events
    val getExtremesFromAllOuter = getExtremesFromAll
    val idOuter = id
    val indexOuter = index
    val keysOuter = keys.map(_.toJSArray)
    val legendIndexOuter = legendIndex
    val lineWidthOuter = lineWidth
    val linkedToOuter = linkedTo
    val markerOuter = marker
    val maxSizeOuter = maxSize
    val minSizeOuter = minSize
    val nameOuter = name
    val negativeColorOuter = negativeColor.map(_.c)
    val pointOuter = point
    val pointIntervalOuter = pointInterval
    val pointIntervalUnitOuter = pointIntervalUnit.map(_.name)
    val pointStartOuter = pointStart
    val selectedOuter = selected
    val shadowOuter = shadow.map(_.value)
    val showCheckboxOuter = showCheckbox
    val showInLegendOuter = showInLegend
    val sizeByOuter = sizeBy
    val sizeByAbsoluteValueOuter = sizeByAbsoluteValue
    val softThresholdOuter = softThreshold
    val statesOuter = states
    val stickyTrackingOuter = stickyTracking
    val thresholdOuter = threshold
    val tooltipOuter = tooltip
    val turboThresholdOuter = turboThreshold
    val visibleOuter = visible
    val xAxisOuter = xAxis
    val yAxisOuter = yAxis
    val zIndexOuter = zIndex
    val zMaxOuter = zMax
    val zMinOuter = zMin
    val zThresholdOuter = zThreshold
    val zoneAxisOuter = zoneAxis
    val zonesOuter = zones.map(_.toJSArray)

    new SeriesBubble {
      override val `type`: String = "bubble"
      override val allowPointSelect = allowPointSelectOuter
      override val animation = animationOuter
      override val animationLimit = animationLimitOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val cropThreshold = cropThresholdOuter
      override val cursor = cursorOuter
      override val dashStyle = dashStyleOuter
      override val data = dataOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val displayNegative = displayNegativeOuter
      override val enableMouseTracking = enableMouseTrackingOuter
      override val events = eventsOuter
      override val getExtremesFromAll = getExtremesFromAllOuter
      override val id = idOuter
      override val index = indexOuter
      override val keys = keysOuter
      override val legendIndex = legendIndexOuter
      override val lineWidth = lineWidthOuter
      override val linkedTo = linkedToOuter
      override val marker = markerOuter
      override val maxSize = maxSizeOuter
      override val minSize = minSizeOuter
      override val name = nameOuter
      override val negativeColor = negativeColorOuter
      override val point = pointOuter
      override val pointInterval = pointIntervalOuter
      override val pointIntervalUnit = pointIntervalUnitOuter
      override val pointStart = pointStartOuter
      override val selected = selectedOuter
      override val shadow = shadowOuter
      override val showCheckbox = showCheckboxOuter
      override val showInLegend = showInLegendOuter
      override val sizeBy = sizeByOuter
      override val sizeByAbsoluteValue = sizeByAbsoluteValueOuter
      override val softThreshold = softThresholdOuter
      override val states = statesOuter
      override val stickyTracking = stickyTrackingOuter
      override val threshold = thresholdOuter
      override val tooltip = tooltipOuter
      override val turboThreshold = turboThresholdOuter
      override val visible = visibleOuter
      override val xAxis = xAxisOuter
      override val yAxis = yAxisOuter
      override val zIndex = zIndexOuter
      override val zMax = zMaxOuter
      override val zMin = zMinOuter
      override val zThreshold = zThresholdOuter
      override val zoneAxis = zoneAxisOuter
      override val zones = zonesOuter
    }
  }
}
