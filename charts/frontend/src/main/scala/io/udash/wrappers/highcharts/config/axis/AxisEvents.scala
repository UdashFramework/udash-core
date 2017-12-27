/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.jquery.JQueryEvent

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSName}

trait BaseAxisEvents[AxisType <: BaseAxis[AxisType, _]] extends js.Object {

  /**
    * As opposed to the <code>setExtremes</code> event, this event fires after the final min and max values are computed
    * and corrected for <code>minRange</code>.
    */
  val afterSetExtremes: js.UndefOr[js.ThisFunction0[AxisType, Any]] = js.undefined

  /**
    * <p>Fires when the minimum and maximum is set for the axis, either by calling the <code>.setExtremes()</code> method
    * or by selecting an area in the chart. One parameter, <code>event</code>, is passed to the function.
    * This contains common event information based on jQuery or MooTools depending on which library is used as the base
    * for Highcharts.</p>
    * 
    * <p>The new user set minimum and maximum values can be found by <code>event.min</code> and <code>event.max</code>.
    * When an axis is zoomed all the way out from the "Reset zoom" button, <code>event.min</code>
    * and <code>event.max</code> are null, and the new extremes are set based on <code>this.dataMin</code>
    * and <code>this.dataMax</code>.</p>
    */
  val setExtremes: js.UndefOr[js.ThisFunction1[AxisType, AxisEvents.SetExtremesEvent, Any]] = js.undefined
}

trait AxisEvents[AxisType <: BaseAxis[AxisType, _]] extends BaseAxisEvents[AxisType] {

  /**
    * An event fired after the breaks have rendered.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/axisbreak/break-event//" target="_blank">afterBreak Event</a>
    */
  val afterBreaks: js.UndefOr[js.Function0[Any]] = js.undefined

  /**
    * An event fired when a break from this axis occurs on a point.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/axisbreak/break-visualized/" target="_blank">Visualization of a Break</a>
    */
  val pointBreak: js.UndefOr[js.ThisFunction0[AxisType, Any]] = js.undefined

  /**
    * An event fired when a point falls inside a break from this axis.
    */
  val pointInBreak: js.UndefOr[js.ThisFunction0[AxisType, Any]] = js.undefined
}

object AxisEvents {
  @js.native @JSGlobal("SetExtremesEvent")
  class SetExtremesEvent extends JQueryEvent
}

trait AxisEventsCompanion[AxisType <: BaseAxis[AxisType, AxisEventsType], AxisEventsType <: AxisEvents[AxisType]] {
  import scala.scalajs.js.JSConverters._

  /**
    * @param afterBreaks      An event fired after the breaks have rendered.
    * @param afterSetExtremes As opposed to the <code>setExtremes</code> event, this event fires after the final min and max values are computed and corrected for <code>minRange</code>.
    * @param pointBreak       An event fired when a break from this axis occurs on a point.
    * @param pointInBreak     An event fired when a point falls inside a break from this axis.
    * @param setExtremes      <p>Fires when the minimum and maximum is set for the axis, either by calling the <code>.setExtremes()</code> method or by selecting an area in the chart. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on which library is used as the base for Highcharts.</p>. . <p>The new user set minimum and maximum values can be found by <code>event.min</code> and <code>event.max</code>. When an axis is zoomed all the way out from the "Reset zoom" button, <code>event.min</code> and <code>event.max</code> are null, and the new extremes are set based on <code>this.dataMin</code> and <code>this.dataMax</code>.</p>
    */
  def apply(afterBreaks: js.UndefOr[() => Any] = js.undefined,
            afterSetExtremes: js.UndefOr[(AxisType) => Any] = js.undefined,
            pointBreak: js.UndefOr[(AxisType) => Any] = js.undefined,
            pointInBreak: js.UndefOr[(AxisType) => Any] = js.undefined,
            setExtremes: js.UndefOr[(AxisType, AxisEvents.SetExtremesEvent) => Any] = js.undefined): AxisEventsType = {
    val afterBreaksOuter = afterBreaks.map(js.Any.fromFunction0)
    val afterSetExtremesOuter = afterSetExtremes.map(js.ThisFunction.fromFunction1[AxisType, Any])
    val pointBreakOuter = pointBreak.map(js.ThisFunction.fromFunction1[AxisType, Any])
    val pointInBreakOuter = pointInBreak.map(js.ThisFunction.fromFunction1[AxisType, Any])
    val setExtremesOuter = setExtremes.map(js.ThisFunction.fromFunction2[AxisType, AxisEvents.SetExtremesEvent, Any])

    new AxisEvents[AxisType] {
      override val afterBreaks = afterBreaksOuter
      override val afterSetExtremes = afterSetExtremesOuter
      override val pointBreak = pointBreakOuter
      override val pointInBreak = pointInBreakOuter
      override val setExtremes = setExtremesOuter
    }.asInstanceOf[AxisEventsType]
  }
}

trait XAxisEvents extends AxisEvents[XAxis]
object XAxisEvents extends AxisEventsCompanion[XAxis, XAxisEvents]

trait YAxisEvents extends AxisEvents[YAxis]
object YAxisEvents extends AxisEventsCompanion[YAxis, YAxisEvents]

trait ZAxisEvents extends AxisEvents[ZAxis]
object ZAxisEvents extends AxisEventsCompanion[ZAxis, ZAxisEvents]

trait ColorAxisEvents extends BaseAxisEvents[ColorAxis]
object ColorAxisEvents {
  import scala.scalajs.js.JSConverters._

  /**
    * @param afterSetExtremes As opposed to the <code>setExtremes</code> event, this event fires after the final min and max values are computed and corrected for <code>minRange</code>.
    * @param setExtremes      <p>Fires when the minimum and maximum is set for the axis, either by calling the <code>.setExtremes()</code> method or by selecting an area in the chart. One parameter, <code>event</code>, is passed to the function. This contains common event information based on jQuery or MooTools depending on which library is used as the base for Highcharts.</p>. . <p>The new user set minimum and maximum values can be found by <code>event.min</code> and <code>event.max</code>. When an axis is zoomed all the way out from the "Reset zoom" button, <code>event.min</code> and <code>event.max</code> are null, and the new extremes are set based on <code>this.dataMin</code> and <code>this.dataMax</code>.</p>
    */
  def apply(afterSetExtremes: js.UndefOr[(ColorAxis) => Any] = js.undefined,
    setExtremes: js.UndefOr[(ColorAxis, AxisEvents.SetExtremesEvent) => Any] = js.undefined): ColorAxisEvents = {
    val afterSetExtremesOuter = afterSetExtremes.map(js.ThisFunction.fromFunction1[ColorAxis, Any])
    val setExtremesOuter = setExtremes.map(js.ThisFunction.fromFunction2[ColorAxis, AxisEvents.SetExtremesEvent, Any])

    new ColorAxisEvents {
      override val afterSetExtremes = afterSetExtremesOuter
      override val setExtremes = setExtremesOuter
    }
  }
}
