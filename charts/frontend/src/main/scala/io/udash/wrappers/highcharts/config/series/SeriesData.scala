/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
trait BaseSeriesData[DataLabels <: BaseSeriesDataLabels] extends js.Object {

  /**
    * Individual color for the point. By default the color is pulled from the global <code>colors</code> array.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/point/color/" target="_blank">Mark the highest point</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Individual data label for each point. The options are the same as the ones for  <a class="internal" href="#plotOptions.series.dataLabels">plotOptions.series.dataLabels</a>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/point/datalabels/" target="_blank">Show a label for the last value</a>
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
    * The <code>id</code> of a series in the <a href="#drilldown.series">drilldown.series</a> array to use for a drilldown for this point.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/basic/" target="_blank">Basic drilldown</a>
    */
  val drilldown: js.UndefOr[String] = js.undefined

  /**
    * Individual point events
    */
  val events: js.UndefOr[SeriesDataEvents] = js.undefined

  /**
    * An id for the point. This can be used after render time to get a pointer to the point object through <code>chart.get()</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/point/id/" target="_blank">Remove an id'd point</a>
    */
  val id: js.UndefOr[String] = js.undefined

  /**
    * The rank for this point's data label in case of collision. If two data labels are about to overlap,
    * only the one with the highest <code>labelrank</code> will be drawn.
    */
  val labelrank: js.UndefOr[Double] = js.undefined

  /**
    * <p>The name of the point as shown in the legend, tooltip, dataLabel etc.</p>
    *
    * <p>If the <a href="#xAxis.type">xAxis.type</a> is set to <code>category</code>, and no <a href="#xAxis.categories">categories</a> option exists, the category will be pulled from the <code>point.name</code> of the last series defined. For multiple series, best practice however is to define <code>xAxis.categories</code>.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/series/data-array-of-objects/" target="_blank">Point names</a>
    */
  val name: js.UndefOr[String] = js.undefined

  /**
    * Whether the data point is selected initially.
    */
  val selected: js.UndefOr[Boolean] = js.undefined
}

@js.annotation.ScalaJSDefined
trait BaseXSeriesData[DataLabels <: BaseSeriesDataLabels] extends BaseSeriesData[DataLabels] {
  /**
    * The x value of the point.
    */
  val x: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait BaseYSeriesData[DataLabels <: BaseSeriesDataLabels] extends BaseSeriesData[DataLabels] {
  /**
    * The y value of the point.
    */
  val y: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait BaseTwoDimSeriesData[DataLabels <: BaseSeriesDataLabels] extends BaseSeriesData[DataLabels] {
  /**
    * The x value of the point.
    */
  val x: js.UndefOr[Double] = js.undefined

  /**
    * The y value of the point.
    */
  val y: js.UndefOr[Double] = js.undefined
}

@js.annotation.ScalaJSDefined
trait BaseTwoDimMarkerSeriesData[DataLabels <: BaseSeriesDataLabels] extends BaseTwoDimSeriesData[DataLabels] {
  val marker: js.UndefOr[SeriesDataMarker] = js.undefined
}