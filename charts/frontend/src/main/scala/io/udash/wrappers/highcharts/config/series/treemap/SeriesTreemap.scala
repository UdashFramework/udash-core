/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.treemap

import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.utils.{Animation, Color, Shadow}

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
trait SeriesTreemap extends MapSeries {
  override type Data = js.Array[SeriesTreemapData | Double]
  override type DataLabels = SeriesDataLabels
  override type States = SeriesBarStates

  /**
    * When enabled the user can click on a point which is a parent and zoom in on its children.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-allowdrilltonode/" target="_blank">Enabled</a>
    */
  val allowDrillToNode: js.UndefOr[Boolean] = js.undefined

  /**
    * Enabling this option will make the treemap alternate the drawing direction between vertical and horizontal.
    * The next levels starting direction will always be the opposite of the previous.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-alternatestartingdirection-true/" target="_blank">Enabled</a>
    *
    */
  val alternateStartingDirection: js.UndefOr[Boolean] = js.undefined

  /**
    * This option decides if the user can interact with the parent nodes or just the leaf nodes. When this option is
    * undefined, it will be true by default. However when allowDrillToNode is true, then it will be false by default.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-interactbyleaf-false/" target="_blank">false</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-interactbyleaf-true-and-allowdrilltonode/" target="_blank">interactByLeaf and allowDrillToNode is true</a>
    */
  val interactByLeaf: js.UndefOr[Boolean] = js.undefined

  /**
    * This option decides which algorithm is used for setting position and dimensions of the points. Can be one
    * of <code>sliceAndDice</code>, <code>stripes</code>, <code>squarified</code> or <code>strip</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-layoutalgorithm-sliceanddice/" target="_blank">sliceAndDice by default</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-layoutalgorithm-stripes/" target="_blank">stripes</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-layoutalgorithm-squarified/" target="_blank">squarified</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-layoutalgorithm-strip/" target="_blank">strip</a>
    */
  val layoutAlgorithm: js.UndefOr[String] = js.undefined

  /**
    * Defines which direction the layout algorithm will start drawing. Possible values are "vertical" and "horizontal".
    */
  val layoutStartingDirection: js.UndefOr[String] = js.undefined

  /**
    * Used together with the levels and allowDrillToNode options. When set to false the first level visible when drilling
    * is considered to be level one. Otherwise the level will be the same as the tree structure.
    */
  val levelIsConstant: js.UndefOr[Boolean] = js.undefined

  /**
    * Set options on specific levels. Takes precedence over series options, but not point options.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-levels/" target="_blank">Styling dataLabels and borders</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/treemap-with-levels/" target="_blank">Different layoutAlgorithm</a>
    */
  val levels: js.UndefOr[js.Array[SeriesTreemapLevels]] = js.undefined

  /**
    * The sort index of the point inside the treemap level.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/treemap-sortindex/" target="_blank">Sort by years</a>
    */
  val sortIndex: js.UndefOr[Double] = js.undefined
}

object SeriesTreemap {
  import scala.scalajs.js.JSConverters._

  final class LayoutAlgorithm(val name: String) extends AnyVal
  object LayoutAlgorithm {
    val SliceAndDice = new LayoutAlgorithm("sliceAndDice")
    val Stripes = new LayoutAlgorithm("stripes")
    val Squarified = new LayoutAlgorithm("squarified")
    val Strip = new LayoutAlgorithm("strip")
  }

  final class LayoutStartingDirection(val name: String) extends AnyVal
  object LayoutStartingDirection {
    val Vertical = new LayoutStartingDirection("vertical")
    val Horizontal = new LayoutStartingDirection("horizontal")
  }

