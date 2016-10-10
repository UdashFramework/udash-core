/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.utils.Animation

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.native
trait Axis[AxisType <: config.axis.Axis[AxisType, _], PlotBand <: AxisPlotBand] extends js.Object {
  /**
    * Add a plot band after render time.
    *
    * @param options A configuration object consisting of the same members as <a class="internal" href="#xAxis.plotBands">options.xAxis.plotBands</a>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-addplotband/" target="_blank">Toggle the plot band from a button</a>
    */
  def addPlotBand(options: PlotBand): Unit = js.native

  /**
    * Add a plot line after render time.
    *
    * @param options A configuration object consisting of the same members as <a class="internal" href="#xAxis.plotLines">options.xAxis.plotLines</a>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-addplotline/" target="_blank">Toggle the plot line from a button</a>
    */
  def addPlotLine(options: AxisPlotLine): Unit = js.native

  /**
    * Get the current extremes for the axis. The returned object contains:
    *
    * <dl>
    * <dt>dataMax:</dt>
    * <dd>The maximum value of the axis' associated series.</dd>
    *
    * <dt>dataMin:</dt>
    * <dd>The minimum value of the axis' associated series.</dd>
    *
    * <dt>max:</dt>
    * <dd>The maximum axis value, either automatic or set manually. If the <code>max</code> option is not set,
    *   <code>maxPadding</code> is 0 and <code>endOnTick</code> is <code>false<code>, this value will be the same as <code>dataMax</code>.</dd>
    *
    * <dt>min:</dt>
    * <dd>The minimum axis value, either automatic or set manually. If the <code>min</code> option is not set,
    *   <code>minPadding</code> is 0 and <code>startOnTick</code> is <code>false</code>, this value will be the same as <code>dataMin</code>.</dd>
    * </dl>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-getextremes/" target="_blank">Report extremes by click on a button</a>
    */
  def getExtremes(): AxisExtremes = js.native

  /**
    * Remove an axis from the chart.
    *
    * @param redraw Defaults to <code>true</code>. Whether to redraw the chart following the remove.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-addaxis/" target="_blank">Add and remove axes</a>
    */
  def remove(redraw: Boolean = js.native): Unit = js.native

  /**
    * Remove a plot band by its <code>id</code>.
    *
    * @param id The plot band's <code>id</code> as given in the original configuration object or in the addPlotBand method.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-removeplotband/" target="_blank">Remove plot band by id</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-addplotband/" target="_blank">Toggle the plot band from a button</a>
    */
  def removePlotBand(id: String): Unit = js.native

  /**
    * Remove a plot line by its <code>id</code>.
    *
    * @param id The plot line's <code>id</code> as given in the original configuration object or in the addPlotLine method.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-id/" target="_blank">Remove plot line by id</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-addplotline/" target="_blank">toggle the plot line from a button</a>
    */
  def removePlotLine(id: String): Unit = js.native

  /**
    * Update the title of the axis after render time.
    *
    * @param title  The new title options on the same format as given in <a class="internal" href="#xAxis.title">xAxis.title</a>.
    * @param redraw Whether to redraw the chart now or hold until the next chart.redraw()
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-settitle/" target="_blank">Set a new Y axis title</a>
    */
  def setTitle(title: AxisTitle, redraw: Boolean = js.native): Unit = js.native

  /**
    * Translates a value in terms of axis units in to pixels within the chart.
    *
    * @param value           A value in terms of axis units.
    * @param paneCoordinates Whether to return the pixel coordinate relative to the chart or just the axis/pane itself.
    */
  def toPixels(value: Double, paneCoordinates: Boolean = js.native): Double = js.native

  /**
    * Translate a pixel position along the axis to a value in terms of axis units.
    *
    * @param pixel           A pixel position along the axis.
    * @param paneCoordinates Whether the input pixel position is relative to the chart or just the axis/pane itself.
    */
  def toValue(pixel: Double, paneCoordinates: Boolean = js.native): Double = js.native

  /**
    * Update an axis object with a new set of options. The options are merged with the existing options, so only new or altered options need to be specified.
    *
    * @param options The new options that will be merged in with existing options on the axis.
    * @param redraw  Defaults to `true`. Whether to redraw the chart after the new options are set.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-update/" target="_blank">Axis update demo</a>
    */
  def update(options: AxisType, redraw: Boolean = js.native): Unit = js.native
}

object Axis {
  import scala.scalajs.js.JSConverters._

  implicit class AxisExt(val axis: Axis[_, _]) extends AnyVal {
    /**
      * Set new categories for the axis.
      *
      * @param categories The new category names.
      * @param redraw     Defaults to <code>true</code>. Whether to redraw the axis or wait for an explicit call to <code>chart.redraw()</code>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-setcategories/" target="_blank">Set categories by click on a button</a>
      */
    def setCategories(categories: Seq[String], redraw: Boolean = true): Unit =
      axis.asInstanceOf[js.Dynamic].setCategories(categories.toJSArray, redraw)

    /**
      * Set the minimum and maximum of the axes after render time. If the <code>startOnTick</code> and <code>endOnTick</code>
      * options are true, the minimum and maximum values are rounded off to the nearest tick. To prevent this,
      * these options can be set to false before calling setExtremes. Also, <code>setExtremes</code> will not allow a range lower
      * than the <a href="#xAxis.minRange">minRange</a> option, which by default is the range of five points.
      *
      * @param min       The new minimum value
      * @param max       The new maximum value
      * @param redraw    Defaults to <code>true</code>. Whether to redraw the chart or wait for an explicit call to <code>chart.redraw()</code>.
      * @param animation Defaults to true. When true, the resize will be animated with default animation options.
      *                  The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-setextremes/" target="_blank">Set extremes from button</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-setextremes-datetime/" target="_blank">Set extremes on datetime axis</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/axis-setextremes-off-ticks/" target="_blank">setting extremes off ticks</a>
      */
    def setExtremes(min: Double, max: Double, redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      axis.asInstanceOf[js.Dynamic].setExtremes(min, max, redraw, animation.value.asInstanceOf[js.Any])
  }
}

@js.native
trait XAxis extends Axis[config.axis.XAxis, XAxisPlotBand]

@js.native
trait YAxis extends Axis[config.axis.YAxis, YAxisPlotBand]

@js.native
trait AxisExtremes extends js.Object {
  /** The maximum value of the axis' associated series. */
  def dataMax: Double = js.native

  /** The minimum value of the axis' associated series. */
  def dataMin: Double = js.native

  /** The maximum axis value, either automatic or set manually.
    * If the max option is not set, maxPadding is 0 and endOnTick is false, this value will be the same as dataMax. */
  def max: Double = js.native

  /** The minimum axis value, either automatic or set manually.
    * If the min option is not set, minPadding is 0 and startOnTick is false, this value will be the same as dataMin. */
  def min: Double = js.native
}