/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package tooltip

import io.udash.wrappers.highcharts.api.{Point, Series}
import io.udash.wrappers.highcharts.config.utils.{Color, DateTimeLabelFormats}

import scala.scalajs.js
import scala.scalajs.js.{ThisFunction, `|`}

trait Tooltip extends js.Object {

  /**
    * Enable or disable animation of the tooltip. In slow legacy IE browsers the animation is disabled by default.
    */
  val animation: js.UndefOr[Boolean] = js.undefined

  /**
    * The background color or gradient for the tooltip.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/backgroundcolor-solid/" target="_blank">Yellowish background</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/backgroundcolor-gradient/" target="_blank">gradient</a>
    */
  val backgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the tooltip border. When <code>null</code>, the border takes the color of the corresponding series or point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-default/" target="_blank">Follow series by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-black/" target="_blank">black border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The radius of the rounded border corners.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-default/" target="_blank">5px by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/borderradius-0/" target="_blank">square borders</a>
    */
  val borderRadius: js.UndefOr[Double] = js.undefined

  /**
    * The pixel width of the tooltip border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-default/" target="_blank">2px by default,</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/borderwidth/" target="_blank">no border (shadow only)</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * Since 4.1, the crosshair definitions are moved to the Axis object in order for a better separation from the tooltip.
    * See <a href="#xAxis.crosshair">xAxis.crosshair<a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/crosshairs-x/" target="_blank">Enable a crosshair for the x value</a>.
    */
  @deprecated("The crosshair definitions were moved to the Axis object in order for a better separation from the tooltip.", "0.5.0")
  val crosshairs: js.UndefOr[js.Any] = js.undefined

  /**
    * <p>For series on a datetime axes, the date format in the tooltip's header will by default be guessed based on
    * the closest data points. This member gives the default string representations used for each unit. For an overview
    * of the replacement codes, see <a href="#Highcharts.dateFormat">dateFormat</a>.</p>
    *
    * <p>Defaults to:
    * <pre>{
    * millisecond:"%A, %b %e, %H:%M:%S.%L",
    * second:"%A, %b %e, %H:%M:%S",
    * minute:"%A, %b %e, %H:%M",
    * hour:"%A, %b %e, %H:%M",
    * day:"%A, %b %e, %Y",
    * week:"Week from %A, %b %e, %Y",
    * month:"%B %Y",
    * year:"%Y"
    * }</pre>
    * </p>
    */
  val dateTimeLabelFormats: js.UndefOr[js.Dictionary[String]] = js.undefined

  /**
    * Enable or disable the tooltip.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/enabled/" target="_blank">Disabled</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-point-events-mouseover/" target="_blank">disable tooltip and show values on chart instead</a>.
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>Whether the tooltip should follow the mouse as it moves across columns, pie slices and other point types with an extent.
    * By default it behaves this way for scatter, bubble and pie series by override in the <code>plotOptions</code> for those series types. </p>
    * <p>For touch moves to behave the same way, <a href="#tooltip.followTouchMove">followTouchMove</a> must be <code>true</code> also.</p>
    */
  val followPointer: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether the tooltip should follow the finger as it moves on a touch device. If <a href="#chart.zoomType">chart.zoomType</a> is set, it will override <code>followTouchMove</code>.
    */
  val followTouchMove: js.UndefOr[Boolean] = js.undefined

  /**
    * A string to append to the tooltip format.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/footerformat/" target="_blank">A table for value alignment</a>
    */
  val footerFormat: js.UndefOr[String] = js.undefined

