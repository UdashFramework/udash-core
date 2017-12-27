/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts.config.responsive

import io.udash.wrappers.highcharts.config.HighchartsConfig

import scala.scalajs.js

trait ResponsiveRule extends js.Object {
  /**
    * <p>A full set of chart options to apply as overrides to the general chart options.
    * The chart options are applied when the given rule is active.</p>
    *
    * <p>A special case is configuration objects that take arrays, for example
    * <a href="#xAxis">xAxis</a>, <a href="#yAxis">yAxis</a> or <a href="#series">series</a>.
    * For these collections, an <code>id</code> option is used to map the new option set to an existing object.
    * If an existing object of the same id is not found, the first item is updated. So for example,
    * setting <code>chartOptions</code> with a series item without an <code>id</code>, will cause the
    * existing chart's first series to be updated.</p>
    *
    * @example Chart options overrides for <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/axis/" target="_blank">axis</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/legend/" target="_blank">legend</a> and <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/classname/" target="_blank">class name</a>.
    */
  val chartOptions: js.UndefOr[HighchartsConfig] = js.undefined

  /**
    * Under which conditions the rule applies.
    */
  val condition: js.UndefOr[ResponsiveRuleCondition] = js.undefined
}

object ResponsiveRule {
  import scala.scalajs.js.JSConverters._

  /**
    * @param chartOptions <p>A full set of chart options to apply as overrides to the general chart options. The chart options are applied when the given rule is active.</p>. . <p>A special case is configuration objects that take arrays, for example <a href="#xAxis">xAxis</a>, <a href="#yAxis">yAxis</a> or <a href="#series">series</a>. For these collections, an <code>id</code> option is used to map the new option set to an existing object. If an existing object of the same id is not found, the first item is updated. So for example, setting <code>chartOptions</code> with a series item without an <code>id</code>, will cause the existing chart's first series to be updated.</p>
    * @param condition Under which conditions the rule applies.
    */
  def apply(chartOptions: HighchartsConfig, condition: ResponsiveRuleCondition): ResponsiveRule = {
    val chartOptionsOuter = chartOptions
    val conditionOuter = condition

    new ResponsiveRule {
      override val chartOptions: js.UndefOr[HighchartsConfig] = chartOptionsOuter
      override val condition: js.UndefOr[ResponsiveRuleCondition] = conditionOuter
    }
  }
}