/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts.config.responsive

import io.udash.wrappers.highcharts.api

import scala.scalajs.js
import scala.scalajs.js.ThisFunction

@js.annotation.ScalaJSDefined
trait ResponsiveRuleCondition extends js.Object {
  /**
    * A callback function to gain complete control on when the responsive rule applies. Return <code>true</code> if it applies.
    * This opens for checking against other metrics than the chart size, or example the document size or other elements.
    */
  val callback: js.UndefOr[js.ThisFunction0[api.Chart, Boolean]] = js.undefined

  /**
    * The responsive rule applies if the chart height is less than this.
    */
  val maxHeight: js.UndefOr[Double] = js.undefined

  /**
    * The responsive rule applies if the chart width is less than this.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/axis/" target="_blank">Max width is 500</a>
    */
  val maxWidth: js.UndefOr[Double] = js.undefined

  /**
    * The responsive rule applies if the chart height is greater than this.
    */
  val minHeight: js.UndefOr[Double] = js.undefined

  /**
    * The responsive rule applies if the chart width is greater than this.
    */
  val minWidth: js.UndefOr[Double] = js.undefined
}

object ResponsiveRuleCondition {
  import scala.scalajs.js.JSConverters._

  /**
    * @param callback A callback function to gain complete control on when the responsive rule applies. Return <code>true</code> if it applies. This opens for checking against other metrics than the chart size, or example the document size or other elements.
    * @param maxHeight The responsive rule applies if the chart height is less than this.
    * @param maxWidth The responsive rule applies if the chart width is less than this.
    * @param minHeight The responsive rule applies if the chart height is greater than this.
    * @param minWidth The responsive rule applies if the chart width is greater than this.
    */
  def apply(callback: js.UndefOr[(api.Chart) => Boolean] = js.undefined,
            maxHeight: js.UndefOr[Double] = js.undefined,
            maxWidth: js.UndefOr[Double] = js.undefined,
            minHeight: js.UndefOr[Double] = js.undefined,
            minWidth: js.UndefOr[Double] = js.undefined): ResponsiveRuleCondition = {
    val callbackOuter = callback.map(ThisFunction.fromFunction1[api.Chart, Boolean])
    val maxHeightOuter = maxHeight
    val maxWidthOuter = maxWidth
    val minHeightOuter = minHeight
    val minWidthOuter = minWidth

    new ResponsiveRuleCondition {
      override val callback = callbackOuter
      override val maxHeight = maxHeightOuter
      override val maxWidth = maxWidthOuter
      override val minHeight = minHeightOuter
      override val minWidth = minWidthOuter
    }
  }
}