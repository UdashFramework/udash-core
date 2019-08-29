/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.pyramid

import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.utils.{Animation, Color, Shadow}

import scala.scalajs.js
import scala.scalajs.js.`|`

trait SeriesPyramid extends PieLikeSeries {
  override type Data = js.Array[SeriesPyramidData | Double]
  override type DataLabels = SeriesConnectorDataLabels
  override type States = SeriesAreaStates

  /**
    * The height of the funnel or pyramid. If it is a number it defines the pixel height, if it is a percentage string it is the percentage of the plot area height.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/funnel/" target="_blank">Funnel demo</a>
    */
  val height: js.UndefOr[Double | String] = js.undefined

  /**
    * The pyramid is reversed by default, as opposed to the funnel, which shares the layout engine, and is not reversed.
    */
  val reversed: js.UndefOr[Boolean] = js.undefined

  /**
    * The width of the funnel compared to the width of the plot area, or the pixel width if it is a number.
    */
  val width: js.UndefOr[Double | String] = js.undefined
}

object SeriesPyramid {
  import scala.scalajs.js.JSConverters._

  /**
    * @param allowPointSelect    Allow this series' points to be selected by clicking on the markers, bars or pie slices.
    * @param animationLimit      For some series, there is a limit that shuts down initial animation by default when the total number of points in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    * @param borderColor         The color of the border surrounding each slice. When <code>null</code>, the border takes the same color as the slice fill. This can be used together with a <code>borderWidth</code> to fill drawing gaps created by antialiazing artefacts in borderless pies.
    * @param borderWidth         <p>The width of the border surrounding each slice.</p>. . <p>When setting the border width to 0, there may be small gaps between the slices due to SVG antialiasing artefacts. To work around this, keep the border width at 0.5 or 1, but set the <code>borderColor</code> to <code>null</code> instead.</p>
    * @param center              The center of the series. By default, it is centered in the middle of the plot area, so it fills the plot area height.
    * @param className           A class name to apply to the series' graphical elements.
    * @param colors              A series specific or series type specific color set to use instead of the global <a href="#colors">colors</a>.
    * @param cursor              You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that the points and lines can be clicked.
    * @param data                An array of data points for the series. For the <code>pyramid</code> series type, points can be given in the following ways:.  <ol>.  	<li>An array of numerical values. In this case, the numerical values will .  	be interpreted as <code>y</code> options.  Example:. <pre>data: [0, 5, 3, 5]</pre>.  	</li>.  <li><p>An array of objects with named values. The objects are.  	point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<pyramid>.turboThreshold'>turboThreshold</a>, this option is not available.</p>. . <pre>data: [{.     y: 6,.     name: "Point2",.     color: "#00FF00". }, {.     y: 7,.     name: "Point1",.     color: "#FF00FF". }]</pre></li>.  </ol>
    * @param depth               The thickness of a 3D pie. Requires <code>highcharts-3d.js</code>
    * @param enableMouseTracking Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points. For large datasets it improves performance.
    * @param getExtremesFromAll  Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis. By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    * @param height              The height of the funnel or pyramid. If it is a number it defines the pixel height, if it is a percentage string it is the percentage of the plot area height.
    * @param id                  An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    * @param index               The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array, the visible Z index as well as the order in the legend.
    * @param keys                An array specifying which option maps to which key in the data point array. This makes it convenient to work with unstructured data arrays from different sources.
    * @param legendIndex         The sequential index of the series in the legend.  <div class="demo">Try it:  	<a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    * @param linkedTo            The <a href="#series.id">id</a> of another series to link to. Additionally, the value can be ":previous" to link to the previous series. When two series are linked, only the first one appears in the legend. Toggling the visibility of this also toggles the linked series.
    * @param minSize             The minimum size for a pie in response to auto margins. The pie will try to shrink to make room for data labels in side the plot area, but only to this size.
    * @param name                The name of the series as shown in the legend, tooltip etc.
    * @param point               Properties for each single point
    * @param reversed            The pyramid is reversed by default, as opposed to the funnel, which shares the layout engine, and is not reversed.
    * @param selected            Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    * @param shadow              Whether to apply a drop shadow to the graph line. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param showInLegend        Whether to display this particular series or series type in the legend. Since 2.1, pies are not shown in the legend by default.
    * @param slicedOffset        If a point is sliced, moved out from the center, how many pixels should  it be moved?.
    * @param states              A wrapper object for all the series options in specific states.
    * @param stickyTracking      Sticky tracking of mouse events. When true, the <code>mouseOut</code> event.  on a series isn't triggered until the mouse moves over another series, or out.  of the plot area. When false, the <code>mouseOut</code> event on a series is.  triggered when the mouse leaves the area around the series' graph or markers..  This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the .  tooltip will be hidden when moving the mouse between series.
    * @param tooltip             A configuration object for the tooltip rendering of each single series. Properties are inherited from <a href="#tooltip">tooltip</a>, but only the following properties can be defined on a series level.
    * @param visible             Set the initial visibility of the series.
    * @param width               The width of the funnel compared to the width of the plot area, or the pixel width if it is a number.
    * @param zIndex              Define the visual z index of the series.
    * @param zoneAxis            Defines the Axis on which the zones are applied.
    * @param zones               An array defining zones within a series. Zones can be applied to the X axis, Y axis or Z axis for bubbles, according to the <code>zoneAxis</code> option.
    */
  def apply(allowPointSelect: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            animationLimit: js.UndefOr[Double] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            center: js.UndefOr[(String | Double, String | Double)] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            colors: js.UndefOr[Seq[Color]] = js.undefined,
            cursor: js.UndefOr[String] = js.undefined,
            data: Seq[Double | SeriesPyramidData] = Seq.empty,
            dataLabels: js.UndefOr[SeriesConnectorDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            depth: js.UndefOr[Double] = js.undefined,
            enableMouseTracking: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[SeriesEvents] = js.undefined,
            getExtremesFromAll: js.UndefOr[Boolean] = js.undefined,
            height: js.UndefOr[Double | String] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            index: js.UndefOr[Double] = js.undefined,
            keys: js.UndefOr[Seq[String]] = js.undefined,
            legendIndex: js.UndefOr[Double] = js.undefined,
            linkedTo: js.UndefOr[String] = js.undefined,
            minSize: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            point: js.UndefOr[SeriesPoint] = js.undefined,
            reversed: js.UndefOr[Boolean] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            showCheckbox: js.UndefOr[Boolean] = js.undefined,
            showInLegend: js.UndefOr[Boolean] = js.undefined,
            slicedOffset: js.UndefOr[Double] = js.undefined,
            states: js.UndefOr[SeriesAreaStates] = js.undefined,
            stickyTracking: js.UndefOr[Boolean] = js.undefined,
            tooltip: js.UndefOr[SeriesTooltip] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined,
            width: js.UndefOr[Double | String] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined,
            zoneAxis: js.UndefOr[String] = js.undefined,
            zones: js.UndefOr[Seq[SeriesZone]] = js.undefined): SeriesPyramid = {
    val allowPointSelectOuter = allowPointSelect
    val animationOuter = animation.map(_.value)
    val animationLimitOuter = animationLimit
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val centerOuter = center.map(v => js.Array(v._1, v._2))
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val colorsOuter = colors.map(_.map(_.c).toJSArray)
    val cursorOuter = cursor
    val dataOuter = data.toJSArray.asInstanceOf[js.UndefOr[SeriesPyramid#Data]]
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val depthOuter = depth
    val enableMouseTrackingOuter = enableMouseTracking
    val eventsOuter = events
    val getExtremesFromAllOuter = getExtremesFromAll
    val heightOuter = height
    val idOuter = id
    val indexOuter = index
    val keysOuter = keys.map(_.toJSArray)
    val legendIndexOuter = legendIndex
    val linkedToOuter = linkedTo
    val minSizeOuter = minSize
    val nameOuter = name
    val pointOuter = point
    val reversedOuter = reversed
    val selectedOuter = selected
    val shadowOuter = shadow.map(_.value)
    val showCheckboxOuter = showCheckbox
    val showInLegendOuter = showInLegend
    val slicedOffsetOuter = slicedOffset
    val statesOuter = states
    val stickyTrackingOuter = stickyTracking
    val tooltipOuter = tooltip
    val visibleOuter = visible
    val widthOuter = width
    val zIndexOuter = zIndex
    val zoneAxisOuter = zoneAxis
    val zonesOuter = zones.map(_.toJSArray)

    new SeriesPyramid {
      override val `type`: String = "pyramid"
      override val allowPointSelect = allowPointSelectOuter
      override val animation = animationOuter
      override val animationLimit = animationLimitOuter
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val center = centerOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val colors = colorsOuter
      override val cursor = cursorOuter
      override val data = dataOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val depth = depthOuter
      override val enableMouseTracking = enableMouseTrackingOuter
      override val events = eventsOuter
      override val getExtremesFromAll = getExtremesFromAllOuter
      override val height = heightOuter
      override val id = idOuter
      override val index = indexOuter
      override val keys = keysOuter
      override val legendIndex = legendIndexOuter
      override val linkedTo = linkedToOuter
      override val minSize = minSizeOuter
      override val name = nameOuter
      override val point = pointOuter
      override val reversed = reversedOuter
      override val selected = selectedOuter
      override val shadow = shadowOuter
      override val showCheckbox = showCheckboxOuter
      override val showInLegend = showInLegendOuter
      override val slicedOffset = slicedOffsetOuter
      override val states = statesOuter
      override val stickyTracking = stickyTrackingOuter
      override val tooltip = tooltipOuter
      override val visible = visibleOuter
      override val width = widthOuter
      override val zIndex = zIndexOuter
      override val zoneAxis = zoneAxisOuter
      override val zones = zonesOuter
    }
  }
}
