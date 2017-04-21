/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import io.udash.wrappers.highcharts.config.series.{BaseSeriesData, Series => SeriesConfig}
import io.udash.wrappers.highcharts.config.utils.Animation

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.native
trait Series extends js.Object {

  /**
    * Add a point to the series after render time. The point can be added at the end, or by giving it an X value,
    * to the start or in the middle of the series.
    *
    * @param animation Defaults to true. When true, the graph will be animated with default animation options.
    *                  The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-addpoint-append/" target="_blank">Append point</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-addpoint-append-and-shift/" target="_blank">append and shift</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-addpoint-x-and-y/" target="_blank">both x and y values given</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-addpoint-pie/" target="_blank">append pie slice</a>
    */
  def addPoint(options: BaseSeriesData[_], redraw: Boolean = js.native, shift: Boolean = js.native, animation: Boolean | js.Object = js.native): Unit = js.native

  /**
    * Read only. The chart that the series belongs to.
    */
  val chart: Chart = js.native

  /**
    * Hides the series if visible. If the <code>chart.ignoreHiddenSeries</code> option is true, the chart is redrawn without this series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-hide/" target="_blank">Toggle visibility from a button</a>
    */
  def hide(): Unit = js.native

  /**
    * The series' name as given in the options.
    */
  val name: String = js.native

  /**
    * Read only. The series' options.
    */
  val options: SeriesConfig = js.native

  /**
    * Remove the series from the chart.
    *
    * @param redraw Defaults to <code>true</code>. Whether to redraw the chart after the series is removed.
    *               If doing more operations on the chart, it is a good idea to set redraw to false and call <code>chart.redraw()</code> after.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-remove/" target="_blank">Remove first series from a button</a>
    */
  def remove(redraw: Boolean = js.native): Unit = js.native

  /**
    * Select or unselect the series. This means its <code>selected</code> property is set,the checkbox in the legend
    * is toggled and when selected, the series is returned in the <code>chart.getSelectedSeries()</code> method.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-select/" target="_blank">Select a series from a button</a>
    */
  def select(selected: Boolean = js.native): Unit = js.native

  /**
    * Read only. The series' selected state as set by <code>series.select()</code>.
    */
  def selected: Boolean = js.native

  /**
    * Apply a new set of data to the series and optionally redraw it. Note the difference in behaviour when setting the
    * same amount of points, or a different amount of points, as handled by the <code>updatePoints</code> parameter.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-setdata/" target="_blank">Set new data from a button</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-setdata-pie/" target="_blank">set data in a pie</a>
    */
  def setData(data: js.Array[Double] | js.Array[js.Array[Double]] | js.Array[js.Object], redraw: Boolean = js.native,
              animation: Boolean | js.Object = js.native, updatePoints: Boolean = js.native): Unit = js.native

  /**
    * A utility function to show or hide the series with an optional redraw.
    *
    * @param visible Whether to show or hide the series. If undefined, the visibility is toggled.
    * @param redraw  Defaults to <code>true</code>. Whether to redraw the chart after the series is altered.If doing more operations on the chart, it is a good idea to set redraw to false and call <code>chart.redraw()</code> after.
    */
  def setVisible(visible: Boolean, redraw: Boolean = js.native): Unit = js.native

  /**
    * Shows the series if hidden.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-hide/" target="_blank">Toggle visibility from a button</a>
    */
  def show(): Unit = js.native

  /**
    * Read only. The series' type, like "line", "area" etc.
    */
  val `type`: String = js.native

  /**
    * Update the series with a new set of options. For a clean and precise handling of new options, all methods and elements from the series is removed, and it is initiated from scratch. Therefore, this method is more performance expensive than some other utility methods like <code>setData</code> or <code>setVisible</code>.
    *
    * @param options New options that will be merged into the series' existing options.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/series-update/" target="_blank">Updating series options</a>
    */
  def update(options: SeriesConfig, redraw: Boolean = js.native): Unit = js.native

  /**
    * Read only. The series' visibility state as set by <code>series.show()</code>, <code>series.hide()</code>, or the initial configuration.
    */
  val visible: Boolean = js.native

  /**
    * Read only. The unique xAxis object associated with the series.
    */
  val xAxis: XAxis = js.native

  /**
    * Read only. The unique yAxis object associated with the series.
    */
  val yAxis: YAxis = js.native
}

object Series {

  implicit class SeriesExt(val series: Series) extends AnyVal {
    def data: Seq[Point] =
      series.asInstanceOf[js.Dynamic].data.asInstanceOf[js.Array[Point]].toSeq

    def removePoint(index: Int, redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      series.asInstanceOf[js.Dynamic].removePoint(index, redraw, animation.value.asInstanceOf[js.Any])
  }
}