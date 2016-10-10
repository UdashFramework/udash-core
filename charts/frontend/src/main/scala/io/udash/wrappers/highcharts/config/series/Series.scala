/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
abstract class Series extends js.Object {
  type Data
  type DataLabels <: BaseSeriesDataLabels
  val `type`: String

  /**
    * <p>Enable or disable the initial animation when a series is displayed. The animation can also be set as a configuration object.
    * Please note that this option only applies to the initial animation of the series itself. For other animations,
    * see <a href="#chart.animation">chart.animation</a> and the animation parameter under the API methods.
    * The following properties are supported:</p>
    * <dl>
    * <dt>duration</dt>
    * <dd>The duration of the animation in milliseconds.</dd>
    * <dt>easing</dt>
    * <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>
    * </dl>
    * <p>
    * Due to poor performance, animation is disabled in old IE browsers for column charts and polar charts.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-disabled/" target="_blank">Animation disabled</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-slower/" target="_blank">slower animation</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/" target="_blank">custom easing function</a>.
    */
  val animation: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * For some series, there is a limit that shuts down initial animation by default when the total number of points
    * in the chart is too high. For example, for a column chart and its derivatives, animation doesn't run if there
    * is more than 250 points totally. To disable this cap, set <code>animationLimit</code> to <code>Infinity</code>.
    */
  val animationLimit: js.UndefOr[Double] = js.undefined

  /**
    * A class name to apply to the series' graphical elements.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * The main color or the series. In line type series it applies to the line and the point markers unless otherwise
    * specified. In bar type series it applies to the bars unless a color is specified per point. The default value is
    * pulled from the  <code>options.colors</code> array.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-color-general/" target="_blank">General plot option</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-color-specific/" target="_blank">one specific series</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-color-area/" target="_blank">area color</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * You can set the cursor to "pointer" if you have click events attached to  the series, to signal to the user that
    * the points and lines can be clicked.
    *
    * @example Pointer cursor <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-cursor-line/" target="_blank">on line graph</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-cursor-column/" target="_blank">on columns</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-cursor-scatter/" target="_blank">on scatter markers</a>
    */
  val cursor: js.UndefOr[String] = js.undefined

  /**
    * An array of data points for the series. For the <code>area</code> series type, points can be given in the following ways:
    * <ol>
    * <li>An array of numerical values. In this case, the numerical values will
    * be interpreted as <code>y</code> options. The <code>x</code> values will be automatically calculated,
    * either starting at 0 and incremented by 1, or from <code>pointStart</code>
    * and <code>pointInterval</code> given in the series options. If the axis
    * has categories, these will be used.  Example:
    * <pre>data: [0, 5, 3, 5]</pre>
    * </li>
    * <li><p>An array of arrays with 2 values. In this case, the values correspond to <code>x,y</code>. If the first value is a string, it is
    * applied as the name of the point, and the <code>x</code> value is inferred. <pre>data: [
    * [0, 9],
    * [1, 7],
    * [2, 6]
    * ]</pre></li>
    *
    *
    * <li><p>An array of objects with named values. The objects are
    * point configuration objects as seen below. If the total number of data points exceeds the series' <a href='#series<area>.turboThreshold'>turboThreshold</a>,
    * this option is not available.</p>
    *
    * <pre>data: [{
    * x: 1,
    * y: 9,
    * name: "Point2",
    * color: "#00FF00"
    * }, {
    * x: 1,
    * y: 6,
    * name: "Point1",
    * color: "#FF00FF"
    * }]</pre></li>
    * </ol>
    *
    * @example <p>The demos use a line series, but the principle is the same for all types.</p>
    *          <ul>
    *          <li><a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/reflow-true/" target="_blank">Numerical values</a></li>
    *          <li><a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-array-of-arrays/" target="_blank">Arrays of numeric x and y</a></li>
    *          <li><a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-array-of-arrays-datetime/" target="_blank">Arrays of datetime x and y</a></li>
    *          <li><a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-array-of-name-value/" target="_blank">Arrays of point.name and y</a></li>
    *          <li><a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-array-of-objects/" target="_blank"> Config objects</a></li>
    *          </ul>
    */
  val data: js.UndefOr[Data] = js.undefined

  /**
    * <p>Options for the series data labels, appearing next to each data point.</p>
    * <p>In <a href="http://www.highcharts.com/docs/chart-design-and-style/style-by-css">styled mode</a>, the data labels can be styled wtih the <code>.highcharts-data-label-box</code> and <code>.highcharts-data-label</code> class names (<a href=#http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/css/series-datalabels/">see example</a>).</p>
    */
  val dataLabels: js.UndefOr[DataLabels] = js.undefined

