/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package chart

import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.annotation.ScalaJSDefined
trait Chart extends js.Object {

  /**
    * <p>When using multiple axis, the ticks of two or more opposite axes will  automatically be aligned by adding
    * ticks to the axis or axes with the least ticks, as if <code>tickAmount</code> were specified.</p>
    *
    * <p>This can be prevented by setting <code>alignTicks</code> to false. If the grid lines look messy,
    * it's a good idea to hide them for the secondary axis by setting <code>gridLineWidth</code> to 0.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/alignticks-true/" target="_blank">True by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/alignticks-false/" target="_blank">false</a>.
    */
  val alignTicks: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>Set the overall animation for all chart updating. Animation can be disabled throughout
    * the chart by setting it to false here. It can be overridden for each individual
    * API method as a function parameter. The only animation not affected by this option is the
    * initial series animation, see <a class="internal" href="#plotOptions.series.animation">plotOptions.series.animation</a>.</p>
    *
    * <p>The animation can either be set as a boolean or a configuration object. If <code>true</code>,
    * it will use the 'swing' jQuery easing and a duration of 500 ms. If used as a configuration object,
    * the following properties are supported:
    * </p><dl>
    * <dt>duration</dt>
    * <dd>The duration of the animation in milliseconds.</dd>
    *
    * <dt>easing</dt>
    * <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>
    * </dl>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/animation-none/" target="_blank">Updating with no animation</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/animation-duration/" target="_blank">with a longer duration</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/animation-easing/" target="_blank">with a jQuery UI easing</a>.
    */
  val animation: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * The background color or gradient for the outer chart area.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/backgroundcolor-color/" target="_blank">Color</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/backgroundcolor-gradient/" target="_blank">gradient</a>
    */
  val backgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The color of the outer chart border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/bordercolor/" target="_blank">Brown border</a>
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The corner radius of the outer chart border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/borderradius/" target="_blank">20px radius</a>
    */
  val borderRadius: js.UndefOr[Double] = js.undefined

  /**
    * The pixel width of the outer chart border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/borderwidth/" target="_blank">5px border</a>
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * A CSS class name to apply to the charts container <code>div</code>, allowing unique CSS styling for each chart.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * In styled mode, this sets how many colors the class names should rotate between.
    */
  val colorCount: js.UndefOr[Int] = js.undefined

  /**
    * Alias of <code>type</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/defaultseriestype/" target="_blank">Bar</a>
    */
  @deprecated("Alias of `type`.", "0.5.0")
  val defaultSeriesType: js.UndefOr[String] = js.undefined

  /**
    * A text description of the chart. If the Accessibility module is loaded, this is included by default
    * as a long description of the chart and its contents in the hidden screen reader information region.
    */
  val description: js.UndefOr[String] = js.undefined

  /**
    * Event listeners for the chart.
    */
  val events: js.UndefOr[ChartEvents] = js.undefined

  /**
    * An explicit height for the chart. By default (when <code>null</code>) the height is calculated from the offset height of the containing element,
    * or 400 pixels if the containing element's height is 0.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/height/" target="_blank">500px height</a>
    */
  val height: js.UndefOr[Double] = js.undefined

  /**
    * If true, the axes will scale to the remaining visible series once one series is hidden. If false, hiding and showing
    * a series will not affect the axes or the other series. For stacks, once one series within the stack is hidden,
    * the rest of the stack will close in around it even if the axis is not affected.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/ignorehiddenseries-true/" target="_blank">True by default</a>
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/ignorehiddenseries-false/" target="_blank">false</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/ignorehiddenseries-true-stacked/" target="_blank">true with stack</a>
    */
  val ignoreHiddenSeries: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>Whether to invert the axes so that the x axis is vertical and y axis is horizontal. When true, the x axis
    * is <a href="#xAxis.reversed">reversed</a> by default. If a bar series is present in the chart, it will be inverted automatically.</p>
    *
    * <p>Inverting the chart doesn't have an effect if there are no cartesian series in the chart, or if the chart is <a href="#chart.polar">polar</a>.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/inverted/" target="_blank">Inverted line</a>
    */
  val inverted: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>The margin between the outer edge of the chart and the plot area. The numbers in the array designate
    * top, right, bottom and left respectively. Use the options <code>marginTop</code>,
    * <code>marginRight</code>, <code>marginBottom</code> and <code>marginLeft</code> for shorthand setting of one option.</p>
    * <p>Since version 2.1, the margin is 0 by default. The actual space is dynamically calculated  from the offset of axis labels,
    * axis title, title, subtitle and legend in addition to the <code>spacingTop</code>, <code>spacingRight</code>,
    * <code>spacingBottom</code> and <code>spacingLeft</code> options.</p>
    * Defaults to <code>[null]</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/margins-zero/" target="_blank">Zero margins</a>
    */
  val margin: js.UndefOr[js.Array[Double]] = js.undefined