  /**
    * <p>Callback function to format the text of the tooltip. Return false to disable tooltip for a specific point on series.</p>
    * <p>A subset of HTML is supported. The HTML of the tooltip is parsed and converted to SVG,  therefore this isn't a complete HTML renderer.
    * The following tabs are supported:  <code>&lt;b&gt;</code>, <code>&lt;strong&gt;</code>, <code>&lt;i&gt;</code>, <code>&lt;em&gt;</code>,
    * <code>&lt;br/&gt;</code>, <code>&lt;span&gt;</code>. Spans can be styled with a <code>style</code> attribute, but only text-related CSS that is
    * shared with SVG is handled. </p> <p>Since version 2.1 the tooltip can be shared between multiple series through  the <code>shared</code> option.
    * The available data in the formatter differ a bit depending on whether the tooltip is shared or not. In a shared tooltip, all  properties except <code>x</code>,
    * which is common for all points, are kept in  an array, <code>this.points</code>.</p>  <p>Available data are:</p> <dl>
    *   <dt>this.percentage (not shared) / this.points[i].percentage (shared)</dt> 	<dd>Stacked series and pies only. The point's percentage of the total.</dd>
    *   <dt>this.point (not shared) / this.points[i].point (shared)</dt> 	<dd>The point object. The point name, if defined, is available  through <code>this.point.name</code>.</dd>
    *   <dt>this.points</dt> 	<dd>In a shared tooltip, this is an array containing all other properties for each point.</dd>
    *   <dt>this.series (not shared) / this.points[i].series (shared)</dt> 	<dd>The series object. The series name is available  through <code>this.series.name</code>.</dd>
    *   <dt>this.total (not shared) / this.points[i].total (shared)</dt> 	<dd>Stacked series only. The total value at this point's x value.</dd> 	 	<dt>this.x</dt>
    *   <dd>The x value. This property is the same regardless of the tooltip being shared or not.</dd> 	 	<dt>this.y (not shared) / this.points[i].y (shared)</dt>
    *   <dd>The y value.</dd>
    * </dl>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/formatter-simple/" target="_blank">Simple string formatting</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/formatter-shared/" target="_blank">formatting with shared tooltip</a>
    */
  val formatter: js.UndefOr[js.ThisFunction0[Tooltip.FormatterData, String | Boolean]] = js.undefined

  /**
    * <p>The HTML of the tooltip header line. Variables are enclosed by curly brackets. Available variables
    * are <code>point.key</code>, <code>series.name</code>, <code>series.color</code> and other members from
    * the <code>point</code> and <code>series</code> objects. The <code>point.key</code> variable contains the category name,
    * x value or datetime string depending on the type of axis. For datetime axes, the <code>point.key</code> date format can be set using tooltip.xDateFormat.</p>
    *
    * <p>Defaults to <code>&lt;span style="font-size: 10px"&gt;{point.key}&lt;/span&gt;&lt;br/&gt;</code></p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/footerformat/" target="_blank">A HTML table in the tooltip</a>
    */
  val headerFormat: js.UndefOr[String] = js.undefined

  /**
    * The number of milliseconds to wait until the tooltip is hidden when mouse out from a point or chart. 
    */
  val hideDelay: js.UndefOr[Double] = js.undefined

  /**
    * Padding inside the tooltip, in pixels.
    */
  val padding: js.UndefOr[Double] = js.undefined

  /**
    * <p>The HTML of the point's line in the tooltip. Variables are enclosed by curly brackets. Available variables
    * are point.x, point.y, series.name and series.color and other properties on the same form. Furthermore,  point.y
    * can be extended by the <code>tooltip.valuePrefix</code> and <code>tooltip.valueSuffix</code> variables.
    * This can also be overridden for each series, which makes it a good hook for displaying units.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/pointformat/" target="_blank">A different point format with value suffix</a>
    */
  val pointFormat: js.UndefOr[String] = js.undefined

  /**
    * A callback function for formatting the HTML output for a single point in the tooltip. Like the <code>pointFormat</code> string, but with more flexibility.
    */
  val pointFormatter: js.UndefOr[js.ThisFunction0[Point, String | Boolean]] = js.undefined

  /**
    * <p>A callback function to place the tooltip in a default position. The callback receives three parameters:
    * <code>labelWidth</code>, <code>labelHeight</code> and <code>point</code>, where point contains values for
    * <code>plotX</code> and <code>plotY</code> telling where the reference point is in the plot area.
    * Add <code>chart.plotLeft</code> and <code>chart.plotTop</code> to get the full coordinates.</p>
    *
    * <p>The return should be an object containing x and y values, for example <code>{ x: 100, y: 100 }</code>.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/positioner/" target="_blank">
    *          A fixed tooltip position</a>
    */
  val positioner: js.UndefOr[js.Function3[Double, Double, Point, Tooltip.Coords]] = js.undefined