  /**
    * <p><i>Requires Accessibility module</i></p>
    * <p>A description of the series to add to the screen reader information about the series.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/stock/accessibility/accessible-stock/">Accessible stock chart</a>
    */
  val description: js.UndefOr[String] = js.undefined

  /**
    * Enable or disable the mouse tracking for a specific series. This includes point tooltips and click events on graphs and points.
    * For large datasets it improves performance.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-enablemousetracking-false/" target="_blank">No mouse tracking</a>
    */
  val enableMouseTracking: js.UndefOr[Boolean] = js.undefined

  val events: js.UndefOr[SeriesEvents] = js.undefined

  /**
    * Whether to use the Y extremes of the total chart width or only the zoomed area when zooming in on parts of the X axis.
    * By default, the Y axis adjusts to the min and max of the visible data. Cartesian series only.
    */
  val getExtremesFromAll: js.UndefOr[Boolean] = js.undefined

  /**
    * An id for the series. This can be used after render time to get a pointer to the series object through <code>chart.get()</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-id/" target="_blank">Get series by id</a>
    */
  val id: js.UndefOr[String] = js.undefined

  /**
    * The index of the series in the chart, affecting the internal index in the <code>chart.series</code> array,
    * the visible Z index as well as the order in the legend.
    */
  val index: js.UndefOr[Double] = js.undefined

  /**
    * An array specifying which option maps to which key in the data point array. This makes it convenient to
    * work with unstructured data arrays from different sources.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-keys/" target="_blank">An extended data array with keys</a>
    */
  val keys: js.UndefOr[js.Array[String]] = js.undefined

  /**
    * The sequential index of the series in the legend.  <div class="demo">Try it:
    *
    * <a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/series/legendindex/" target="_blank">Legend in opposite order</a> </div>.
    */
  val legendIndex: js.UndefOr[Double] = js.undefined

  /**
    * The <a href="#series.id">id</a> of another series to link to. Additionally, the value can be ":previous" to link to the previous series.
    * When two series are linked, only the first one appears in the legend. Toggling the visibility of this also toggles the linked series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/arearange-line/" target="_blank">Linked series</a>
    */
  val linkedTo: js.UndefOr[String] = js.undefined

  /**
    * The name of the series as shown in the legend, tooltip etc.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/name/" target="_blank">Series name</a>
    */
  val name: js.UndefOr[String] = js.undefined

  /**
    * Properties for each single point
    */
  val point: js.UndefOr[SeriesPoint] = js.undefined

  /**
    * Whether to select the series initially. If <code>showCheckbox</code> is true, the checkbox next to the series name will be checked for a selected series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-selected/" target="_blank">One out of two series selected</a>
    */
  val selected: js.UndefOr[Boolean] = js.undefined

  /**
    * If true, a checkbox is displayed next to the legend item to allow selecting the series.
    * The state of the checkbox is determined by the <code>selected</code> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-showcheckbox-true/" target="_blank">Show select box</a>
    */
  val showCheckbox: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to display this particular series or series type in the legend. The default value
    * is <code>true</code> for standalone series, <code>false</code> for linked series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-showinlegend/" target="_blank">One series in the legend, one hidden</a>
    */
  val showInLegend: js.UndefOr[Boolean] = js.undefined

  /**
    * Sticky tracking of mouse events. When true, the <code>mouseOut</code> event
    * on a series isn't triggered until the mouse moves over another series, or out
    * of the plot area. When false, the <code>mouseOut</code> event on a series is
    * triggered when the mouse leaves the area around the series' graph or markers.
    * This also implies the tooltip. When <code>stickyTracking</code> is false and <code>tooltip.shared</code> is false, the
    * tooltip will be hidden when moving the mouse between series. Defaults to true for line and area type series, but to false for columns, pies etc.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stickytracking-true/" target="_blank">True by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stickytracking-false/" target="_blank">false</a>
    */
  val stickyTracking: js.UndefOr[Boolean] = js.undefined

  /**
    * A configuration object for the tooltip rendering of each single series. Properties are inherited
    * from <a href="#tooltip">tooltip</a>, but only the following properties can be defined on a series level.
    */
  val tooltip: js.UndefOr[SeriesTooltip] = js.undefined

  /**
    * Set the initial visibility of the series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-visible/" target="_blank">Two series, one hidden and one visible</a>
    */
  val visible: js.UndefOr[Boolean] = js.undefined

