/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.{ThisFunction, `|`}

@js.annotation.ScalaJSDefined
abstract class AxisPlotBand extends js.Object {
  /**
    * Border color for the plot band. Also requires <code>borderWidth</code> to be set.
    */
  val borderColor: js.UndefOr[String | js.Object] = js.undefined

  /**
    * Border width for the plot band.  Also requires <code>borderColor</code> to be set.
    */
  val borderWidth: js.UndefOr[Double] = js.undefined

  /**
    * A custom class name, in addition to the default <code>highcharts-plot-band</code>, to apply to each individual band.
    */
  val className: js.UndefOr[String] = js.undefined

  /**
    * The color of the plot band.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-color/" target="_blank">Color band</a>
    */
  val color: js.UndefOr[String | js.Object] = js.undefined

  /**
    * An object defining mouse events for the plot band. Supported properties are <code>click</code>,
    * <code>mouseover</code>, <code>mouseout</code>, <code>mousemove</code>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-events/" target="_blank">Mouse events demonstrated</a>
    */
  val events: js.UndefOr[js.Object] = js.undefined

  /**
    * The start position of the plot band in axis units.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-color/" target="_blank">Datetime axis</a>,
			<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-from/" target="_blank">categorized axis</a>,
    */
  val from: js.UndefOr[Double] = js.undefined

  /**
    * An id used for identifying the plot band in Axis.removePlotBand.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-id/" target="_blank">Remove plot band by id</a>
    */
  val id: js.UndefOr[String] = js.undefined

  /**
    * Text labels for the plot bands
    */
  val label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined

  /**
    * The end position of the plot band in axis units.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-color/" target="_blank">Datetime axis</a>,
			<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-from/" target="_blank">categorized axis</a>,
    */
  val to: js.UndefOr[Double] = js.undefined

  /**
    * The z index of the plot band within the chart, relative to other elements. Using the same z index as another element may give unpredictable results, as the last rendered element will be on top. Values from 0 to 20 make sense.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-color/" target="_blank">Behind plot lines by default</a>,
			<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-zindex/" target="_blank">above plot lines</a>,
			<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-zindex-above-series/" target="_blank">above plot lines and series</a>.
    */
  val zIndex: js.UndefOr[Int] = js.undefined
}

@js.annotation.ScalaJSDefined
class XAxisPlotBand extends AxisPlotBand

@js.annotation.ScalaJSDefined
class YAxisPlotBand extends AxisPlotBand {
  /**
    * In a gauge chart, this option determines the inner radius of the plot band that stretches along the perimeter.
    * It can be given as a percentage string, like <code>"100%"</code>, or as a pixel number, like <code>100</code>.
    * By default, the inner radius is controlled by the <a href="#yAxis.plotBands.thickness">thickness</a> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-gauge" target="_blank">Gauge plot band</a>
    */
  val innerRadius: js.UndefOr[Double | String] = js.undefined

  /**
    * In a gauge chart, this option determines the outer radius of the plot band that stretches along the perimeter.
    * It can be given as a percentage string, like <code>"100%"</code>, or as a pixel number, like <code>100</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-gauge" target="_blank">Gauge plot band</a>
    */
  val outerRadius: js.UndefOr[Double | String] = js.undefined

  /**
    * In a gauge chart, this option sets the width of the plot band stretching along the perimeter.
    * It can be given as a percentage string, like <code>"10%"</code>, or as a pixel number, like <code>10</code>.
    * The default value 10 is the same as the default <a href="#yAxis.tickLength">tickLength</a>,
    * thus making the plot band act as a background for the tick markers.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/xaxis/plotbands-gauge" target="_blank">Gauge plot band</a>
    */
  val thickness: js.UndefOr[Double | String] = js.undefined
}

@js.annotation.ScalaJSDefined
class ZAxisPlotBand extends AxisPlotBand

