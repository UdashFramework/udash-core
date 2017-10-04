/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.errorbar

import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
trait SeriesErrorbar extends CandleLikeSeries {
  override type Data = js.Array[SeriesErrorbarData | js.Array[String | Double]]
  override type DataLabels = BaseSeriesDataLabels
  override type States = SeriesBarStates
}

object SeriesErrorbar {
  import scala.scalajs.js.JSConverters._

  /**
    * @param allowPointSelect    Allow this series' points to be selected by clicking on the markers, bars or pie slices.
    * @param animationLimit      For some series, there is a limit that shuts down initial animation by default when the total number of points in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    * @param className           A class name to apply to the series' graphical elements.
    * @param color               The main color of the bars. This can be overridden by <a href="#plotOptions.errorbar.stemColor">stemColor</a> and <a href="#plotOptions.errorbar.whiskerColor">whiskerColor</a> individually.
    * @param colorByPoint        When using automatic point colors pulled from the <code>options.colors</code>.  collection, this option determines whether the chart should receive .  one color per series or one color per point.
    * @param colors              A series specific or series type specific color set to apply instead of the global <a href="#colors">colors</a> when <a href="#plotOptions.column.colorByPoint">colorByPoint</a> is true.
    * @param crisp               When true, each column edge is rounded to its nearest pixel in order to render sharp on screen. In some cases, when there are a lot of densely packed columns, this leads to visible difference in column widths or distance between columns. In these cases, setting crisp to false may look better, even though each column is rendered blurry. Defaults to true.
    * @param cursor              You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that the points and lines can be clicked.
    * @param data                An array of data points for the series. For the <code>errorbar</code> series type, points can be given in the following ways:.  <ol>.  	<li><p>An array of arrays with 3 or 2 values. In this case, the values correspond to <code>x,low,high</code>. If the first value is a string, it is.  	applied as the name of the point, and the <code>x</code> value is inferred. The <code>x</code> value can also be omitted, in which case the inner arrays should be of length 2. Then the <code>x</code> value is automatically calculated, either starting at 0 and incremented by 1, or from <code>pointStart</code> .  	and <code>pointInterval</code> given in the series options.</p>. <pre>data: [.     [0, 10, 2], .     [1, 1, 8], .     [2, 4, 5]. ]</pre></li>. . . <li><p>An array of objects with named values. The objects are.  	point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<errorbar>.turboThreshold'>turboThreshold</a>, this option is not available.</p>. . <pre>data: [{.     x: 1,.     low: 0,.     high: 0,.     name: "Point2",.     color: "#00FF00". }, {.     x: 1,.     low: 5,.     high: 5,.     name: "Point1",.     color: "#FF00FF". }]</pre></li>.  </ol>
    * @param depth               Depth of the columns in a 3D column chart. Requires <code>highcharts-3d.js</code>.
    * @param edgeColor           3D columns only. The color of the edges. Similar to <code>borderColor</code>, except it defaults to the same color as the column.
    * @param edgeWidth           3D columns only. The width of the colored edges.
    * @param enableMouseTracking Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points. For large datasets it improves performance.
    * @param getExtremesFromAll  Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis. By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    * @param groupZPadding       The spacing between columns on the Z Axis in a 3D chart. Requires <code>highcharts-3d.js</code>.
    * @param id                  An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    * @param index               The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array, the visible Z index as well as the order in the legend.
    * @param keys                An array specifying which option maps to which key in the data point array. This makes it convenient to work with unstructured data arrays from different sources.
    * @param legendIndex         The sequential index of the series in the legend.  <div class="demo">Try it:  	<a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    * @param lineWidth           The width of the line surrounding the box. If any of <a href="#plotOptions.boxplot.stemWidth">stemWidth</a>, <a href="#plotOptions.boxplot.medianWidth">medianWidth</a> or <a href="#plotOptions.boxplot.whiskerWidth">whiskerWidth</a> are <code>null</code>, the lineWidth also applies to these lines.
    * @param linkedTo            The parent series of the error bar. The default value links it to the previous series. Otherwise, use the id of the parent series.
    * @param maxPointWidth       The maximum allowed pixel width for a column, translated to the height of a bar in a bar chart. This prevents the columns from becoming too wide when there is a small number of points in the chart.
    * @param name                The name of the series as shown in the legend, tooltip etc.
    * @param negativeColor       The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    * @param point               Properties for each single point
    * @param pointInterval       <p>If no x values are given for the points in a series, pointInterval defines.  the interval of the x values. For example, if a series contains one value.  every decade starting from year 0, set pointInterval to 10.</p>. <p>Since Highcharts 4.1, it can be combined with <code>pointIntervalUnit</code> to draw irregular intervals.</p>
    * @param pointIntervalUnit   On datetime series, this allows for setting the <a href="plotOptions.series.pointInterval">pointInterval</a> to irregular time units, <code>day</code>, <code>month</code> and <code>year</code>. A day is usually the same as 24 hours, but pointIntervalUnit also takes the DST crossover into consideration when dealing with local time. Combine this option with <code>pointInterval</code> to draw weeks, quarters, 6 months, 10 years etc.
    * @param pointPadding        Padding between each column or bar, in x axis units.
    * @param pointPlacement      <p>Possible values: <code>null</code>, <code>"on"</code>, <code>"between"</code>.</p>. <p>In a column chart, when pointPlacement is <code>"on"</code>, the point will not create any padding of the X axis. In a polar column chart this means that the first column points directly north. If the pointPlacement is <code>"between"</code>, the columns will be laid out between ticks. This is useful for example for visualising an amount between two points in time or in a certain sector of a polar chart.</p>. <p>Since Highcharts 3.0.2, the point placement can also be numeric, where 0 is on the axis value, -0.5 is between this value and the previous, and 0.5 is between this value and the next. Unlike the textual options, numeric point placement options won't affect axis padding.</p>. <p>Note that pointPlacement needs a <a href="#plotOptions.series.pointRange">pointRange</a> to work. For column series this is computed, but for line-type series it needs to be set.</p>. <p>Defaults to <code>null</code> in cartesian charts, <code>"between"</code> in polar charts.
    * @param pointRange          The X axis range that each point is valid for. This determines the width of the column. On a categorized axis, the range will be 1 by default (one category unit). On linear and datetime axes, the range will be computed as the distance between the two closest data points.
    * @param pointStart          If no x values are given for the points in a series, pointStart defines on what value to start. For example, if a series contains one yearly value starting from 1945, set pointStart to 1945.
    * @param pointWidth          A pixel value specifying a fixed width for each column or bar. When <code>null</code>, the width is calculated from the <code>pointPadding</code> and <code>groupPadding</code>.
    * @param selected            Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    * @param states              A wrapper object for all the series options in specific states.
    * @param stemColor           The color of the stem, the vertical line extending from the box to the whiskers. If <code>null</code>, the series color is used.
    * @param stemDashStyle       The dash style of the stem, the vertical line extending from the box to the whiskers.
    * @param stemWidth           The width of the stem, the vertical line extending from the box to the whiskers. If <code>null</code>, the width is inherited from the <a href="#plotOptions.boxplot.lineWidth">lineWidth</a> option.
    * @param stickyTracking      Sticky tracking of mouse events. When true, the <code>mouseOut</code> event.  on a series isn't triggered until the mouse moves over another series, or out.  of the plot area. When false, the <code>mouseOut</code> event on a series is.  triggered when the mouse leaves the area around the series' graph or markers..  This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the .  tooltip will be hidden when moving the mouse between series. Defaults to true for line and area type series, but to false for columns, pies etc.
    * @param tooltip             A configuration object for the tooltip rendering of each single series. Properties are inherited from <a href="#tooltip">tooltip</a>, but only the following properties can be defined on a series level.
    * @param turboThreshold      When a series contains a data array that is longer than this, only one dimensional arrays of numbers,.  or two dimensional arrays with x and y values are allowed. Also, only the first.  point is tested, and the rest are assumed to be the same format. This saves expensive.  data checking and indexing in long series. Set it to <code>0</code> disable.
    * @param visible             Set the initial visibility of the series.
    * @param whiskerColor        The color of the whiskers, the horizontal lines marking low and high values. When <code>null</code>, the general series color is used.
    * @param whiskerLength       The length of the whiskers, the horizontal lines marking low and high values. It can be a numerical pixel value, or a percentage value of the box width. Set <code>0</code> to disable whiskers.
    * @param whiskerWidth        The line width of the whiskers, the horizontal lines marking low and high values. When <code>null</code>, the general <a href="#plotOptions.errorbar.lineWidth">lineWidth</a> applies.
    * @param xAxis               When using dual or multiple x axes, this number defines which xAxis the particular series is connected to. It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    * @param yAxis               When using dual or multiple y axes, this number defines which yAxis the particular series is connected to. It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    * @param zIndex              Define the visual z index of the series.
    * @param zoneAxis            Defines the Axis on which the zones are applied.
    * @param zones               An array defining zones within a series. Zones can be applied to the X axis, Y axis or Z axis for bubbles, according to the <code>zoneAxis</code> option.
    */
  def apply(allowPointSelect: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            animationLimit: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            colorByPoint: js.UndefOr[Boolean] = js.undefined,
            colors: js.UndefOr[Seq[Color]] = js.undefined,
            crisp: js.UndefOr[Boolean] = js.undefined,
            cursor: js.UndefOr[String] = js.undefined,
            data: Seq[SeriesErrorbarData] = Seq.empty,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            depth: js.UndefOr[Double] = js.undefined,
            edgeColor: js.UndefOr[Color] = js.undefined,
            edgeWidth: js.UndefOr[Double] = js.undefined,
            enableMouseTracking: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[SeriesEvents] = js.undefined,
            getExtremesFromAll: js.UndefOr[Boolean] = js.undefined,
            groupPadding: js.UndefOr[Double] = js.undefined,
            groupZPadding: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            index: js.UndefOr[Double] = js.undefined,
            keys: js.UndefOr[Seq[String]] = js.undefined,
            legendIndex: js.UndefOr[Double] = js.undefined,
            lineWidth: js.UndefOr[Double] = js.undefined,
            linkedTo: js.UndefOr[String] = js.undefined,
            maxPointWidth: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            negativeColor: js.UndefOr[Color] = js.undefined,
            point: js.UndefOr[SeriesPoint] = js.undefined,
            pointInterval: js.UndefOr[Double] = js.undefined,
            pointIntervalUnit: js.UndefOr[PointIntervalUnit] = js.undefined,
            pointPadding: js.UndefOr[Double] = js.undefined,
            pointPlacement: js.UndefOr[PointPlacement] = js.undefined,
            pointRange: js.UndefOr[Double] = js.undefined,
            pointStart: js.UndefOr[Double] = js.undefined,
            pointWidth: js.UndefOr[Double] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            showCheckbox: js.UndefOr[Boolean] = js.undefined,
            showInLegend: js.UndefOr[Boolean] = js.undefined,
            states: js.UndefOr[SeriesBarStates] = js.undefined,
            stemColor: js.UndefOr[Color] = js.undefined,
            stemDashStyle: js.UndefOr[DashStyle] = js.undefined,
            stemWidth: js.UndefOr[Double] = js.undefined,
            stickyTracking: js.UndefOr[Boolean] = js.undefined,
            tooltip: js.UndefOr[SeriesTooltip] = js.undefined,
            turboThreshold: js.UndefOr[Double] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined,
            whiskerColor: js.UndefOr[Color] = js.undefined,
            whiskerLength: js.UndefOr[Double | String] = js.undefined,
            whiskerWidth: js.UndefOr[Double] = js.undefined,
            xAxis: js.UndefOr[Int | String] = js.undefined,
            yAxis: js.UndefOr[Int | String] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined,
            zoneAxis: js.UndefOr[String] = js.undefined,
            zones: js.UndefOr[Seq[SeriesZone]] = js.undefined): SeriesErrorbar = {
    val allowPointSelectOuter = allowPointSelect
    val animationOuter = animation.map(_.value)
    val animationLimitOuter = animationLimit
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val colorByPointOuter = colorByPoint
    val colorsOuter = colors.map(_.map(_.c).toJSArray)
    val crispOuter = crisp
    val cursorOuter = cursor
    val dataOuter = data.toJSArray.asInstanceOf[js.UndefOr[SeriesErrorbar#Data]]
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val depthOuter = depth
    val edgeColorOuter = edgeColor.map(_.c)
    val edgeWidthOuter = edgeWidth
    val enableMouseTrackingOuter = enableMouseTracking
    val eventsOuter = events
    val getExtremesFromAllOuter = getExtremesFromAll
    val groupPaddingOuter = groupPadding
    val groupZPaddingOuter = groupZPadding
    val idOuter = id
    val indexOuter = index
    val keysOuter = keys.map(_.toJSArray)
    val legendIndexOuter = legendIndex
    val lineWidthOuter = lineWidth
    val linkedToOuter = linkedTo
    val maxPointWidthOuter = maxPointWidth
    val nameOuter = name
    val negativeColorOuter = negativeColor.map(_.c)
    val pointOuter = point
    val pointIntervalOuter = pointInterval
    val pointIntervalUnitOuter = pointIntervalUnit.map(_.name)
    val pointPaddingOuter = pointPadding
    val pointPlacementOuter = pointPlacement.map(_.name)
    val pointRangeOuter = pointRange
    val pointStartOuter = pointStart
    val pointWidthOuter = pointWidth
    val selectedOuter = selected
    val shadowOuter = shadow.map(_.value)
    val showCheckboxOuter = showCheckbox
    val showInLegendOuter = showInLegend
    val statesOuter = states
    val stemColorOuter = stemColor.map(_.c)
    val stemDashStyleOuter = stemDashStyle.map(_.name)
    val stemWidthOuter = stemWidth
    val stickyTrackingOuter = stickyTracking
    val tooltipOuter = tooltip
    val turboThresholdOuter = turboThreshold
    val visibleOuter = visible
    val whiskerColorOuter = whiskerColor.map(_.c)
    val whiskerLengthOuter = whiskerLength
    val whiskerWidthOuter = whiskerWidth
    val xAxisOuter = xAxis
    val yAxisOuter = yAxis
    val zIndexOuter = zIndex
    val zoneAxisOuter = zoneAxis
    val zonesOuter = zones.map(_.toJSArray)

    new SeriesErrorbar {
      override val `type`: String = "errorbar"
      override val allowPointSelect = allowPointSelectOuter
      override val animation = animationOuter
      override val animationLimit = animationLimitOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val colorByPoint = colorByPointOuter
      override val colors = colorsOuter
      override val crisp = crispOuter
      override val cursor = cursorOuter
      override val data = dataOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val depth = depthOuter
      override val edgeColor = edgeColorOuter
      override val edgeWidth = edgeWidthOuter
      override val enableMouseTracking = enableMouseTrackingOuter
      override val events = eventsOuter
      override val getExtremesFromAll = getExtremesFromAllOuter
      override val groupPadding = groupPaddingOuter
      override val groupZPadding = groupZPaddingOuter
      override val id = idOuter
      override val index = indexOuter
      override val keys = keysOuter
      override val legendIndex = legendIndexOuter
      override val lineWidth = lineWidthOuter
      override val linkedTo = linkedToOuter
      override val maxPointWidth = maxPointWidthOuter
      override val name = nameOuter
      override val negativeColor = negativeColorOuter
      override val point = pointOuter
      override val pointInterval = pointIntervalOuter
      override val pointIntervalUnit = pointIntervalUnitOuter
      override val pointPadding = pointPaddingOuter
      override val pointPlacement = pointPlacementOuter
      override val pointRange = pointRangeOuter
      override val pointStart = pointStartOuter
      override val pointWidth = pointWidthOuter
      override val selected = selectedOuter
      override val shadow = shadowOuter
      override val showCheckbox = showCheckboxOuter
      override val showInLegend = showInLegendOuter
      override val states = statesOuter
      override val stemColor = stemColorOuter
      override val stemDashStyle = stemDashStyleOuter
      override val stemWidth = stemWidthOuter
      override val stickyTracking = stickyTrackingOuter
      override val tooltip = tooltipOuter
      override val turboThreshold = turboThresholdOuter
      override val visible = visibleOuter
      override val whiskerColor = whiskerColorOuter
      override val whiskerLength = whiskerLengthOuter
      override val whiskerWidth = whiskerWidthOuter
      override val xAxis = xAxisOuter
      override val yAxis = yAxisOuter
      override val zIndex = zIndexOuter
      override val zoneAxis = zoneAxisOuter
      override val zones = zonesOuter
    }
  }
}