  /**
    * The margin between the bottom outer edge of the chart and the plot area. Use this to set a fixed
    * pixel value for the margin as opposed to the default dynamic margin. See also <code>spacingBottom</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/marginbottom/" target="_blank">100px bottom margin</a>
    */
  val marginBottom: js.UndefOr[Double] = js.undefined

  /**
    * The margin between the left outer edge of the chart and the plot area. Use this to set a fixed
    * pixel value for the margin as opposed to the default dynamic margin. See also <code>spacingLeft</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/marginleft/" target="_blank">150px left margin</a>
    */
  val marginLeft: js.UndefOr[Double] = js.undefined

  /**
    * The margin between the right outer edge of the chart and the plot area. Use this to set a fixed
    * pixel value for the margin as opposed to the default dynamic margin. See also <code>spacingRight</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/marginright/" target="_blank">100px right margin</a>
    */
  val marginRight: js.UndefOr[Double] = js.undefined

  /**
    * The margin between the top outer edge of the chart and the plot area. Use this to set a fixed pixel value
    * for the margin as opposed to the default dynamic margin. See also <code>spacingTop</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/margintop/" target="_blank">100px top margin</a>
    */
  val marginTop: js.UndefOr[Double] = js.undefined

  /**
    * Options to render charts in 3 dimensions. This feature requires <code>highcharts-3d.js</code>, found in the download package or online at <a href="http://code.highcharts.com/highcharts-3d.js">code.highcharts.com/highcharts-3d.js</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/3d/column/">Basic 3D columns</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/3d-pie/">basic 3D pie</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/3d/scatter/">basic 3D scatter chart</a>.
    */
  val options3d: js.UndefOr[ChartOptions3d] = js.undefined

  /**
    * Allows setting a key to switch between zooming and panning.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/pankey/" target="_blank">Hold down <em>shift</em> to pan</a>
    */
  val panKey: js.UndefOr[String] = js.undefined

  /**
    * Allow panning in a chart. Best used with <a href="#chart.panKey">panKey</a> to combine zooming and panning.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/pankey/" target="_blank">Zooming and panning</a>
    */
  val panning: js.UndefOr[Boolean] = js.undefined

  /**
    * Equivalent to <a href="#chart.zoomType">zoomType</a>, but for multitouch gestures only. By default, the <code>pinchType</code> is the same as the <code>zoomType</code> setting. However, pinching can be enabled separately in some cases, for example in stock charts where a mouse drag pans the chart, while pinching is enabled.
    */
  val pinchType: js.UndefOr[String] = js.undefined

  /**
    * The background color or gradient for the plot area.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotbackgroundcolor-color/" target="_blank">Color</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotbackgroundcolor-gradient/" target="_blank">gradient</a>
    */
  val plotBackgroundColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The URL for an image to use as the plot background. To set an image as the background for the entire chart,
    * set a CSS background image to the container element. Note that for the image to be applied to exported charts,
    * its URL needs to be accessible by the export server.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotbackgroundimage/" target="_blank">Skies</a>
    */
  val plotBackgroundImage: js.UndefOr[String] = js.undefined

  /**
    * The color of the inner chart or plot area border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotbordercolor/" target="_blank">Blue border</a>
    */
  val plotBorderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The pixel width of the plot area border.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotborderwidth/" target="_blank">1px border</a>
    */
  val plotBorderWidth: js.UndefOr[Double] = js.undefined