object XAxisPlotBand {
  import scala.scalajs.js.JSConverters._
  /**
    * @param borderColor Border color for the plot band. Also requires <code>borderWidth</code> to be set.
    * @param borderWidth Border width for the plot band.  Also requires <code>borderColor</code> to be set.
    * @param className A custom class name, in addition to the default <code>highcharts-plot-band</code>, to apply to each individual band.
    * @param color The color of the plot band.
    * @param onClick <code>click</code> event handler.
    * @param onMouseOver <code>mouseover</code> event handler.
    * @param onMouseOut <code>mouseout</code> event handler.
    * @param onMouseMove <code>mousemove</code> event handler.
    * @param from The start position of the plot band in axis units.
    * @param id An id used for identifying the plot band in Axis.removePlotBand.
    * @param label Text labels for the plot bands
    * @param to The end position of the plot band in axis units.
    * @param zIndex The z index of the plot band within the chart, relative to other elements. Using the same z index as another element may give unpredictable results, as the last rendered element will be on top. Values from 0 to 20 make sense.
    */
  def apply(borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            onClick: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOver: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOut: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseMove: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            from: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined,
            to: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): XAxisPlotBand = {
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val eventsOuter: js.UndefOr[js.Object] = js.Dynamic.literal(
      click = onClick.map(ThisFunction.fromFunction1),
      mouseover = onMouseOver.map(ThisFunction.fromFunction1),
      mouseout = onMouseOut.map(ThisFunction.fromFunction1),
      mousemove = onMouseMove.map(ThisFunction.fromFunction1)
    )
    val fromOuter = from
    val idOuter = id
    val labelOuter = label
    val toOuter = to
    val zIndexOuter = zIndex

    new XAxisPlotBand {
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val events = eventsOuter
      override val from = fromOuter
      override val id = idOuter
      override val label = labelOuter
      override val to = toOuter
      override val zIndex = zIndexOuter
    }
  }
}

object YAxisPlotBand {
  import scala.scalajs.js.JSConverters._
  /**
    * @param borderColor Border color for the plot band. Also requires <code>borderWidth</code> to be set.
    * @param borderWidth Border width for the plot band.  Also requires <code>borderColor</code> to be set.
    * @param className A custom class name, in addition to the default <code>highcharts-plot-band</code>, to apply to each individual band.
    * @param color The color of the plot band.
    * @param onClick <code>click</code> event handler.
    * @param onMouseOver <code>mouseover</code> event handler.
    * @param onMouseOut <code>mouseout</code> event handler.
    * @param onMouseMove <code>mousemove</code> event handler.
    * @param from The start position of the plot band in axis units.
    * @param id An id used for identifying the plot band in Axis.removePlotBand.
    * @param innerRadius In a gauge chart, this option determines the inner radius of the plot band that stretches along the perimeter. It can be given as a percentage string, like <code>"100%"</code>, or as a pixel number, like <code>100</code>. By default, the inner radius is controlled by the <a href="#yAxis.plotBands.thickness">thickness</a> option.
    * @param label Text labels for the plot bands
    * @param outerRadius In a gauge chart, this option determines the outer radius of the plot band that stretches along the perimeter. It can be given as a percentage string, like <code>"100%"</code>, or as a pixel number, like <code>100</code>.
    * @param thickness In a gauge chart, this option sets the width of the plot band stretching along the perimeter. It can be given as a percentage string, like <code>"10%"</code>, or as a pixel number, like <code>10</code>. The default value 10 is the same as the default <a href="#yAxis.tickLength">tickLength</a>, thus making the plot band act as a background for the tick markers.
    * @param to The end position of the plot band in axis units.
    * @param zIndex The z index of the plot band within the chart, relative to other elements. Using the same z index as another element may give unpredictable results, as the last rendered element will be on top. Values from 0 to 20 make sense.
    */
  def apply(borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            onClick: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOver: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOut: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseMove: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            from: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            innerRadius: js.UndefOr[Double | String] = js.undefined,
            label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined,
            outerRadius: js.UndefOr[Double | String] = js.undefined,
            thickness: js.UndefOr[Double | String] = js.undefined,
            to: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): YAxisPlotBand = {
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val eventsOuter: js.UndefOr[js.Object] = js.Dynamic.literal(
      click = onClick.map(ThisFunction.fromFunction1),
      mouseover = onMouseOver.map(ThisFunction.fromFunction1),
      mouseout = onMouseOut.map(ThisFunction.fromFunction1),
      mousemove = onMouseMove.map(ThisFunction.fromFunction1)
    )
    val fromOuter = from
    val idOuter = id
    val innerRadiusOuter = innerRadius
    val labelOuter = label
    val outerRadiusOuter = outerRadius
    val thicknessOuter = thickness
    val toOuter = to
    val zIndexOuter = zIndex

    new YAxisPlotBand {
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val events = eventsOuter
      override val from = fromOuter
      override val id = idOuter
      override val innerRadius = innerRadiusOuter
      override val label = labelOuter
      override val outerRadius = outerRadiusOuter
      override val thickness = thicknessOuter
      override val to = toOuter
      override val zIndex = zIndexOuter
    }
  }
}