  /**
    * Whether to apply a drop shadow to the tooltip.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-default/" target="_blank">True by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/shadow/" target="_blank">false</a>
    */
  val shadow: js.UndefOr[Boolean] = js.undefined

  /**
    * The name of a symbol to use for the border around the tooltip. In Highcharts 3.x and less, the shape was <code>square</code>. 
    */
  val shape: js.UndefOr[String] = js.undefined

  /**
    * <p>When the tooltip is shared, the entire plot area will capture mouse movement or touch events. Tooltip texts for series types with ordered data (not pie, scatter, flags etc) will be shown in a single bubble. This is recommended for single series charts and for tablet/mobile optimized charts.</p>
    *
    * <p>See also the experimental implementation for <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/studies/tooltip-split/">tooltip.split</a>, that is better suited for charts with many series, especially line-type series.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/shared-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/shared-true/" target="_blank">true</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/shared-x-crosshair/" target="_blank">true with x axis crosshair</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/shared-true-mixed-types/" target="_blank">true with mixed series types</a>.
    */
  val shared: js.UndefOr[Boolean] = js.undefined

  /**
    * Proximity snap for graphs or single points. Does not apply to bars, columns and pie slices. It defaults to 10 for mouse-powered devices and 25 for touch  devices.
    * Note that since Highcharts 4.1 the whole plot area by default captures pointer events in order to show the tooltip, so for tooltip.snap to make sense,
    * <a href="#plotOptions.series.stickyTracking">stickyTracking</a> must be <code>false</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/bordercolor-default/" target="_blank">10 px by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/snap-50/" target="_blank">50 px on graph</a>
    */
  val snap: js.UndefOr[Double] = js.undefined

  /**
    * Split the tooltip into one label per series, with the header close to the axis. This is recommended over
    * <a href="#tooltip.shared">shared</a> tooltips for charts with multiple line series, generally making them easier to read.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/split/" target="_blank">Split tooltip</a>
    */
  val split: js.UndefOr[Boolean] = js.undefined

  /**
    * CSS styles for the tooltip. The tooltip can also be styled through the CSS
    * class <code>.highcharts-tooltip</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/style/" target="_blank">Greater padding, bold text</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * Use HTML to render the contents of the tooltip instead of SVG. Using HTML allows advanced formatting like tables and images in the tooltip.
    * It is also recommended for rtl languages as it works around rtl bugs in early Firefox.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/footerformat/" target="_blank">A table for value alignment</a>. <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/fullhtml/" target="_blank">Full HTML tooltip</a>.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * How many decimals to show in each series' y value. This is overridable in each series' tooltip options object. The default is to preserve all decimals.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/valuedecimals/" target="_blank">Set decimals, prefix and suffix for the value</a>
    */
  val valueDecimals: js.UndefOr[Double] = js.undefined

  /**
    * A string to prepend to each series' y value. Overridable in each series' tooltip options object.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/valuedecimals/" target="_blank">Set decimals, prefix and suffix for the value</a>
    */
  val valuePrefix: js.UndefOr[String] = js.undefined

  /**
    * A string to append to each series' y value. Overridable in each series' tooltip options object.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/valuedecimals/" target="_blank">Set decimals, prefix and suffix for the value</a>
    */
  val valueSuffix: js.UndefOr[String] = js.undefined

  /**
    * The format for the date in the tooltip header if the X axis is a datetime axis.
    * The default is a best guess based on the smallest distance between points in the chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/tooltip/xdateformat/" target="_blank">A different format</a>
    */
  val xDateFormat: js.UndefOr[String] = js.undefined
}

object Tooltip {
  import scala.scalajs.js.JSConverters._

