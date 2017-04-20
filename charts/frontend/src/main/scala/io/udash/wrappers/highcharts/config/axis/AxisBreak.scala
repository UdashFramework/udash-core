/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import scala.scalajs.js


@js.annotation.ScalaJSDefined
class AxisBreak extends js.Object {

  /**
    * A number indicating how much space should be left between the start and the end of the break.
    * The break size is given in axis units, so for instance on a <code>datetime</code> axis,
    * a break size of 3600000 would indicate the equivalent of an hour.
    */
  val breakSize: js.UndefOr[Double] = js.undefined

  /**
    * The point where the break starts.
    */
  val from: js.UndefOr[Double] = js.undefined

  /**
    * Defines an interval after which the break appears again. By default the breaks do not repeat.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/axisbreak/break-repeated/">Repeated Break</a>
    */
  val repeat: js.UndefOr[Double] = js.undefined

  /**
    * The point where the break ends.
    */
  val to: js.UndefOr[Double] = js.undefined
}

object AxisBreak {
  import scala.scalajs.js.JSConverters._

  /**
    * @param breakSize A number indicating how much space should be left between the start and the end of the break. The break size is given in axis units, so for instance on a <code>datetime</code> axis, a break size of 3600000 would indicate the equivalent of an hour.
    * @param from The point where the break starts.
    * @param repeat Defines an interval after which the break appears again. By default the breaks do not repeat.
    * @param to The point where the break ends.
    */
  def apply(breakSize: js.UndefOr[Double] = js.undefined,
            from: js.UndefOr[Double] = js.undefined,
            repeat: js.UndefOr[Double] = js.undefined,
            to: js.UndefOr[Double] = js.undefined): AxisBreak = {
    val breakSizeOuter = breakSize
    val fromOuter = from
    val repeatOuter = repeat
    val toOuter = to

    new AxisBreak {
      override val breakSize = breakSizeOuter
      override val from = fromOuter
      override val repeat = repeatOuter
      override val to = toOuter
    }
  }
}