  /**
    * @param allowDrillToNode           When enabled the user can click on a point which is a parent and zoom in on its children.
    * @param allowPointSelect           Allow this series' points to be selected by clicking on the markers, bars or pie slices.
    * @param alternateStartingDirection Enabling this option will make the treemap alternate the drawing direction between vertical and horizontal.. The next levels starting direction will always be the opposite of the previous.
    * @param animation                  <p>Enable or disable the initial animation when a series is displayed. The animation can also be set as a configuration object. Please note that this option only applies to the initial animation of the series itself. For other animations, see <a href="#chart.animation">chart.animation</a> and the animation parameter under the API methods.		The following properties are supported:</p>. <dl>.   <dt>duration</dt>.   <dd>The duration of the animation in milliseconds.</dd>. <dt>easing</dt>. <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>. </dl>. <p>. Due to poor performance, animation is disabled in old IE browsers for column charts and polar charts.</p>
    * @param animationLimit             For some series, there is a limit that shuts down initial animation by default when the total number of points in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    * @param borderColor                The color of the border surrounding each tree map item.
    * @param borderWidth                The width of the border surrounding each column or bar.
    * @param className           A class name to apply to the series' graphical elements.
    * @param color                      The main color of the series. In heat maps this color is rarely used, as we mostly use the color to denote the value of each point. Unless options are set in the <a href="#colorAxis">colorAxis</a>, the default value is pulled from the <a href="#colors">options.colors</a> array.
    * @param colorByPoint               When using automatic point colors pulled from the <code>options.colors</code>.  collection, this option determines whether the chart should receive .  one color per series or one color per point.
    * @param colors                     A series specific or series type specific color set to apply instead of the global <a href="#colors">colors</a> when <a href="#plotOptions.column.colorByPoint">colorByPoint</a> is true.
    * @param cropThreshold              When the series contains less points than the crop threshold, all points are drawn, event if the points fall outside the visible plot area at the current zoom. The advantage of drawing all points (including markers and columns), is that animation is performed on updates. On the other hand, when the series contains more points than the crop threshold, the series data is cropped to only contain points that fall within the plot area. The advantage of cropping away invisible points is to increase performance on large series.
    * @param cursor                     You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that the points and lines can be clicked.
    * @param data                       An array of data points for the series. For the <code>treemap</code> series type, points can be given in the following ways:.  <ol>.  	<li>An array of numerical values. In this case, the numerical values will .  	be interpreted as <code>value</code> options.  Example:. <pre>data: [0, 5, 3, 5]</pre>.  	</li>.  <li><p>An array of objects with named values. The objects are.  	point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<treemap>.turboThreshold'>turboThreshold</a>, this option is not available.</p>. . <pre>data: [{.     value: 7,.     name: "Point2",.     color: "#00FF00". }, {.     value: 2,.     name: "Point1",.     color: "#FF00FF". }]</pre></li>.  </ol>
    * @param enableMouseTracking        Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points. For large datasets it improves performance.
    * @param getExtremesFromAll         Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis. By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    * @param id                         An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    * @param index                      The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array, the visible Z index as well as the order in the legend.
    * @param interactByLeaf             This option decides if the user can interact with the parent nodes or just the leaf nodes. When this option is undefined, it will be true by default. However when allowDrillToNode is true, then it will be false by default.
    * @param keys                       An array specifying which option maps to which key in the data point array. This makes it convenient to work with unstructured data arrays from different sources.
    * @param layoutAlgorithm            This option decides which algorithm is used for setting position and dimensions of the points. Can be one of <code>sliceAndDice</code>, <code>stripes</code>, <code>squarified</code> or <code>strip</code>.
    * @param layoutStartingDirection    Defines which direction the layout algorithm will start drawing. Possible values are "vertical" and "horizontal".
    * @param legendIndex                The sequential index of the series in the legend.  <div class="demo">Try it:  	<a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    * @param levelIsConstant            Used together with the levels and allowDrillToNode options. When set to false the first level visible when drilling is considered to be level one. Otherwise the level will be the same as the tree structure.
    * @param levels                     Set options on specific levels. Takes precedence over series options, but not point options.
    * @param linkedTo                   The <a href="#series.id">id</a> of another series to link to. Additionally, the value can be ":previous" to link to the previous series. When two series are linked, only the first one appears in the legend. Toggling the visibility of this also toggles the linked series.
    * @param maxPointWidth              The maximum allowed pixel width for a column, translated to the height of a bar in a bar chart. This prevents the columns from becoming too wide when there is a small number of points in the chart.
    * @param name                       The name of the series as shown in the legend, tooltip etc.
    * @param point                      Properties for each single point
    * @param selected                   Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    * @param shadow                     Whether to apply a drop shadow to the graph line. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param showCheckbox               If true, a checkbox is displayed next to the legend item to allow selecting the series. The state of the checkbox is determined by the <code>selected</code> option.
    * @param showInLegend               Whether to display this series type or specific series item in the legend.
    * @param sortIndex                  The sort index of the point inside the treemap level.
    * @param states                     A wrapper object for all the series options in specific states.
    * @param stickyTracking             Sticky tracking of mouse events. When true, the <code>mouseOut</code> event.  on a series isn't triggered until the mouse moves over another series, or out.  of the plot area. When false, the <code>mouseOut</code> event on a series is.  triggered when the mouse leaves the area around the series' graph or markers..  This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the .  tooltip will be hidden when moving the mouse between series. Defaults to true for line and area type series, but to false for columns, pies etc.
    * @param turboThreshold             When a series contains a data array that is longer than this, only one dimensional arrays of numbers,.  or two dimensional arrays with x and y values are allowed. Also, only the first.  point is tested, and the rest are assumed to be the same format. This saves expensive.  data checking and indexing in long series. Set it to <code>0</code> disable.
    * @param visible                    Set the initial visibility of the series.
    * @param xAxis                      When using dual or multiple x axes, this number defines which xAxis the particular series is connected to. It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    * @param yAxis                      When using dual or multiple y axes, this number defines which yAxis the particular series is connected to. It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    * @param zIndex                     Define the visual z index of the series.
    * @param zoneAxis                   Defines the Axis on which the zones are applied.
    * @param zones                      An array defining zones within a series. Zones can be applied to the X axis, Y axis or Z axis for bubbles, according to the <code>zoneAxis</code> option.
    */
  def apply(allowDrillToNode: js.UndefOr[Boolean] = js.undefined,
            allowPointSelect: js.UndefOr[Boolean] = js.undefined,
            alternateStartingDirection: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            animationLimit: js.UndefOr[Double] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            colorByPoint: js.UndefOr[Boolean] = js.undefined,
            colors: js.UndefOr[Seq[Color]] = js.undefined,
            cropThreshold: js.UndefOr[Double] = js.undefined,
            cursor: js.UndefOr[String] = js.undefined,
            data: Seq[SeriesTreemapData | Double] = Seq.empty,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            enableMouseTracking: js.UndefOr[Boolean] = js.undefined,
            events: js.UndefOr[SeriesEvents] = js.undefined,
            getExtremesFromAll: js.UndefOr[Boolean] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            index: js.UndefOr[Double] = js.undefined,
            interactByLeaf: js.UndefOr[Boolean] = js.undefined,
            keys: js.UndefOr[Seq[String]] = js.undefined,
            layoutAlgorithm: js.UndefOr[LayoutAlgorithm] = js.undefined,
            layoutStartingDirection: js.UndefOr[LayoutStartingDirection] = js.undefined,
            legendIndex: js.UndefOr[Double] = js.undefined,
            levelIsConstant: js.UndefOr[Boolean] = js.undefined,
            levels: js.UndefOr[Seq[SeriesTreemapLevels]] = js.undefined,
            linkedTo: js.UndefOr[String] = js.undefined,
            maxPointWidth: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            point: js.UndefOr[SeriesPoint] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            showCheckbox: js.UndefOr[Boolean] = js.undefined,
            showInLegend: js.UndefOr[Boolean] = js.undefined,
            sortIndex: js.UndefOr[Double] = js.undefined,
            states: js.UndefOr[SeriesBarStates] = js.undefined,
            stickyTracking: js.UndefOr[Boolean] = js.undefined,
            tooltip: js.UndefOr[SeriesTooltip] = js.undefined,
            turboThreshold: js.UndefOr[Double] = js.undefined,
            visible: js.UndefOr[Boolean] = js.undefined,
            xAxis: js.UndefOr[Int | String] = js.undefined,
            yAxis: js.UndefOr[Int | String] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined,
            zoneAxis: js.UndefOr[String] = js.undefined,
            zones: js.UndefOr[Seq[SeriesZone]] = js.undefined): SeriesTreemap = {
    val allowDrillToNodeOuter = allowDrillToNode
    val allowPointSelectOuter = allowPointSelect
    val alternateStartingDirectionOuter = alternateStartingDirection
    val animationOuter = animation.map(_.value)
    val animationLimitOuter = animationLimit
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val colorByPointOuter = colorByPoint
    val colorsOuter = colors.map(_.map(_.c).toJSArray)
    val cropThresholdOuter = cropThreshold
    val cursorOuter = cursor
    val dataOuter = data.toJSArray.asInstanceOf[js.UndefOr[SeriesTreemap#Data]]
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val enableMouseTrackingOuter = enableMouseTracking
    val eventsOuter = events
    val getExtremesFromAllOuter = getExtremesFromAll
    val idOuter = id
    val indexOuter = index
    val interactByLeafOuter = interactByLeaf
    val keysOuter = keys.map(_.toJSArray)
    val layoutAlgorithmOuter = layoutAlgorithm.map(_.name)
    val layoutStartingDirectionOuter = layoutStartingDirection.map(_.name)
    val legendIndexOuter = legendIndex
    val levelIsConstantOuter = levelIsConstant
    val levelsOuter = levels.map(_.toJSArray)
    val linkedToOuter = linkedTo
    val maxPointWidthOuter = maxPointWidth
    val nameOuter = name
    val pointOuter = point
    val selectedOuter = selected
    val shadowOuter = shadow.map(_.value)
    val showCheckboxOuter = showCheckbox
    val showInLegendOuter = showInLegend
    val sortIndexOuter = sortIndex
    val statesOuter = states
    val stickyTrackingOuter = stickyTracking
    val tooltipOuter = tooltip
    val turboThresholdOuter = turboThreshold
    val visibleOuter = visible
    val xAxisOuter = xAxis
    val yAxisOuter = yAxis
    val zIndexOuter = zIndex
    val zoneAxisOuter = zoneAxis
    val zonesOuter = zones.map(_.toJSArray)

    new SeriesTreemap {
      override val `type`: String = "treemap"
      override val allowDrillToNode = allowDrillToNodeOuter
      override val allowPointSelect = allowPointSelectOuter
      override val alternateStartingDirection = alternateStartingDirectionOuter
      override val animation = animationOuter
      override val animationLimit = animationLimitOuter
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val colorByPoint = colorByPointOuter
      override val colors = colorsOuter
      override val cropThreshold = cropThresholdOuter
      override val cursor = cursorOuter
      override val data = dataOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val enableMouseTracking = enableMouseTrackingOuter
      override val events = eventsOuter
      override val getExtremesFromAll = getExtremesFromAllOuter
      override val id = idOuter
      override val index = indexOuter
      override val interactByLeaf = interactByLeafOuter
      override val keys = keysOuter
      override val layoutAlgorithm = layoutAlgorithmOuter
      override val layoutStartingDirection = layoutStartingDirectionOuter
      override val legendIndex = legendIndexOuter
      override val levelIsConstant = levelIsConstantOuter
      override val levels = levelsOuter
      override val linkedTo = linkedToOuter
      override val maxPointWidth = maxPointWidthOuter
      override val name = nameOuter
      override val point = pointOuter
      override val selected = selectedOuter
      override val shadow = shadowOuter
      override val showCheckbox = showCheckboxOuter
      override val showInLegend = showInLegendOuter
      override val sortIndex = sortIndexOuter
      override val states = statesOuter
      override val stickyTracking = stickyTrackingOuter
      override val tooltip = tooltipOuter
      override val turboThreshold = turboThresholdOuter
      override val visible = visibleOuter
      override val xAxis = xAxisOuter
      override val yAxis = yAxisOuter
      override val zIndex = zIndexOuter
      override val zoneAxis = zoneAxisOuter
      override val zones = zonesOuter
    }
  }
}
