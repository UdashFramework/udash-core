package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
class ColorAxisDataClasses extends js.Object {

  /**
    * The color of each data class. If not set, the color is pulled from the global or chart-specific <a href="#colors">colors</a> array.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/demo/data-class-two-ranges/" target="_blank">Explicit colors</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The start of the value range that the data class represents, relating to the point value.
    */
  val from: js.UndefOr[Double] = js.undefined

  /**
    * The name of the data class as it appears in the legend. If no name is given, it is automatically created based
    * on the <code>from</code> and <code>to</code> values. For full programmatic control, <a href="#legend.labelFormatter">legend.labelFormatter</a>
    * can be used. In the formatter, <code>this.from</code> and <code>this.to</code> can be accessed.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/dataclasses-name/" target="_blank">Named data classes</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/maps/coloraxis/dataclasses-labelformatter/" target="_blank">formatted data classes</a>.
    */
  val name: js.UndefOr[String] = js.undefined

  /**
    * The end of the value range that the data class represents, relating to the point value.
    */
  val to: js.UndefOr[Double] = js.undefined
}

object ColorAxisDataClasses {
  import scala.scalajs.js.JSConverters._

  /**
    * @param color The color of each data class. If not set, the color is pulled from the global or chart-specific <a href="#colors">colors</a> array.
    * @param from  The start of the value range that the data class represents, relating to the point value.
    * @param name  The name of the data class as it appears in the legend. If no name is given, it is automatically created based on the <code>from</code> and <code>to</code> values. For full programmatic control, <a href="#legend.labelFormatter">legend.labelFormatter</a> can be used. In the formatter, <code>this.from</code> and <code>this.to</code> can be accessed.
    * @param to    The end of the value range that the data class represents, relating to the point value.
    */
  def apply(color: js.UndefOr[Color] = js.undefined, from: js.UndefOr[Double] = js.undefined, name: js.UndefOr[String] = js.undefined, to: js.UndefOr[Double] = js.undefined): ColorAxisDataClasses = {
    val colorOuter = color.map(_.c)
    val fromOuter = from
    val nameOuter = name
    val toOuter = to

    new ColorAxisDataClasses {
      override val color = colorOuter
      override val from = fromOuter
      override val name = nameOuter
      override val to = toOuter
    }
  }
}