  /**
    * Define the visual z index of the series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-zindex-default/" target="_blank">With no z index, the series defined last are on top</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-zindex/" target="_blank">with a z index, the series with the highest z index is on top</a>.
    */
  val zIndex: js.UndefOr[Int] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class StandardSeries extends Series {
  type States <: SeriesStates[_]

  /**
    * Allow this series' points to be selected by clicking on the markers, bars or pie slices.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-allowpointselect-line/" target="_blank">Line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-allowpointselect-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-allowpointselect-pie/" target="_blank">pie</a>
    */
  val allowPointSelect: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to apply a drop shadow to the graph line. Since 2.3 the shadow can be an object configuration
    * containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-shadow/" target="_blank">Shadow enabled</a>
    */
  val shadow: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * A wrapper object for all the series options in specific states.
    */
  val states: js.UndefOr[States] = js.undefined

  /**
    * Defines the Axis on which the zones are applied.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/color-zones-zoneaxis-x/" target="_blank">Zones on the X-Axis</a>
    */
  val zoneAxis: js.UndefOr[String] = js.undefined

  /**
    * An array defining zones within a series. Zones can be applied to the X axis, Y axis or Z axis for bubbles,
    * according to the <code>zoneAxis</code> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/color-zones-simple/" target="_blank">Color zones</a>
    */
  val zones: js.UndefOr[js.Array[SeriesZone]] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class XYSeries extends StandardSeries {
  /**
    * When using dual or multiple x axes, this number defines which xAxis the particular series is connected to.
    * It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    */
  val xAxis: js.UndefOr[Int | String] = js.undefined

  /**
    * When using dual or multiple y axes, this number defines which yAxis the particular series is connected to.
    * It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/yaxis/" target="_blank">Apply the column series to the secondary Y axis</a>
    */
  val yAxis: js.UndefOr[Int | String] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class PointSeries extends XYSeries {
  /**
    * <p>If no x values are given for the points in a series, pointInterval defines
    * the interval of the x values. For example, if a series contains one value
    * every decade starting from year 0, set pointInterval to 10.</p>
    * <p>Since Highcharts 4.1, it can be combined with <code>pointIntervalUnit</code> to draw irregular intervals.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointstart-datetime/" target="_blank">Datetime X axis</a>
    */
  val pointInterval: js.UndefOr[Double] = js.undefined

  /**
    * On datetime series, this allows for setting the <a href="plotOptions.series.pointInterval">pointInterval</a>
    * to irregular time units, <code>day</code>, <code>month</code> and <code>year</code>. A day is usually the same as
    * 24 hours, but pointIntervalUnit also takes the DST crossover into consideration when dealing with local time.
    * Combine this option with <code>pointInterval</code> to draw weeks, quarters, 6 months, 10 years etc.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointintervalunit/" target="_blank">One point a month</a>
    */
  val pointIntervalUnit: js.UndefOr[String] = js.undefined

  /**
    * If no x values are given for the points in a series, pointStart defines on what value to start. For example,
    * if a series contains one yearly value starting from 1945, set pointStart to 1945.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointstart-linear/" target="_blank">Linear</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointstart-datetime/" target="_blank">datetime</a> X axis
    */
  val pointStart: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class BoxSeries extends PointSeries {
  /**
    * When using automatic point colors pulled from the <code>options.colors</code>
    * collection, this option determines whether the chart should receive
    * one color per series or one color per point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-colorbypoint-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-colorbypoint-true/" target="_blank">true</a>
    */
  val colorByPoint: js.UndefOr[Boolean] = js.undefined

  /**
    * A series specific or series type specific color set to apply instead of the global <a href="#colors">colors</a>
    * when <a href="#plotOptions.column.colorByPoint">colorByPoint</a> is true.
    */
  val colors: js.UndefOr[js.Array[String | js.Object]] = js.undefined

  /**
    * When true, each column edge is rounded to its nearest pixel in order to render sharp on screen.
    * In some cases, when there are a lot of densely packed columns, this leads to visible difference in column
    * widths or distance between columns. In these cases, setting crisp to false may look better, even though each
    * column is rendered blurry. Defaults to true.
    */
  val crisp: js.UndefOr[Boolean] = js.undefined

  /**
    * Depth of the columns in a 3D column chart. Requires <code>highcharts-3d.js</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/3d/column/">Basic 3D column chart</a>
    */
  val depth: js.UndefOr[Double] = js.undefined

  /**
    * 3D columns only. The color of the edges. Similar to <code>borderColor</code>, except it defaults to the same color as the column.
    */
  val edgeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * 3D columns only. The width of the colored edges.
    */
  val edgeWidth: js.UndefOr[Double] = js.undefined

  /**
    * Padding between each value groups, in x axis units.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-grouppadding-default/" target="_blank">0.2 by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-grouppadding-none/" target="_blank">no group padding - all
    *          columns are evenly spaced</a>
    */
  val groupPadding: js.UndefOr[Double] = js.undefined

  /**
    * The spacing between columns on the Z Axis in a 3D chart. Requires <code>highcharts-3d.js</code>.
    */
  val groupZPadding: js.UndefOr[Double] = js.undefined

  /**
    * The maximum allowed pixel width for a column, translated to the height of a bar in a bar chart.
    * This prevents the columns from becoming too wide when there is a small number of points in the chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-maxpointwidth-20/" target="_blank">Limited to 50</a>
    */
  val maxPointWidth: js.UndefOr[Double] = js.undefined

  /**
    * Padding between each column or bar, in x axis units.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-pointpadding-default/" target="_blank">0.1 by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-pointpadding-025/" target="_blank">0.25</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-pointpadding-none/" target="_blank">0 for tightly packed columns</a>,
    */
  val pointPadding: js.UndefOr[Double] = js.undefined

  /**
    * <p>Possible values: <code>null</code>, <code>"on"</code>, <code>"between"</code>.</p>
    * <p>In a column chart, when pointPlacement is <code>"on"</code>, the point will not create any padding of the X axis.
    * In a polar column chart this means that the first column points directly north. If the pointPlacement is <code>"between"</code>,
    * the columns will be laid out between ticks. This is useful for example for visualising an amount between two points
    * in time or in a certain sector of a polar chart.</p>
    * <p>Since Highcharts 3.0.2, the point placement can also be numeric, where 0 is on the axis value, -0.5 is
    * between this value and the previous, and 0.5 is between this value and the next. Unlike the textual options,
    * numeric point placement options won't affect axis padding.</p>
    * <p>Note that pointPlacement needs a <a href="#plotOptions.series.pointRange">pointRange</a> to work. For column
    * series this is computed, but for line-type series it needs to be set.</p>
    * <p>Defaults to <code>null</code> in cartesian charts, <code>"between"</code> in polar charts.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointplacement-between/" target="_blank">Between in a column chart</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointplacement-numeric/" target="_blank">numeric placement for custom layout</a>.
    */
  val pointPlacement: js.UndefOr[String | Double] = js.undefined

  /**
    * The X axis range that each point is valid for. This determines the width of the column. On a categorized axis,
    * the range will be 1 by default (one category unit). On linear and datetime axes, the range will be computed as
    * the distance between the two closest data points.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-pointrange/" target="_blank">Set the point range to one day on a data set with one week between the points</a>
    */
  val pointRange: js.UndefOr[Double] = js.undefined

  /**
    * A pixel value specifying a fixed width for each column or bar. When <code>null</code>, the width is calculated
    * from the <code>pointPadding</code> and <code>groupPadding</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-pointwidth-20/" target="_blank">20px wide columns regardless of chart width
    *          or the amount of data points</a>
    */
  val pointWidth: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class BarSeries extends BoxSeries {
  /**
    * The color of the border surrounding each column or bar.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-bordercolor/" target="_blank">Dark gray border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The corner radius of the border surrounding each column or bar.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-borderradius/" target="_blank">Rounded columns</a>
    */
  val borderRadius: js.UndefOr[Double] = js.undefined

  /**
    * The width of the border surrounding each column or bar.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-borderwidth/" target="_blank">2px black border</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * Whether to group non-stacked columns or to let them render independent of each other.
    * Non-grouped columns will be laid out individually and overlap each other.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-grouping-false/" target="_blank">Grouping disabled</a>
    */
  val grouping: js.UndefOr[Boolean] = js.undefined

  /**
    * The minimal height for a column or width for a bar. By default, 0 values are not shown.
    * To visualize a 0 (or close to zero) point, set the minimal point length to a  pixel value like 3.
    * In stacked column charts, minPointLength might not be respected for tightly packed values.
    *
    * @example Set to three with <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-minpointlength/" target="_blank">zero base value</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-minpointlength-pos-and-neg/" target="_blank">positive and negative close to zero values</a>
    */
  val minPointLength: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class NonRangeBarSeries extends BarSeries {
  /**
    * <p>When this is true, the series will not cause the Y axis to cross the zero plane
    * (or <a href="#plotOptions.series.threshold">threshold</a> option) unless the data actually crosses the plane.</p>
    *
    * <p>For example, if <code>softThreshold</code> is <code>false</code>, a series of 0, 1, 2, 3 will make the Y axis show negative values according to the <code>minPadding</code> option. If <code>softThreshold</code> is <code>true</code>, the Y axis starts at 0.</p>
    */
  val softThreshold: js.UndefOr[Boolean] = js.undefined

  /**
    * This option allows grouping series in a stacked chart. The stack option can be a string
    * or a number or anything else, as long as the grouped series' stack options match each other.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/stack/" target="_blank">Stacked and grouped columns</a>
    */
  val stack: js.UndefOr[String] = js.undefined

  /**
    * The Y axis value to serve as the base for the columns, for distinguishing between values above and below a threshold.
    * If <code>null</code>, the columns extend from the padding Y axis minimum.
    */
  val threshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class StrictBarSeries extends NonRangeBarSeries {
  /**
    * When the series contains less points than the crop threshold, all points are drawn,
    * event if the points fall outside the visible plot area at the current zoom. The advantage of drawing all points
    * (including markers and columns), is that animation is performed on updates. On the other hand, when the series contains
    * more points than the crop threshold, the series data is cropped to only contain points that fall within the plot area.
    * The advantage of cropping away invisible points is to increase performance on large series.
    */
  val cropThreshold: js.UndefOr[Double] = js.undefined

  /**
    * The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-negative-color/" target="_blank">Spline, area and column</a> - <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-negativecolor/" target="_blank">arearange</a>.
    */
  val negativeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Whether to stack the values of each series on top of each other. Possible values are null to disable, "normal" to stack by value or "percent".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-line/" target="_blank">Line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-bar/" target="_blank">bar</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-area/" target="_blank">area</a> with "normal" stacking.
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-line/" target="_blank">Line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-bar/" target="_blank">bar</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-area/" target="_blank">area</a> with "percent" stacking.
    */
  val stacking: js.UndefOr[String] = js.undefined

  /**
    * When a series contains a data array that is longer than this, only one dimensional arrays of numbers,
    * or two dimensional arrays with x and y values are allowed. Also, only the first
    * point is tested, and the rest are assumed to be the same format. This saves expensive
    * data checking and indexing in long series. Set it to <code>0</code> disable.
    */
  val turboThreshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class LineSeries extends PointSeries {
  /**
    * Polar charts only. Whether to connect the ends of a line series plot across the extremes.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/line-connectends-false/" target="_blank">Do not connect</a>
    */
  val connectEnds: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to connect a graph line across null points.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-connectnulls-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-connectnulls-true/" target="_blank">true</a>
    */
  val connectNulls: js.UndefOr[Boolean] = js.undefined

  /**
    * When the series contains less points than the crop threshold, all points are drawn,  even if the points fall
    * outside the visible plot area at the current zoom. The advantage of drawing all points (including markers and columns),
    * is that animation is performed on updates. On the other hand, when the series contains more points than the crop threshold,
    * the series data is cropped to only contain points that fall within the plot area.
    *
    * The advantage of cropping away invisible points is to increase performance on large series.
    */
  val cropThreshold: js.UndefOr[Double] = js.undefined

  /**
    * A name for the dash style to use for the graph. Applies only to series type having a graph,
    * like <code>line</code>, <code>spline</code>, <code>area</code> and <code>scatter</code> in
    * case it has a <code>lineWidth</code>. The value for the <code>dashStyle</code> include:
    * <ul>
    * <li>Solid</li>
    * <li>ShortDash</li>
    * <li>ShortDot</li>
    * <li>ShortDashDot</li>
    * <li>ShortDashDotDot</li>
    * <li>Dot</li>
    * <li>Dash</li>
    * <li>LongDash</li>
    * <li>DashDot</li>
    * <li>LongDashDot</li>
    * <li>LongDashDotDot</li>
    * </ul>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/" target="_blank">Possible values demonstrated</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle/" target="_blank">chart suitable for printing in black and white</a>
    */
  val dashStyle: js.UndefOr[String] = js.undefined

  /**
    * Pixel with of the graph line.
    *
    * @example 5px <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-linewidth-general/" target="_blank">on all series</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-linewidth-specific/" target="_blank">on one single series</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  /**
    * The line cap used for line ends and line joins on the graph.
    */
  val linecap: js.UndefOr[String] = js.undefined

  /**
    * The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-negative-color/" target="_blank">Spline, area and column</a> - <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-negativecolor/" target="_blank">arearange</a>.
    */
  val negativeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * <p>Possible values: <code>null</code>, <code>"on"</code>, <code>"between"</code>.</p>
    * <p>In a column chart, when pointPlacement is <code>"on"</code>, the point will not create any padding of the X axis.
    * In a polar column chart this means that the first column points directly north. If the pointPlacement is <code>"between"</code>,
    * the columns will be laid out between ticks. This is useful for example for visualising an amount between two points
    * in time or in a certain sector of a polar chart.</p>
    * <p>Since Highcharts 3.0.2, the point placement can also be numeric, where 0 is on the axis value, -0.5 is
    * between this value and the previous, and 0.5 is between this value and the next. Unlike the textual options,
    * numeric point placement options won't affect axis padding.</p>
    * <p>Note that pointPlacement needs a <a href="#plotOptions.series.pointRange">pointRange</a> to work. For column
    * series this is computed, but for line-type series it needs to be set.</p>
    * <p>Defaults to <code>null</code> in cartesian charts, <code>"between"</code> in polar charts.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointplacement-between/" target="_blank">Between in a column chart</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-pointplacement-numeric/" target="_blank">numeric placement for custom layout</a>.
    */
  val pointPlacement: js.UndefOr[String | Double] = js.undefined

  /**
    * <p>When this is true, the series will not cause the Y axis to cross the zero plane
    * (or <a href="#plotOptions.series.threshold">threshold</a> option) unless the data actually crosses the plane.</p>
    *
    * <p>For example, if <code>softThreshold</code> is <code>false</code>, a series of 0, 1, 2, 3 will make
    * the Y axis show negative values according to the <code>minPadding</code> option. If <code>softThreshold</code>
    * is <code>true</code>, the Y axis starts at 0.</p>
    */
  val softThreshold: js.UndefOr[Boolean] = js.undefined

  /**
    * When a series contains a data array that is longer than this, only one dimensional arrays of numbers,
    * or two dimensional arrays with x and y values are allowed. Also, only the first
    * point is tested, and the rest are assumed to be the same format. This saves expensive
    * data checking and indexing in long series. Set it to <code>0</code> disable.
    */
  val turboThreshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class AreaLineSeries extends LineSeries {
  /**
    * Fill color or gradient for the area. When <code>null</code>, the series' <code>color</code>  is
    * used with the series' <code>fillOpacity</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/area-fillcolor-default/" target="_blank">Null by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/area-fillcolor-gradient/" target="_blank">gradient</a>
    */
  val fillColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Fill opacity for the area. Note that when you set an explicit <code>fillColor</code>, the <code>fillOpacity</code>
    * is not applied. Instead, you should define the opacity in the <code>fillColor</code> with an rgba color definition.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/area-fillopacity/" target="_blank">Automatic fill color and fill opacity of 0.1</a>
    */
  val fillOpacity: js.UndefOr[Double] = js.undefined

  /**
    * A separate color for the graph line. By default the line takes the <code>color</code> of the series,
    * but the lineColor setting allows setting a separate color for the line without altering the <code>fillColor</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/area-linecolor/" target="_blank">Dark gray line</a>
    */
  val lineColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * A separate color for the negative part of the area.
    */
  val negativeFillColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Whether the whole area or just the line should respond to mouseover tooltips and other mouse or touch events.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/area-trackbyarea/" target="_blank">Display the tooltip when the
    *          area is hovered</a>
    */
  val trackByArea: js.UndefOr[Boolean] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class StrictLineSeries extends LineSeries {
  val marker: js.UndefOr[SeriesMarker] = js.undefined

  /**
    * This option allows grouping series in a stacked chart. The stack option can be a string or a number or anything else,
    * as long as the grouped series' stack options match each other.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/stack/" target="_blank">Stacked and grouped columns</a>
    */
  val stack: js.UndefOr[String] = js.undefined

  /**
    * Whether to stack the values of each series on top of each other.
    * Possible values are null to disable, "normal" to stack by value or "percent".
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-line/" target="_blank">Line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-bar/" target="_blank">bar</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-area/" target="_blank">area</a> with "normal" stacking.
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-line/" target="_blank">Line</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-column/" target="_blank">column</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-bar/" target="_blank">bar</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-stacking-percent-area/" target="_blank">area</a> with "percent" stacking.
    */
  val stacking: js.UndefOr[String] = js.undefined

  /**
    * The threshold, also called zero level or base level.
    * For line type series this is only used in conjunction with <a href="#plotOptions.series.negativeColor">negativeColor</a>.
    */
  val threshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class FreePointsSeries extends PointSeries {
  /**
    * When the series contains less points than the crop threshold, all points are drawn,
    * even if the points fall outside the visible plot area at the current zoom.
    * The advantage of drawing all points (including markers and columns), is that animation is performed on updates.
    * On the other hand, when the series contains more points than the crop threshold,
    * the series data is cropped to only contain points that fall within the plot area.
    * The advantage of cropping away invisible points is to increase performance on large series.
    */
  val cropThreshold: js.UndefOr[Double] = js.undefined

  /**
    * A name for the dash style to use for the graph. Applies only to series type having a graph,
    * like <code>line</code>, <code>spline</code>, <code>area</code> and <code>scatter</code> in
    * case it has a <code>lineWidth</code>. The value for the <code>dashStyle</code> include:
    * <ul>
    * <li>Solid</li>
    * <li>ShortDash</li>
    * <li>ShortDot</li>
    * <li>ShortDashDot</li>
    * <li>ShortDashDotDot</li>
    * <li>Dot</li>
    * <li>Dash</li>
    * <li>LongDash</li>
    * <li>DashDot</li>
    * <li>LongDashDot</li>
    * <li>LongDashDotDot</li>
    * </ul>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/" target="_blank">Possible values demonstrated</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-dashstyle/" target="_blank">chart suitable for printing in black and white</a>
    */
  val dashStyle: js.UndefOr[String] = js.undefined

  /**
    * The width of the line connecting the data points.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/scatter-linewidth-none/" target="_blank">0 by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/scatter-linewidth-1/" target="_blank">1px</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  val marker: js.UndefOr[SeriesMarker] = js.undefined

  /**
    * When a point's Z value is below the <a href="#plotOptions.bubble.zThreshold">zThreshold</a> setting, this color is used.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/bubble-negative/" target="_blank">Negative bubbles</a>
    */
  val negativeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * When a series contains a data array that is longer than this, only one dimensional arrays of numbers,
    * or two dimensional arrays with x and y values are allowed. Also, only the first
    * point is tested, and the rest are assumed to be the same format. This saves expensive
    * data checking and indexing in long series. Set it to <code>0</code> disable.
    */
  val turboThreshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class MapSeries extends XYSeries {
  /**
    * The color of the border surrounding each column or bar.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-bordercolor/" target="_blank">Dark gray border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The width of the border surrounding each column or bar.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-borderwidth/" target="_blank">2px black border</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * When using automatic point colors pulled from the <code>options.colors</code>
    * collection, this option determines whether the chart should receive
    * one color per series or one color per point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-colorbypoint-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-colorbypoint-true/" target="_blank">true</a>
    */
  val colorByPoint: js.UndefOr[Boolean] = js.undefined

  /**
    * A series specific or series type specific color set to apply instead of
    * the global <a href="#colors">colors</a> when <a href="#plotOptions.column.colorByPoint">colorByPoint</a> is true.
    */
  val colors: js.UndefOr[js.Array[String | js.Object]] = js.undefined

  /**
    * When the series contains less points than the crop threshold, all points are drawn,
    * event if the points fall outside the visible plot area at the current zoom.
    * The advantage of drawing all points (including markers and columns),
    * is that animation is performed on updates. On the other hand, when the series contains more points
    * than the crop threshold, the series data is cropped to only contain points that fall within the plot area.
    * The advantage of cropping away invisible points is to increase performance on large series.
    */
  val cropThreshold: js.UndefOr[Double] = js.undefined

  /**
    * The maximum allowed pixel width for a column, translated to the height of a bar in a bar chart.
    * This prevents the columns from becoming too wide when there is a small number of points in the chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/column-maxpointwidth-20/" target="_blank">Limited to 50</a>
    */
  val maxPointWidth: js.UndefOr[Double] = js.undefined

  /**
    * When a series contains a data array that is longer than this, only one dimensional arrays of numbers,
    * or two dimensional arrays with x and y values are allowed. Also, only the first
    * point is tested, and the rest are assumed to be the same format. This saves expensive
    * data checking and indexing in long series. Set it to <code>0</code> disable.
    */
  val turboThreshold: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class PieLikeSeries extends StandardSeries {
  /**
    * The color of the border surrounding each slice. When <code>null</code>, the border takes the same color as the slice fill.
    * This can be used together with a <code>borderWidth</code> to fill drawing gaps created by antialiazing artefacts in borderless pies.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-bordercolor-black/" target="_blank">Black border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * <p>The width of the border surrounding each slice.</p>
    *
    * <p>When setting the border width to 0, there may be small gaps between the slices due to SVG antialiasing artefacts.
    * To work around this, keep the border width at 0.5 or 1, but set the <code>borderColor</code> to <code>null</code> instead.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-borderwidth/" target="_blank">3px border</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * The center of the series. By default, it is centered in the middle of the plot area, so it fills the plot area height.
    */
  val center: js.UndefOr[js.Array[String | Double]] = js.undefined

  /**
    * A series specific or series type specific color set to use instead of the global <a href="#colors">colors</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/pie-monochrome/" target="_blank">Set default colors for all pies</a>
    */
  val colors: js.UndefOr[js.Array[String | js.Object]] = js.undefined

  /**
    * The thickness of a 3D pie. Requires <code>highcharts-3d.js</code>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/3d-pie/">Basic 3D pie chart</a>
    */
  val depth: js.UndefOr[Double] = js.undefined

  /**
    * The minimum size for a pie in response to auto margins.
    * The pie will try to shrink to make room for data labels in side the plot area, but only to this size.
    */
  val minSize: js.UndefOr[Double] = js.undefined

  /**
    * If a point is sliced, moved out from the center, how many pixels should  it be moved?.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/pie-slicedoffset-20/" target="_blank">20px offset</a>
    */
  val slicedOffset: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class CandleLikeSeries extends BoxSeries {
  /**
    * The width of the line surrounding the box.
    * If any of <a href="#plotOptions.boxplot.stemWidth">stemWidth</a>, <a href="#plotOptions.boxplot.medianWidth">medianWidth</a>
    * or <a href="#plotOptions.boxplot.whiskerWidth">whiskerWidth</a> are <code>null</code>, the lineWidth also applies to these lines.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">Box plot styling</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/error-bar-styling/" target="_blank">error bar styling</a>
    */
  val lineWidth: js.UndefOr[Double] = js.undefined

  /**
    * The color for the parts of the graph or points that are below the <a href="#plotOptions.series.threshold">threshold</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-negative-color/" target="_blank">Spline, area and column</a> - <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/arearange-negativecolor/" target="_blank">arearange</a>.
    */
  val negativeColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the stem, the vertical line extending from the box to the whiskers. If <code>null</code>, the series color is used.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">Box plot styling</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/error-bar-styling/" target="_blank">error bar styling</a>
    */
  val stemColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The dash style of the stem, the vertical line extending from the box to the whiskers.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">Box plot styling</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/error-bar-styling/" target="_blank">error bar styling</a>
    */
  val stemDashStyle: js.UndefOr[String] = js.undefined

  /**
    * The width of the stem, the vertical line extending from the box to the whiskers. If <code>null</code>,
    * the width is inherited from the <a href="#plotOptions.boxplot.lineWidth">lineWidth</a> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">Box plot styling</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/error-bar-styling/" target="_blank">error bar styling</a>
    */
  val stemWidth: js.UndefOr[Double] = js.undefined

  /**
    * When a series contains a data array that is longer than this, only one dimensional arrays of numbers,
    * or two dimensional arrays with x and y values are allowed. Also, only the first
    * point is tested, and the rest are assumed to be the same format. This saves expensive
    * data checking and indexing in long series. Set it to <code>0</code> disable.
    */
  val turboThreshold: js.UndefOr[Double] = js.undefined

  /**
    * The color of the whiskers, the horizontal lines marking low and high values.
    * When <code>null</code>, the general series color is used.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">Box plot styling</a>
    */
  val whiskerColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The length of the whiskers, the horizontal lines marking low and high values.
    * It can be a numerical pixel value, or a percentage value of the box width.
    * Set <code>0</code> to disable whiskers.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/box-plot-styling/" target="_blank">True by default</a>
    */
  val whiskerLength: js.UndefOr[Double | String] = js.undefined

  /**
    * The line width of the whiskers, the horizontal lines marking low and high values.
    * When <code>null</code>, the general <a href="#plotOptions.errorbar.lineWidth">lineWidth</a> applies.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/error-bar-styling/" target="_blank">Error bar styling</a>
    */
  val whiskerWidth: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
abstract class GaugeSeries extends Series {
  override type DataLabels = SeriesDataLabels

  /**
    * Allow the dial to overshoot the end of the perimeter axis by this many degrees. Say if the gauge axis goes from 0 to 60, a value of 100, or 1000, will show 5 degrees beyond the end of the axis.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/gauge-overshoot/" target="_blank">Allow 5 degrees overshoot</a>
    */
  val overshoot: js.UndefOr[Double] = js.undefined

  /**
    * The threshold, also called zero level or base level. For line type series this is only used in conjunction with <a href="#plotOptions.series.negativeColor">negativeColor</a>.
    */
  val threshold: js.UndefOr[Double] = js.undefined

  /**
    * When this option is <code>true</code>, the dial will wrap around the axes. For instance, in a full-range gauge going from 0 to 360, a value of 400 will point to 40. When <code>wrap</code> is <code>false</code>, the dial stops at 360.
    */
  val wrap: js.UndefOr[Boolean] = js.undefined

  /**
    * When using dual or multiple x axes, this number defines which xAxis the particular series is connected to. It refers to either the <a href="#xAxis.id">axis id</a> or the index of the axis in the xAxis array, with 0 being the first.
    */
  val xAxis: js.UndefOr[Double | String] = js.undefined

  /**
    * When using dual or multiple y axes, this number defines which yAxis the particular series is connected to. It refers to either the <a href="#yAxis.id">axis id</a> or the index of the axis in the yAxis array, with 0 being the first.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/yaxis/" target="_blank">Apply the column series to the secondary Y axis</a>
    */
  val yAxis: js.UndefOr[Double | String] = js.undefined
}