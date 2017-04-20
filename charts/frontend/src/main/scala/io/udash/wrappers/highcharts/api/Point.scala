/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import io.udash.wrappers.highcharts.config.series.BaseSeriesData
import io.udash.wrappers.highcharts.config.utils.Animation

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.native
trait Point extends js.Object {

  /**
    * For categorized axes this property holds the category name for the point. For other axis it holds the x value.
    */
  val category: String | Double = js.native

  /**
    * The percentage for points in a stacked series or pies.
    */
  val percentage: Double = js.native

  /**
    * Select or unselect the point.
    *
    * @param select     When <code>true</code>, the point is selected. When <code>false</code>, the point is unselected.
    *                   When <code>null</code> or <code>undefined</code>, the selection state is toggled.
    * @param accumulate When <code>true</code>, the selection is added to other selected points. When <code>false</code>,
    *                   other selected points are deselected. Internally in Highcharts,selected points are accumulated on Control, Shift or Cmd clicking the point.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/point-select/" target="_blank">Select a point from a button</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-selection-points/" target="_blank">select a range of points through a drag selection</a>.
    */
  def select(select: Boolean = js.native, accumulate: Boolean = js.native): Unit = js.native

  /**
    * Whether the point is selected or not.
    */
  def selected: Boolean = js.native

  /**
    * The series object associated with the point.
    */
  val series: Series = js.native

  /**
    * The total of a stack for stacked series, or pie in pie charts.
    */
  val total: Double = js.native

  /**
    * For certain series types, like pie. Whether the Point instance is visible.
    */
  val visible: Boolean = js.native

  /**
    * The x value for the point.
    */
  val x: Double = js.native

  /**
    * The y value for the point.
    */
  val y: Double = js.native
}

object Point {
  implicit class PointExt(val point: Point) extends AnyVal {
    /**
      * Remove the point from the series.
      *
      * @param redraw    Defaults to <code>true</code>. Whether to redraw the chart after the point is removed.
      *                  If doing more operations on the chart, it is a good idea to set redraw to false and call <code>chart.redraw()</code> after.
      * @param animation Defaults to true. When true, the graph's updating will be animated with default animation options.
      *                  The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-point-events-remove/" target="_blank">Remove point and confirm</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/point-remove/" target="_blank">Remove pie slice</a>
      */
    def remove(redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      point.asInstanceOf[js.Dynamic].remove(redraw, animation.value.asInstanceOf[js.Any])

    /**
      * Slice out or set back in a pie chart slice. This is the default way of Highcharts to visualize that a pie point is selected.
      *
      * @param sliced    When <code>true</code>, the point is sliced out. When <code>false</code>,
      *                  the point is set in. When <code>null</code> or <code>undefined</code>, the sliced state is toggled.
      * @param redraw    Defaults to <code>true</code>. Whether to redraw the chart after the point is altered.
      * @param animation Defaults to true. When true, the move will be animated with default animation options.
      *                  The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/point-slice/" target="_blank">Slice and unslice a point from a button</a>
      */
    def slice(sliced: Boolean, redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      point.asInstanceOf[js.Dynamic].slice(sliced, redraw, animation.value.asInstanceOf[js.Any])

    /**
      * Update the point with new values.
      *
      * @param options   The point options. Point options are handled as described under the series<type>.data item
      *                  for each series type. For example for a line series, if options is a single number, the point will
      *                  be given that number as the main y value. If it is an array, it will be interpreted as x and y values respectively.
      *                  If it is an object, advanced options are applied.
      * @param redraw    Defaults to <code>true</code>. Whether to redraw the chart after the point is updated.
      *                  If doing more operations on the chart, it is a good idea to set redraw to false and call <code>chart.redraw()</code> after.
      * @param animation Defaults to true. When true, the update will be animated with default animation options.
      *                  The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/point-update-column/" target="_blank">Update column value</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/point-update-pie/" target="_blank">update pie slice</a>
      */
    def update(options: BaseSeriesData[_], redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      point.asInstanceOf[js.Dynamic].update(options, redraw, animation.value.asInstanceOf[js.Any])
  }
}