  @js.native
  trait FormatterData extends js.Object {
    /** Stacked series and pies only. The point's percentage of the total. */
    val percentage: Double = js.native
    /** The point object. The point name, if defined, is available through this.point.name. */
    val point: Point = js.native
    /** In a shared tooltip, this is an array containing all other properties for each point. */
    val points: js.Array[Point] = js.native
    /** The series object. The series name is available through this.series.name. */
    val series: Series = js.native
    /** Stacked series only. The total value at this point's x value. */
    val total: Double = js.native
    /** The x value. This property is the same regardless of the tooltip being shared or not. */
    val x: js.Any = js.native
    /** The y value. */
    val y: js.Any = js.native
  }

  class Coords(val x: Int, val y: Int) extends js.Object

  /**
    * @param animation            Enable or disable animation of the tooltip. In slow legacy IE browsers the animation is disabled by default.
    * @param backgroundColor      The background color or gradient for the tooltip.
    * @param borderColor          The color of the tooltip border. When <code>null</code>, the border takes the color of the corresponding series or point.
    * @param borderRadius         The radius of the rounded border corners.
    * @param borderWidth          The pixel width of the tooltip border.
    * @param dateTimeLabelFormats <p>For series on a datetime axes, the date format in the tooltip's header will by default be guessed based on the closest data points. This member gives the default string representations used for each unit. For an overview of the replacement codes, see <a href="#Highcharts.dateFormat">dateFormat</a>.</p>. . <p>Defaults to:. <pre>{.     millisecond:"%A, %b %e, %H:%M:%S.%L",.     second:"%A, %b %e, %H:%M:%S",.     minute:"%A, %b %e, %H:%M",.     hour:"%A, %b %e, %H:%M",.     day:"%A, %b %e, %Y",.     week:"Week from %A, %b %e, %Y",.     month:"%B %Y",.     year:"%Y". }</pre>. </p>
    * @param enabled              Enable or disable the tooltip.
    * @param followPointer        <p>Whether the tooltip should follow the mouse as it moves across columns, pie slices and other point types with an extent. By default it behaves this way for scatter, bubble and pie series by override in the <code>plotOptions</code> for those series types. </p>. <p>For touch moves to behave the same way, <a href="#tooltip.followTouchMove">followTouchMove</a> must be <code>true</code> also.</p>
    * @param followTouchMove      Whether the tooltip should follow the finger as it moves on a touch device. If <a href="#chart.zoomType">chart.zoomType</a> is set, it will override <code>followTouchMove</code>.
    * @param footerFormat         A string to append to the tooltip format.
    * @param formatter            <p>Callback function to format the text of the tooltip. Return false to disable tooltip for a specific point on series.</p> <p>A subset of HTML is supported. The HTML of the tooltip is parsed and converted to SVG,  therefore this isn't a complete HTML renderer. The following tabs are supported:  <code>&lt;b&gt;</code>, <code>&lt;strong&gt;</code>, <code>&lt;i&gt;</code>, <code>&lt;em&gt;</code>, <code>&lt;br/&gt;</code>, <code>&lt;span&gt;</code>. Spans can be styled with a <code>style</code> attribute, but only text-related CSS that is  shared with SVG is handled. </p> <p>Since version 2.1 the tooltip can be shared between multiple series through  the <code>shared</code> option. The available data in the formatter differ a bit depending on whether the tooltip is shared or not. In a shared tooltip, all  properties except <code>x</code>, which is common for all points, are kept in  an array, <code>this.points</code>.</p>  <p>Available data are:</p> <dl> 	<dt>this.percentage (not shared) / this.points[i].percentage (shared)</dt> 	<dd>Stacked series and pies only. The point's percentage of the total.</dd> 	 	<dt>this.point (not shared) / this.points[i].point (shared)</dt> 	<dd>The point object. The point name, if defined, is available  through <code>this.point.name</code>.</dd> 	 	<dt>this.points</dt> 	<dd>In a shared tooltip, this is an array containing all other properties for each point.</dd> 	 	<dt>this.series (not shared) / this.points[i].series (shared)</dt> 	<dd>The series object. The series name is available  through <code>this.series.name</code>.</dd>  	<dt>this.total (not shared) / this.points[i].total (shared)</dt> 	<dd>Stacked series only. The total value at this point's x value.</dd> 	 	<dt>this.x</dt> 	<dd>The x value. This property is the same regardless of the tooltip being shared or not.</dd> 	 	<dt>this.y (not shared) / this.points[i].y (shared)</dt> 	<dd>The y value.</dd>  </dl>
    * @param headerFormat         <p>The HTML of the tooltip header line. Variables are enclosed by curly brackets. Available variables			are <code>point.key</code>, <code>series.name</code>, <code>series.color</code> and other members from the <code>point</code> and <code>series</code> objects. The <code>point.key</code> variable contains the category name, x value or datetime string depending on the type of axis. For datetime axes, the <code>point.key</code> date format can be set using tooltip.xDateFormat.</p>.  . <p>Defaults to <code>&lt;span style="font-size: 10px"&gt;{point.key}&lt;/span&gt;&lt;br/&gt;</code></p>
    * @param hideDelay            The number of milliseconds to wait until the tooltip is hidden when mouse out from a point or chart.
    * @param padding              Padding inside the tooltip, in pixels.
    * @param pointFormat          <p>The HTML of the point's line in the tooltip. Variables are enclosed by curly brackets. Available variables are point.x, point.y, series.name and series.color and other properties on the same form. Furthermore,  point.y can be extended by the <code>tooltip.valuePrefix</code> and <code>tooltip.valueSuffix</code> variables. This can also be overridden for each series, which makes it a good hook for displaying units.</p>
    * @param pointFormatter       A callback function for formatting the HTML output for a single point in the tooltip. Like the <code>pointFormat</code> string, but with more flexibility.
    * @param positioner           <p>A callback function to place the tooltip in a default position. The callback receives three parameters: <code>labelWidth</code>, <code>labelHeight</code> and <code>point</code>, where point contains values for <code>plotX</code> and <code>plotY</code> telling where the reference point is in the plot area. Add <code>chart.plotLeft</code> and <code>chart.plotTop</code> to get the full coordinates.</p>. . <p>The return should be an object containing x and y values, for example <code>{ x: 100, y: 100 }</code>.</p>
    * @param split                Split the tooltip into one label per series, with the header close to the axis.
    * @param shadow               Whether to apply a drop shadow to the tooltip.
    * @param shape                The name of a symbol to use for the border around the tooltip. In Highcharts 3.x and less, the shape was <code>square</code>.
    * @param shared               <p>When the tooltip is shared, the entire plot area will capture mouse movement or touch events. Tooltip texts for series types with ordered data (not pie, scatter, flags etc) will be shown in a single bubble. This is recommended for single series charts and for tablet/mobile optimized charts.</p>. . <p>See also the experimental implementation for <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/studies/tooltip-split/">tooltip.split</a>, that is better suited for charts with many series, especially line-type series.</p>
    * @param snap                 Proximity snap for graphs or single points. Does not apply to bars, columns and pie slices. It defaults to 10 for mouse-powered devices and 25 for touch  devices. Note that since Highcharts 4.1 the whole plot area by default captures pointer events in order to show the tooltip, so for tooltip.snap to make sense, <a href="#plotOptions.series.stickyTracking">stickyTracking</a> must be <code>false</code>.
    * @param style                CSS styles for the tooltip. The tooltip can also be styled through the CSS.  class <code>.highcharts-tooltip</code>.
    * @param useHTML              Use HTML to render the contents of the tooltip instead of SVG. Using HTML allows advanced formatting like tables and images in the tooltip. It is also recommended for rtl languages as it works around rtl bugs in early Firefox.
    * @param valueDecimals        How many decimals to show in each series' y value. This is overridable in each series' tooltip options object. The default is to preserve all decimals.
    * @param valuePrefix          A string to prepend to each series' y value. Overridable in each series' tooltip options object.
    * @param valueSuffix          A string to append to each series' y value. Overridable in each series' tooltip options object.
    * @param xDateFormat          The format for the date in the tooltip header if the X axis is a datetime axis. The default is a best guess based on the smallest distance between points in the chart.
    */
  def apply(animation: js.UndefOr[Boolean] = js.undefined,
            backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            dateTimeLabelFormats: js.UndefOr[DateTimeLabelFormats] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            followPointer: js.UndefOr[Boolean] = js.undefined,
            followTouchMove: js.UndefOr[Boolean] = js.undefined,
            footerFormat: js.UndefOr[String] = js.undefined,
            formatter: js.UndefOr[(Tooltip.FormatterData) => String | Boolean] = js.undefined,
            headerFormat: js.UndefOr[String] = js.undefined,
            hideDelay: js.UndefOr[Double] = js.undefined,
            padding: js.UndefOr[Double] = js.undefined,
            pointFormat: js.UndefOr[String] = js.undefined,
            pointFormatter: js.UndefOr[(Point) => String | Boolean] = js.undefined,
            positioner: js.UndefOr[(Double, Double, Point) => Tooltip.Coords] = js.undefined,
            shadow: js.UndefOr[Boolean] = js.undefined,
            shape: js.UndefOr[String] = js.undefined,
            shared: js.UndefOr[Boolean] = js.undefined,
            snap: js.UndefOr[Double] = js.undefined,
            split: js.UndefOr[Boolean] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined,
            valueDecimals: js.UndefOr[Double] = js.undefined,
            valuePrefix: js.UndefOr[String] = js.undefined,
            valueSuffix: js.UndefOr[String] = js.undefined,
            xDateFormat: js.UndefOr[String] = js.undefined): Tooltip = {
    val animationOuter = animation
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val dateTimeLabelFormatsOuter = dateTimeLabelFormats.map(DateTimeLabelFormats.toJSDict)
    val enabledOuter = enabled
    val followPointerOuter = followPointer
    val followTouchMoveOuter = followTouchMove
    val footerFormatOuter = footerFormat
    val formatterOuter = formatter.map(ThisFunction.fromFunction1[Tooltip.FormatterData, String | Boolean])
    val headerFormatOuter = headerFormat
    val hideDelayOuter = hideDelay
    val paddingOuter = padding
    val pointFormatOuter = pointFormat
    val pointFormatterOuter = pointFormatter.map(ThisFunction.fromFunction1[Point, String | Boolean])
    val positionerOuter = positioner.map(js.Any.fromFunction3[Double, Double, Point, Tooltip.Coords])
    val shadowOuter = shadow
    val shapeOuter = shape
    val sharedOuter = shared
    val snapOuter = snap
    val splitOuter = split
    val styleOuter = style.map(stringToStyleObject)
    val useHTMLOuter = useHTML
    val valueDecimalsOuter = valueDecimals
    val valuePrefixOuter = valuePrefix
    val valueSuffixOuter = valueSuffix
    val xDateFormatOuter = xDateFormat

    new Tooltip {
      override val animation = animationOuter
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val dateTimeLabelFormats = dateTimeLabelFormatsOuter
      override val enabled = enabledOuter
      override val followPointer = followPointerOuter
      override val followTouchMove = followTouchMoveOuter
      override val footerFormat = footerFormatOuter
      override val formatter = formatterOuter
      override val headerFormat = headerFormatOuter
      override val hideDelay = hideDelayOuter
      override val padding = paddingOuter
      override val pointFormat = pointFormatOuter
      override val pointFormatter = pointFormatterOuter
      override val positioner = positionerOuter
      override val shadow = shadowOuter
      override val shape = shapeOuter
      override val shared = sharedOuter
      override val snap = snapOuter
      override val split = splitOuter
      override val style = styleOuter
      override val useHTML = useHTMLOuter
      override val valueDecimals = valueDecimalsOuter
      override val valuePrefix = valuePrefixOuter
      override val valueSuffix = valueSuffixOuter
      override val xDateFormat = xDateFormatOuter
    }
  }
}
