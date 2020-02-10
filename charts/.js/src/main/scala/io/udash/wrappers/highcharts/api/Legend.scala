/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import scala.scalajs.js

@js.native
trait Legend extends js.Object {
  /**
    * Update the legend with new options.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/legend/legend-update/" target="_blank">Legend update</a>
    */
  def update(options: config.legend.Legend, redraw: Boolean = js.native): Unit = js.native
}
