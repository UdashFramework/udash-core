/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts.config.responsive

import scala.scalajs.js

@js.annotation.ScalaJSDefined
class Responsive extends js.Object {

  /**
    * A set of rules for responsive settings. The rules are executed from the top down.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/axis/" target="_blank">Axis changes</a>
    */
  val rules: js.UndefOr[js.Array[ResponsiveRule]] = js.undefined
}

object Responsive {
  import scala.scalajs.js.JSConverters._

  /**
    * @param rules A set of rules for responsive settings. The rules are executed from the top down.
    */
  def apply(rules: Seq[ResponsiveRule]): Responsive = {
    val rulesOuter = rules.toJSArray
    new Responsive {
      override val rules: js.UndefOr[js.Array[ResponsiveRule]] = rulesOuter
    }
  }
}