  /**
    * Whether to apply a drop shadow to the plot area. Requires that plotBackgroundColor be set.
    * Since 2.3 the shadow can be an object configuration containing <code>color</code>,
    * <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/plotshadow/" target="_blank">Plot shadow</a>
    */
  val plotShadow: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * When true, cartesian charts like line, spline, area and column are transformed into the polar coordinate system. Requires <code>highcharts-more.js</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/polar-line/">Polar line</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/polar-area/">polar area</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/alignticks-true/">polar column</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/polar/">combined series types</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/polar-spider/">spider chart</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/polar-wind-rose/">wind rose</a>.
    */
  val polar: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether to reflow the chart to fit the width of the container div on resizing the window.
    *
    * @example Move the bar between the JavaScript frame and the Preview frame to see the effect: 
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/reflow-true/" target="_blank">True by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/reflow-false/" target="_blank">false</a>.
    */
  val reflow: js.UndefOr[Boolean] = js.undefined

  /**
    * The HTML element where the chart will be rendered. If it is a string, the element by that id is used. The HTML element can also be passed by direct reference.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/reflow-true/" target="_blank">String</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/renderto-object/" target="_blank">object reference</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/renderto-jquery/" target="_blank">object reference through jQuery</a>,
    */
  val renderTo: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The button that appears after a selection zoom, allowing the user to reset zoom.
    */
  val resetZoomButton: js.UndefOr[ChartResetZoomButton] = js.undefined

  /**
    * The background color of the marker square when selecting (zooming in on) an area of the chart.
    */
  val selectionMarkerFill: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Whether to apply a drop shadow to the outer chart area. Requires that 
    * backgroundColor be set. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/shadow/" target="_blank">Shadow</a>
    */
  val shadow: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * Whether to show the axes initially. This only applies to empty charts where series are added dynamically, as axes are automatically added to cartesian series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/showaxes-false/" target="_blank">False by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/showaxes-true/" target="_blank">true</a>
    */
  val showAxes: js.UndefOr[Boolean] = js.undefined

  /**
    * The distance between the outer edge of the chart and the content, like title, legend, axis title or labels.
    * The numbers in the array designate top, right, bottom and left respectively.
    * Use the options spacingTop, spacingRight, spacingBottom and spacingLeft options for shorthand setting of one option.
    */
  val spacing: js.UndefOr[js.Array[Double]] = js.undefined

  /**
    * <p>The space between the bottom edge of the chart and the content (plot area, axis title and labels, title, subtitle or 
    * legend in top position).</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingbottom/" target="_blank">Spacing bottom set to 100</a>.
    */
  val spacingBottom: js.UndefOr[Double] = js.undefined

  /**
    * <p>The space between the left edge of the chart and the content (plot area, axis title and labels, title, subtitle or 
    * legend in top position).</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingleft/" target="_blank">Spacing left set to 100</a>
    */
  val spacingLeft: js.UndefOr[Double] = js.undefined

  /**
    * <p>The space between the right edge of the chart and the content (plot area, axis title and labels, title, subtitle or 
    * legend in top position).</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingright-100/" target="_blank">Spacing set to 100</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingright-legend/" target="_blank">legend in right position with default spacing</a>
    */
  val spacingRight: js.UndefOr[Double] = js.undefined

  /**
    * <p>The space between the top edge of the chart and the content (plot area, axis title and labels, title, subtitle or 
    * legend in top position).</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingtop-100/" target="_blank">A top spacing of 100</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/spacingtop-10/" target="_blank">floating chart title makes the plot area align to the
    *          default spacingTop of 10.</a>.
    */
  val spacingTop: js.UndefOr[Double] = js.undefined

  /**
    * Additional CSS styles to apply inline to the container <code>div</code>. Note that since the default font styles are applied in the renderer, it is ignorant of the individual chart  options and must be set globally. Defaults to:
    * <pre>style: {
    * fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif', // default font
    * fontSize: '12px'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/style-serif-font/" target="_blank">Using a serif type font</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The default series type for the chart. Can be any of the chart types listed under <a href="#plotOptions">plotOptions</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/type-bar/" target="_blank">Bar</a>
    */
  val `type`: js.UndefOr[String] = js.undefined

  /**
    * A text description of the chart type. If the Accessibility module is loaded,
    * this will be included in the description of the chart in the screen reader information region.
    */
  val typeDescription: js.UndefOr[String] = js.undefined