object ZAxisPlotBand {
  import scala.scalajs.js.JSConverters._
  /**
    * @param borderColor Border color for the plot band. Also requires <code>borderWidth</code> to be set.
    * @param borderWidth Border width for the plot band.  Also requires <code>borderColor</code> to be set.
    * @param className A custom class name, in addition to the default <code>highcharts-plot-band</code>, to apply to each individual band.
    * @param color The color of the plot band.
    * @param onClick <code>click</code> event handler.
    * @param onMouseOver <code>mouseover</code> event handler.
    * @param onMouseOut <code>mouseout</code> event handler.
    * @param onMouseMove <code>mousemove</code> event handler.
    * @param from The start position of the plot band in axis units.
    * @param id An id used for identifying the plot band in Axis.removePlotBand.
    * @param label Text labels for the plot bands
    * @param to The end position of the plot band in axis units.
    * @param zIndex The z index of the plot band within the chart, relative to other elements. Using the same z index as another element may give unpredictable results, as the last rendered element will be on top. Values from 0 to 20 make sense.
    */
  def apply(borderColor: js.UndefOr[Color] = js.undefined,
            borderWidth: js.UndefOr[Double] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[Color] = js.undefined,
            onClick: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOver: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseOut: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            onMouseMove: js.UndefOr[(AxisPlotBand) => Any] = js.undefined,
            from: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            label: js.UndefOr[AxisPlotLineOrBandLabel] = js.undefined,
            to: js.UndefOr[Double] = js.undefined,
            zIndex: js.UndefOr[Int] = js.undefined): XAxisPlotBand = {
    val borderColorOuter = borderColor.map(_.c)
    val borderWidthOuter = borderWidth
    val classNameOuter = className
    val colorOuter = color.map(_.c)
    val eventsOuter: js.UndefOr[js.Object] = js.Dynamic.literal(
      click = onClick.map(ThisFunction.fromFunction1),
      mouseover = onMouseOver.map(ThisFunction.fromFunction1),
      mouseout = onMouseOut.map(ThisFunction.fromFunction1),
      mousemove = onMouseMove.map(ThisFunction.fromFunction1)
    )
    val fromOuter = from
    val idOuter = id
    val labelOuter = label
    val toOuter = to
    val zIndexOuter = zIndex

    new XAxisPlotBand {
      override val borderColor = borderColorOuter
      override val borderWidth = borderWidthOuter
      override val className = classNameOuter
      override val color = colorOuter
      override val events = eventsOuter
      override val from = fromOuter
      override val id = idOuter
      override val label = labelOuter
      override val to = toOuter
      override val zIndex = zIndexOuter
    }
  }
}