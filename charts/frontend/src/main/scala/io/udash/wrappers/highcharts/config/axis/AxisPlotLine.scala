/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Color, DashStyle}

import scala.scalajs.js
import scala.scalajs.js.{ThisFunction, |}

@js.annotation.ScalaJSDefined
trait AxisPlotLine extends js.Object {

  /**
    * A custom class name, in addition to the default <code>highcharts-plot-line</code>, to apply to each individual line.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * The color of the line.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-color/" target="_blank">A red line from X axis</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * The dashing or dot style for the plot line. For possible values see
    * <a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this overview</a>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-dashstyle/" target="_blank">Dash and dot pattern</a>
    */
  val dashStyle: js.UndefOr[String] = js.undefined

  /**
    * An object defining mouse events for the plot line. Supported properties are <code>click</code>,
    * <code>mouseover</code>, <code>mouseout</code>, <code>mousemove</code>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-events/" target="_blank">Mouse events demonstrated</a>
    */
  val events: js.UndefOr[js.Object] = js.undefined

  /**
    * An id used for identifying the plot line in Axis.removePlotLine.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-id/" target="_blank">Remove plot line by id</a>
    */
  val id: js.UndefOr[String] = js.undefined

  /**
    * Text labels for the plot bands
    */
  val label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined

  /**
    * The position of the line in axis units.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-color/" target="_blank">Between two categories on X axis</a>
    */
  val value: js.UndefOr[Double] = js.undefined

  /**
    * The width or thickness of the plot line.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-color/" target="_blank">2px wide line from X axis</a>
    */
  val width: js.UndefOr[Double] = js.undefined

  /**
    * The z index of the plot line within the chart.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-zindex-behind/" target="_blank">Behind plot lines by default</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-zindex-above/" target="_blank">above plot lines</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotlines-zindex-above-all/" target="_blank">above plot lines and series</a>.
    */
  val zIndex: js.UndefOr[Int] = js.undefined
}

object AxisPlotLine {
  import scala.scalajs.js.JSConverters._

  case class EventHandlers()

  /**
    * @param className A custom class name, in addition to the default <code>highcharts-plot-line</code>, to apply to each individual line.
    * @param color The color of the line.
    * @param dashStyle The dashing or dot style for the plot line. For possible values see <a href="http://jsfiddle.net/gh/get/jquery/1.7.1/highslide-software/highcharts.com/tree/master/samples/highcharts/plotoptions/series-dashstyle-all/">this overview</a>.
    * @param onClick <code>click</code> event handler.
    * @param onMouseOver <code>mouseover</code> event handler.
    * @param onMouseOut <code>mouseout</code> event handler.
    * @param onMouseMove <code>mousemove</code> event handler.
    * @param id An id used for identifying the plot line in Axis.removePlotLine.
    * @param label Text labels for the plot bands
    * @param value The position of the line in axis units.
    * @param width The width or thickness of the plot line.
    * @param zIndex The z index of the plot line within the chart.
    */
  def apply(className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            dashStyle: js.UndefOr[DashStyle] = js.undefined,
            onClick: js.UndefOr[(AxisPlotLine) => Any] = js.undefined,
            onMouseOver: js.UndefOr[(AxisPlotLine) => Any] = js.undefined,
            onMouseOut: js.UndefOr[(AxisPlotLine) => Any] = js.undefined,
            onMouseMove: js.UndefOr[(AxisPlotLine) => Any] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined,
            value: js.UndefOr[Double] = js.undefined,
            width: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): AxisPlotLine = {
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val dashStyleOuter = dashStyle.map(_.name)
    val eventsOuter: js.UndefOr[js.Object] = js.Dynamic.literal(
      click = onClick.map(ThisFunction.fromFunction1),
      mouseover = onMouseOver.map(ThisFunction.fromFunction1),
      mouseout = onMouseOut.map(ThisFunction.fromFunction1),
      mousemove = onMouseMove.map(ThisFunction.fromFunction1)
    )
    val idOuter = id
    val labelOuter = label
    val valueOuter = value
    val widthOuter = width
    val zIndexOuter = zIndex

    new AxisPlotLine {
      override val className = classNameOuter
      override val color = colorOuter
      override val dashStyle = dashStyleOuter
      override val events = eventsOuter
      override val id = idOuter
      override val label = labelOuter
      override val value = valueOuter
      override val width = widthOuter
      override val zIndex = zIndexOuter
    }
  }
}
