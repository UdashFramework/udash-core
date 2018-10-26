/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import io.udash.wrappers.jquery.JQueryEvent

import scala.scalajs.js
import scala.scalajs.js.ThisFunction

trait SeriesEvents extends js.Object {

  /**
    * Fires after the series has finished its initial animation, or in case animation is disabled, immediately as the series is displayed.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-afteranimate/" target="_blank">Show label after animate</a>
    */
  val afterAnimate: js.UndefOr[js.ThisFunction0[api.Series, Any]] = js.undefined

  /**
    * Fires when the checkbox next to the series' name in the legend is clicked. One parameter, <code>event</code>,
    * is passed to the function. The state of the checkbox is found by <code>event.checked</code>.
    * The checked item is found by <code>event.item</code>. Return <code>false</code> to prevent
    * the default action which is to toggle the select state of the series.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-checkboxclick/" target="_blank">Alert checkbox status</a>
    */
  val checkboxClick: js.UndefOr[js.ThisFunction1[api.Series, SeriesEvents.CheckboxClickEvent, Boolean]] = js.undefined

  /**
    * Fires when the series is clicked. One parameter, <code>event</code>, is passed to the function.
    * This contains common event information based on jQuery or MooTools depending on  which library is used as the base
    * for Highcharts. Additionally, <code>event.point</code> holds a pointer to the nearest point on the graph.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-click/" target="_blank">Alert click info</a>
    */
  val click: js.UndefOr[js.ThisFunction1[api.Series, SeriesEvents.ClickEvent, Any]] = js.undefined

  /**
    * Fires when the series is hidden after chart generation time, either by clicking the legend item or by calling <code>.hide()</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-hide/" target="_blank">Alert when the series is hidden by clicking
    *          the legend item</a>
    */
  val hide: js.UndefOr[js.ThisFunction0[api.Series, Any]] = js.undefined

  /**
    * Fires when the legend item belonging to the series is clicked. One parameter, <code>event</code>, is passed to the function. The default action is to toggle the visibility of the series. This can be prevented by returning <code>false</code> or calling <code>event.preventDefault()</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-legenditemclick/" target="_blank">Confirm hiding and showing</a>
    */
  val legendItemClick: js.UndefOr[js.ThisFunction1[api.Series, JQueryEvent, Boolean]] = js.undefined

  /**
    * Fires when the mouse leaves the graph. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. If the  <a class="internal" href="#plotOptions.series">stickyTracking</a> option is true, <code>mouseOut</code> doesn't happen before the mouse enters another graph or leaves the plot area.
    *
    * @example Log mouse over and out <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-mouseover-sticky/" target="_blank">with sticky tracking
    *          by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-mouseover-no-sticky/" target="_blank">without sticky tracking</a>
    */
  val mouseOut: js.UndefOr[js.ThisFunction1[api.Series, JQueryEvent, Any]] = js.undefined

  /**
    * Fires when the mouse enters the graph. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.
    *
    * @example Log mouse over and out <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-mouseover-sticky/" target="_blank">with sticky tracking
    *          by default</a>,
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-mouseover-no-sticky/" target="_blank">without sticky tracking</a>
    */
  val mouseOver: js.UndefOr[js.ThisFunction1[api.Series, JQueryEvent, Any]] = js.undefined

  /**
    * Fires when the series is shown after chart generation time, either by clicking the legend item or by calling <code>.show()</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-events-show/" target="_blank">Alert when the series is shown by clicking
    *          the legend item.</a>
    */
  val show: js.UndefOr[js.ThisFunction0[api.Series, Any]] = js.undefined
}

object SeriesEvents {

  @js.native
  trait CheckboxClickEvent extends JQueryEvent {
    def checked: Boolean = js.native
    def item: api.Series = js.native
  }

  @js.native
  trait ClickEvent extends JQueryEvent {
    def checked: Boolean = js.native
    def point: api.Point = js.native
  }

  /**
    * @param afterAnimate    Fires after the series has finished its initial animation, or in case animation is disabled, immediately as the series is displayed.
    * @param checkboxClick   Fires when the checkbox next to the series' name in the legend is clicked. One parameter, <code>event</code>, is passed to the function. The state of the checkbox is found by <code>event.checked</code>. The checked item is found by <code>event.item</code>. Return <code>false</code> to prevent the default action which is to toggle the select state of the series.
    * @param click           Fires when the series is clicked. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. Additionally, <code>event.point</code> holds a pointer to the nearest point on the graph.
    * @param hide            Fires when the series is hidden after chart generation time, either by clicking the legend item or by calling <code>.hide()</code>.
    * @param legendItemClick Fires when the legend item belonging to the series is clicked. One parameter, <code>event</code>, is passed to the function. The default action is to toggle the visibility of the series. This can be prevented by returning <code>false</code> or calling <code>event.preventDefault()</code>.
    * @param mouseOut        Fires when the mouse leaves the graph. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts. If the  <a class="internal" href="#plotOptions.series">stickyTracking</a> option is true, <code>mouseOut</code> doesn't happen before the mouse enters another graph or leaves the plot area.
    * @param mouseOver       Fires when the mouse enters the graph. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on  which library is used as the base for Highcharts.
    * @param show            Fires when the series is shown after chart generation time, either by clicking the legend item or by calling <code>.show()</code>.
    */
  def apply(afterAnimate: js.UndefOr[(api.Series) => Any] = js.undefined,
            checkboxClick: js.UndefOr[(api.Series, SeriesEvents.CheckboxClickEvent) => Boolean] = js.undefined,
            click: js.UndefOr[(api.Series, SeriesEvents.ClickEvent) => Any] = js.undefined,
            hide: js.UndefOr[(api.Series) => Any] = js.undefined,
            legendItemClick: js.UndefOr[(api.Series, JQueryEvent) => Boolean] = js.undefined,
            mouseOut: js.UndefOr[(api.Series, JQueryEvent) => Any] = js.undefined,
            mouseOver: js.UndefOr[(api.Series, JQueryEvent) => Any] = js.undefined,
            show: js.UndefOr[(api.Series) => Any] = js.undefined): SeriesEvents = {
    val afterAnimateOuter = afterAnimate.map(ThisFunction.fromFunction1[api.Series, Any])
    val checkboxClickOuter = checkboxClick.map(ThisFunction.fromFunction2[api.Series, SeriesEvents.CheckboxClickEvent, Boolean])
    val clickOuter = click.map(ThisFunction.fromFunction2[api.Series, SeriesEvents.ClickEvent, Any])
    val hideOuter = hide.map(ThisFunction.fromFunction1[api.Series, Any])
    val legendItemClickOuter = legendItemClick.map(ThisFunction.fromFunction2[api.Series, JQueryEvent, Boolean])
    val mouseOutOuter = mouseOut.map(ThisFunction.fromFunction2[api.Series, JQueryEvent, Any])
    val mouseOverOuter = mouseOver.map(ThisFunction.fromFunction2[api.Series, JQueryEvent, Any])
    val showOuter = show.map(ThisFunction.fromFunction1[api.Series, Any])

    new SeriesEvents {
      override val afterAnimate = afterAnimateOuter
      override val checkboxClick = checkboxClickOuter
      override val click = clickOuter
      override val hide = hideOuter
      override val legendItemClick = legendItemClickOuter
      override val mouseOut = mouseOutOuter
      override val mouseOver = mouseOverOuter
      override val show = showOuter
    }
  }
}
