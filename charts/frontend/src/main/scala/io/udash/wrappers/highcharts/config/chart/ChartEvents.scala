/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package chart

import io.udash.wrappers.highcharts.api.Point
import io.udash.wrappers.highcharts.config.axis.{Axis, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.series.{Series => SeriesConfig}
import io.udash.wrappers.jquery.JQueryEvent
import org.scalajs.dom.Event

import scala.scalajs.js


@js.annotation.ScalaJSDefined
class ChartEvents extends js.Object {

  /**
    * Fires when a series is added to the chart after load time, using the <code>addSeries</code> method.
    * One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools
    * depending on  which library is used as the base for Highcharts. Through <code>event.options</code> you can access
    * the series options that was passed to the <code>addSeries</code>  method. Returning false prevents the series from being added.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-addseries/" target="_blank">Alert on add series</a>
    */
  val addSeries: js.UndefOr[js.ThisFunction1[api.Chart, ChartEvents.AddSeriesEvent, Any]] = js.undefined

  /**
    * Fires after a chart is printed through the context menu item or the <code>Chart.print</code> method. Requires the exporting module.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-beforeprint-afterprint/" target="_blank">Rescale the chart to print</a>
    */
  val afterPrint: js.UndefOr[js.ThisFunction0[api.Chart, Any]] = js.undefined

  /**
    * Fires before a chart is printed through the context menu item or the <code>Chart.print</code> method. Requires the exporting module.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-beforeprint-afterprint/" target="_blank">Rescale the chart to print</a>
    */
  val beforePrint: js.UndefOr[js.ThisFunction0[api.Chart, Any]] = js.undefined

  /**
    * <p>Fires when clicking on the plot background. One parameter, <code>event</code>, is passed to the function.
    * This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.</p>
    * <p>Information on the clicked spot can be found through <code>event.xAxis</code> and  <code>event.yAxis</code>,
    * which are arrays containing the axes of each dimension and each axis' value at the clicked spot.
    * The primary axes are <code>event.xAxis[0]</code> and <code>event.yAxis[0]</code>.
    * Remember the unit of a datetime axis is milliseconds since 1970-01-01 00:00:00.</p>
    * <pre>click: function(e) {
    * 	console.log(
    * 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', e.xAxis[0].value), 
    * 		e.yAxis[0].value
    * )
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-click/" target="_blank">Alert coordinates on click</a>. <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-container/" target="_blank">Alternatively, attach event to container</a>
    */
  val click: js.UndefOr[js.ThisFunction1[api.Chart, ChartEvents.ClickEvent, Any]] = js.undefined

  /**
    * <p>Fires when a drilldown point is clicked, before the new series is added. This event is also utilized for async drilldown,
    * where the seriesOptions are not added by option, but rather loaded async. Note that when clicking a category label
    * to trigger multiple series drilldown, one <code>drilldown</code> event is triggered per point in the category.</p>
    *
    * <p>Event arguments:</p>
    *
    * <dl>
    *
    * <dt><code>category</code></dt>
    * <dd>If a category label was clicked, which index.</dd>
    *
    * <dt><code>point</code></dt>
    * <dd>The originating point.</dd>
    *
    * <dt><code>originalEvent</code></dt>
    * <dd>The original browser event (usually click) that triggered the drilldown.</dd>
    *
    * <dt><code>points</code></dt>
    * <dd>If a category label was clicked, this array holds all points corresponing to the category.</dd>
    *
    * <dt><code>seriesOptions</code></dt>
    * <dd>Options for the new series</dd>
    *
    * </dl>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/async/" target="_blank">Async drilldown</a>
    */
  val drilldown: js.UndefOr[js.ThisFunction1[api.Chart, ChartEvents.DrilldownEvent, Any]] = js.undefined

  /**
    * Fires when drilling up from a drilldown series.
    */
  val drillup: js.UndefOr[js.ThisFunction0[api.Chart, Any]] = js.undefined

  /**
    * In a chart with multiple drilldown series, this event fires after all the series have been drilled up.
    */
  val drillupall: js.UndefOr[js.ThisFunction0[api.Chart, Any]] = js.undefined

  /**
    * <p>Fires when the chart is finished loading. Since v4.2.2, it also waits for images to be loaded,
    * for example from point markers. One parameter, <code>event</code>, is passed to the function.
    * This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.</p>
    *
    * <p>There is also a second parameter to the chart constructor where a callback function can be passed to be executed on chart.load.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-load/" target="_blank">Alert on chart load</a>
    */
  val load: js.UndefOr[js.ThisFunction1[api.Chart, JQueryEvent, Any]] = js.undefined

  /**
    * Fires when the chart is redrawn, either after a call to chart.redraw() or after an axis, series or point is modified with the <code>redraw</code> option set to true. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-redraw/" target="_blank">Alert on chart redraw</a>
    */
  val redraw: js.UndefOr[js.ThisFunction1[api.Chart, JQueryEvent, Any]] = js.undefined

  /**
    * <p>Fires when an area of the chart has been selected. Selection is enabled by setting the chart's zoomType. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. The default action for the  selection event is to zoom the  chart to the selected area. It can be prevented by calling  <code>event.preventDefault()</code>.</p> <p>Information on the selected area can be found through <code>event.xAxis</code> and  <code>event.yAxis</code>, which are arrays containing the axes of each dimension and each axis' min and max values. The primary axes are <code>event.xAxis[0]</code> and <code>event.yAxis[0]</code>. Remember the unit of a datetime axis is milliseconds since 1970-01-01 00:00:00.</p> 
    * <pre>selection: function(event) {
    * // log the min and max of the primary, datetime x-axis
    * 	console.log(
    * 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', event.xAxis[0].min),
    * 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', event.xAxis[0].max)
    * );
    * // log the min and max of the y axis
    * 	console.log(event.yAxis[0].min, event.yAxis[0].max);
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-selection/" target="_blank">Report on selection and reset</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-selection-points/" target="_blank">select a range of points through a drag selection</a>.
    */
  val selection: js.UndefOr[js.ThisFunction1[api.Chart, ChartEvents.SelectionEvent, Any]] = js.undefined
}

object ChartEvents {
  import io.udash.wrappers.jquery._

  import scala.scalajs.js.JSConverters._

  @js.native
  trait AddSeriesEvent extends JQueryEvent {
    def options: SeriesConfig = js.native
  }

  @js.native
  trait ClickEvent extends JQueryEvent {
    def xAxis: js.Array[ClickEventItem[XAxis]] = js.native
    def yAxis: js.Array[ClickEventItem[YAxis]] = js.native
  }

  @js.native
  trait ClickEventItem[AxisType <: Axis[AxisType, _]] extends JQueryEvent {
    def value: Double = js.native
    def axis: AxisType = js.native
  }

  @js.native
  trait DrilldownEvent extends JQueryEvent {
    def category: Int = js.native
    def point: Point = js.native
    def originalEvent: Event = js.native
    def points: js.Array[Point] = js.native
    def seriesOptions: SeriesConfig = js.native
  }

  @js.native
  trait SelectionEvent extends JQueryEvent {
    def xAxis: js.Array[XAxis] = js.native
    def yAxis: js.Array[YAxis] = js.native
  }

  /**
    * @param addSeries   Fires when a series is added to the chart after load time, using the <code>addSeries</code> method. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. Through <code>event.options</code> you can access the series options that was passed to the <code>addSeries</code>  method. Returning false prevents the series from being added.
    * @param afterPrint  Fires after a chart is printed through the context menu item or the <code>Chart.print</code> method. Requires the exporting module.
    * @param beforePrint Fires before a chart is printed through the context menu item or the <code>Chart.print</code> method. Requires the exporting module.
    * @param click       <p>Fires when clicking on the plot background. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.</p> <p>Information on the clicked spot can be found through <code>event.xAxis</code> and  <code>event.yAxis</code>, which are arrays containing the axes of each dimension and each axis' value at the clicked spot. The primary axes are <code>event.xAxis[0]</code> and <code>event.yAxis[0]</code>. Remember the unit of a datetime axis is milliseconds since 1970-01-01 00:00:00.</p>. <pre>click: function(e) {. 	console.log(. 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', e.xAxis[0].value), . 		e.yAxis[0].value. 	). }</pre>
    * @param drilldown   <p>Fires when a drilldown point is clicked, before the new series is added. This event is also utilized for async drilldown, where the seriesOptions are not added by option, but rather loaded async. Note that when clicking a category label to trigger multiple series drilldown, one <code>drilldown</code> event is triggered per point in the category.</p>. . <p>Event arguments:</p>. . <dl>. .   <dt><code>category</code></dt>.   <dd>If a category label was clicked, which index.</dd>. .   <dt><code>point</code></dt>.   <dd>The originating point.</dd>. .   <dt><code>originalEvent</code></dt>.   <dd>The original browser event (usually click) that triggered the drilldown.</dd>. .   <dt><code>points</code></dt>.   <dd>If a category label was clicked, this array holds all points corresponing to the category.</dd>. .   <dt><code>seriesOptions</code></dt>.   <dd>Options for the new series</dd>. . </dl>
    * @param drillup     Fires when drilling up from a drilldown series.
    * @param drillupall  In a chart with multiple drilldown series, this event fires after all the series have been drilled up.
    * @param load        <p>Fires when the chart is finished loading. Since v4.2.2, it also waits for images to be loaded, for example from point markers. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.</p>. . <p>There is also a second parameter to the chart constructor where a callback function can be passed to be executed on chart.load.</p>
    * @param redraw      Fires when the chart is redrawn, either after a call to chart.redraw() or after an axis, series or point is modified with the <code>redraw</code> option set to true. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.
    * @param selection   <p>Fires when an area of the chart has been selected. Selection is enabled by setting the chart's zoomType. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. The default action for the  selection event is to zoom the  chart to the selected area. It can be prevented by calling  <code>event.preventDefault()</code>.</p> <p>Information on the selected area can be found through <code>event.xAxis</code> and  <code>event.yAxis</code>, which are arrays containing the axes of each dimension and each axis' min and max values. The primary axes are <code>event.xAxis[0]</code> and <code>event.yAxis[0]</code>. Remember the unit of a datetime axis is milliseconds since 1970-01-01 00:00:00.</p> . <pre>selection: function(event) {. 	// log the min and max of the primary, datetime x-axis. 	console.log(. 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', event.xAxis[0].min),. 		Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', event.xAxis[0].max). 	);. 	// log the min and max of the y axis. 	console.log(event.yAxis[0].min, event.yAxis[0].max);. }</pre>
    */
  def apply(addSeries: js.UndefOr[(api.Chart, ChartEvents.AddSeriesEvent) => Any] = js.undefined,
            afterPrint: js.UndefOr[(api.Chart) => Any] = js.undefined,
            beforePrint: js.UndefOr[(api.Chart) => Any] = js.undefined,
            click: js.UndefOr[(api.Chart, ChartEvents.ClickEvent) => Any] = js.undefined,
            drilldown: js.UndefOr[(api.Chart, ChartEvents.DrilldownEvent) => Any] = js.undefined,
            drillup: js.UndefOr[(api.Chart) => Any] = js.undefined,
            drillupall: js.UndefOr[(api.Chart) => Any] = js.undefined,
            load: js.UndefOr[(api.Chart, JQueryEvent) => Any] = js.undefined,
            redraw: js.UndefOr[(api.Chart, JQueryEvent) => Any] = js.undefined,
            selection: js.UndefOr[(api.Chart, ChartEvents.SelectionEvent) => Any] = js.undefined): ChartEvents = {
    val addSeriesOuter = addSeries.map(js.ThisFunction.fromFunction2[api.Chart, ChartEvents.AddSeriesEvent, Any])
    val afterPrintOuter = afterPrint.map(js.ThisFunction.fromFunction1[api.Chart, Any])
    val beforePrintOuter = beforePrint.map(js.ThisFunction.fromFunction1[api.Chart, Any])
    val clickOuter = click.map(js.ThisFunction.fromFunction2[api.Chart, ChartEvents.ClickEvent, Any])
    val drilldownOuter = drilldown.map(js.ThisFunction.fromFunction2[api.Chart, ChartEvents.DrilldownEvent, Any])
    val drillupOuter = drillup.map(js.ThisFunction.fromFunction1[api.Chart, Any])
    val drillupallOuter = drillupall.map(js.ThisFunction.fromFunction1[api.Chart, Any])
    val loadOuter = load.map(js.ThisFunction.fromFunction2[api.Chart, JQueryEvent, Any])
    val redrawOuter = redraw.map(js.ThisFunction.fromFunction2[api.Chart, JQueryEvent, Any])
    val selectionOuter = selection.map(js.ThisFunction.fromFunction2[api.Chart, ChartEvents.SelectionEvent, Any])

    new ChartEvents {
      override val addSeries = addSeriesOuter
      override val afterPrint = afterPrintOuter
      override val beforePrint = beforePrintOuter
      override val click = clickOuter
      override val drilldown = drilldownOuter
      override val drillup = drillupOuter
      override val drillupall = drillupallOuter
      override val load = loadOuter
      override val redraw = redrawOuter
      override val selection = selectionOuter
    }
  }
}