  /**
    * An explicit width for the chart. By default (when <code>null</code>) the width is calculated from the offset width of the containing element.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/width/" target="_blank">800px wide</a>
    */
  val width: js.UndefOr[Double] = js.undefined

  /**
    * Decides in what dimensions the user can zoom by dragging the mouse. Can be one of <code>x</code>, <code>y</code> or <code>xy</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/zoomtype-none/" target="_blank">None by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/zoomtype-x/" target="_blank">x</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/zoomtype-y/" target="_blank">y</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/zoomtype-xy/" target="_blank">xy</a>
    */
  val zoomType: js.UndefOr[String] = js.undefined
}

object Chart {
  import scala.scalajs.js.JSConverters._

  final class ZoomType(val value: String) extends AnyVal
  object ZoomType {
    val X = new ZoomType("x")
    val Y = new ZoomType("y")
    val XY = new ZoomType("xy")
  }
  /**
    * @param alignTicks          <p>When using multiple axis, the ticks of two or more opposite axes will  automatically be aligned by adding ticks to the axis or axes with the least ticks, as if <code>tickAmount</code> were specified.</p>. . <p>This can be prevented by setting <code>alignTicks</code> to false. If the grid lines look messy, it's a good idea to hide them for the secondary axis by setting <code>gridLineWidth</code> to 0.</p>
    * @param animation           <p>Set the overall animation for all chart updating. Animation can be disabled throughout.  the chart by setting it to false here. It can be overridden for each individual.  API method as a function parameter. The only animation not affected by this option is the .  initial series animation, see <a class="internal" href="#plotOptions.series.animation">plotOptions.series.animation</a>.</p>.  .  <p>The animation can either be set as a boolean or a configuration object. If <code>true</code>,.  it will use the 'swing' jQuery easing and a duration of 500 ms. If used as a configuration object,.  the following properties are supported: .  </p><dl>.  	<dt>duration</dt>.  	<dd>The duration of the animation in milliseconds.</dd>.  	.  	<dt>easing</dt>.  	<dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>.  </dl>
    * @param backgroundColor     The background color or gradient for the outer chart area.
    * @param borderColor         The color of the outer chart border.
    * @param borderRadius        The corner radius of the outer chart border.
    * @param borderWidth         The pixel width of the outer chart border.
    * @param className           A CSS class name to apply to the charts container <code>div</code>, allowing unique CSS styling for each chart.
    * @param colorCount          In styled mode, this sets how many colors the class names should rotate between.
    * @param description         A text description of the chart. f the Accessibility module is loaded, this is included by default as a long description of the chart and its contents in the hidden screen reader information region.
    * @param events              Event listeners for the chart.
    * @param height              An explicit height for the chart. By default (when <code>null</code>) the height is calculated from the offset height of the containing element, or 400 pixels if the containing element's height is 0.
    * @param ignoreHiddenSeries  If true, the axes will scale to the remaining visible series once one series is hidden. If false, hiding and showing a series will not affect the axes or the other series. For stacks, once one series within the stack is hidden, the rest of the stack will close in around it even if the axis is not affected.
    * @param inverted            <p>Whether to invert the axes so that the x axis is vertical and y axis is horizontal. When true, the x axis is <a href="#xAxis.reversed">reversed</a> by default. If a bar series is present in the chart, it will be inverted automatically.</p>. . <p>Inverting the chart doesn't have an effect if there are no cartesian series in the chart, or if the chart is <a href="#chart.polar">polar</a>.</p>
    * @param margin              <p>The margin between the outer edge of the chart and the plot area. The numbers in the array designate top, right, bottom and left respectively. Use the options <code>marginTop</code>, <code>marginRight</code>, <code>marginBottom</code> and <code>marginLeft</code> for shorthand setting of one option.</p> <p>Since version 2.1, the margin is 0 by default. The actual space is dynamically calculated  from the offset of axis labels, axis title, title, subtitle and legend in addition to the <code>spacingTop</code>, <code>spacingRight</code>, <code>spacingBottom</code> and <code>spacingLeft</code> options.</p>. 		 Defaults to <code>[null]</code>.
    * @param options3d           Options to render charts in 3 dimensions. This feature requires <code>highcharts-3d.js</code>, found in the download package or online at <a href="http://code.highcharts.com/highcharts-3d.js">code.highcharts.com/highcharts-3d.js</a>.
    * @param panKey              Allows setting a key to switch between zooming and panning.
    * @param panning             Allow panning in a chart. Best used with <a href="#chart.panKey">panKey</a> to combine zooming and panning.
    * @param pinchType           Equivalent to <a href="#chart.zoomType">zoomType</a>, but for multitouch gestures only. By default, the <code>pinchType</code> is the same as the <code>zoomType</code> setting. However, pinching can be enabled separately in some cases, for example in stock charts where a mouse drag pans the chart, while pinching is enabled.
    * @param plotBackgroundColor The background color or gradient for the plot area.
    * @param plotBackgroundImage The URL for an image to use as the plot background. To set an image as the background for the entire chart, set a CSS background image to the container element. Note that for the image to be applied to exported charts, its URL needs to be accessible by the export server.
    * @param plotBorderColor     The color of the inner chart or plot area border.
    * @param plotBorderWidth     The pixel width of the plot area border.
    * @param plotShadow          Whether to apply a drop shadow to the plot area. Requires that plotBackgroundColor be set. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param polar               When true, cartesian charts like line, spline, area and column are transformed into the polar coordinate system. Requires <code>highcharts-more.js</code>.
    * @param reflow              Whether to reflow the chart to fit the width of the container div on resizing the window.
    * @param renderTo            The HTML element where the chart will be rendered. If it is a string, the element by that id is used. The HTML element can also be passed by direct reference.
    * @param resetZoomButton     The button that appears after a selection zoom, allowing the user to reset zoom.
    * @param selectionMarkerFill The background color of the marker square when selecting (zooming in on) an area of the chart.
    * @param shadow              Whether to apply a drop shadow to the outer chart area. Requires that .  backgroundColor be set. Since 2.3 the shadow can be an object configuration containing <code>color</code>, <code>offsetX</code>, <code>offsetY</code>, <code>opacity</code> and <code>width</code>.
    * @param showAxes            Whether to show the axes initially. This only applies to empty charts where series are added dynamically, as axes are automatically added to cartesian series.
    * @param spacing             The distance between the outer edge of the chart and the content, like title, legend, axis title or labels. The numbers in the array designate top, right, bottom and left respectively. Use the options spacingTop, spacingRight, spacingBottom and spacingLeft options for shorthand setting of one option.
    * @param style               Additional CSS styles to apply inline to the container <code>div</code>. Note that since the default font styles are applied in the renderer, it is ignorant of the individual chart  options and must be set globally. Defaults to:. <pre>style: {. 	fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif', // default font. 	fontSize: '12px'. }</pre>
    * @param typeDescription     A text description of the chart type. If the Accessibility module is loaded, this will be included in the description of the chart in the screen reader information region.
    * @param width               An explicit width for the chart. By default (when <code>null</code>) the width is calculated from the offset width of the containing element.
    * @param zoomType            Decides in what dimensions the user can zoom by dragging the mouse. Can be one of <code>x</code>, <code>y</code> or <code>xy</code>.
    */
  def apply(alignTicks: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            backgroundColor: js.UndefOr[Color] = js.undefined,
            borderColor: js.UndefOr[Color] = js.undefined,
            borderRadius: js.UndefOr[Double] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            colorCount: js.UndefOr[Int] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            events: js.UndefOr[ChartEvents] = js.undefined,
            height: js.UndefOr[Double] = js.undefined,
            ignoreHiddenSeries: js.UndefOr[Boolean] = js.undefined,
            inverted: js.UndefOr[Boolean] = js.undefined,
            margin: js.UndefOr[Margins] = js.undefined,
            options3d: js.UndefOr[ChartOptions3d] = js.undefined,
            panKey: js.UndefOr[String] = js.undefined,
            panning: js.UndefOr[Boolean] = js.undefined,
            pinchType: js.UndefOr[String] = js.undefined,
            plotBackgroundColor: js.UndefOr[Color] = js.undefined,
            plotBackgroundImage: js.UndefOr[String] = js.undefined,
            plotBorderColor: js.UndefOr[Color] = js.undefined,
            plotBorderWidth: js.UndefOr[Double] = js.undefined,
            plotShadow: js.UndefOr[Shadow] = js.undefined,
            polar: js.UndefOr[Boolean] = js.undefined,
            reflow: js.UndefOr[Boolean] = js.undefined,
            renderTo: js.UndefOr[String | js.Object] = js.undefined,
            resetZoomButton: js.UndefOr[ChartResetZoomButton] = js.undefined,
            selectionMarkerFill: js.UndefOr[Color] = js.undefined,
            shadow: js.UndefOr[Shadow] = js.undefined,
            showAxes: js.UndefOr[Boolean] = js.undefined,
            spacing: js.UndefOr[Spacing] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            typeDescription: js.UndefOr[String] = js.undefined,
            width: js.UndefOr[Double] = js.undefined,
            zoomType: js.UndefOr[ZoomType] = js.undefined): Chart = {

    val alignTicksOuter = alignTicks
    val animationOuter = animation.map(_.value)
    val backgroundColorOuter = backgroundColor.map(_.c)
    val borderColorOuter = borderColor.map(_.c)
    val borderRadiusOuter = borderRadius
    val borderWidthOuter = borderWidth
    val classNameOuter = className
    val colorCountOuter = colorCount
    val descriptionOuter = description
    val eventsOuter = events
    val heightOuter = height
    val ignoreHiddenSeriesOuter = ignoreHiddenSeries
    val invertedOuter = inverted
    val marginOuter = margin.map(m => js.Array(m.top, m.right, m.bottom, m.left))
    val options3dOuter = options3d
    val panKeyOuter = panKey
    val panningOuter = panning
    val pinchTypeOuter = pinchType
    val plotBackgroundColorOuter = plotBackgroundColor.map(_.c)
    val plotBackgroundImageOuter = plotBackgroundImage
    val plotBorderColorOuter = plotBorderColor.map(_.c)
    val plotBorderWidthOuter = plotBorderWidth
    val plotShadowOuter = plotShadow.map(_.value)
    val polarOuter = polar
    val reflowOuter = reflow
    val renderToOuter = renderTo
    val resetZoomButtonOuter = resetZoomButton
    val selectionMarkerFillOuter = selectionMarkerFill.map(_.c)
    val shadowOuter = shadow.map(_.value)
    val showAxesOuter = showAxes
    val spacingOuter = spacing.map(s => js.Array(s.top, s.right, s.bottom, s.left))
    val styleOuter = style.map(stringToStyleObject)
    val typeDescriptionOuter = typeDescription
    val widthOuter = width
    val zoomTypeOuter = zoomType.map(_.value)

    new Chart {
      override val alignTicks = alignTicksOuter
      override val animation = animationOuter
      override val backgroundColor = backgroundColorOuter
      override val borderColor = borderColorOuter
      override val borderRadius = borderRadiusOuter
      override val borderWidth = borderWidthOuter
      override val className = classNameOuter
      override val colorCount = colorCountOuter
      override val description = descriptionOuter
      override val events = eventsOuter
      override val height = heightOuter
      override val ignoreHiddenSeries = ignoreHiddenSeriesOuter
      override val inverted = invertedOuter
      override val margin = marginOuter
      override val options3d = options3dOuter
      override val panKey = panKeyOuter
      override val panning = panningOuter
      override val pinchType = pinchTypeOuter
      override val plotBackgroundColor = plotBackgroundColorOuter
      override val plotBackgroundImage = plotBackgroundImageOuter
      override val plotBorderColor = plotBorderColorOuter
      override val plotBorderWidth = plotBorderWidthOuter
      override val plotShadow = plotShadowOuter
      override val polar = polarOuter
      override val reflow = reflowOuter
      override val renderTo = renderToOuter
      override val resetZoomButton = resetZoomButtonOuter
      override val selectionMarkerFill = selectionMarkerFillOuter
      override val shadow = shadowOuter
      override val showAxes = showAxesOuter
      override val spacing = spacingOuter
      override val style = styleOuter
      override val typeDescription = typeDescriptionOuter
      override val width = widthOuter
      override val zoomType = zoomTypeOuter
    }
  }
}
