/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package chart

import scala.scalajs.js


@js.annotation.ScalaJSDefined
trait ChartOptions3d extends js.Object {

  /**
    * One of the two rotation angles for the chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/3d-column-interactive/">Dynamic Angles</a>
    */
  val alpha: js.UndefOr[Double] = js.undefined

  /**
    * One of the two rotation angles for the chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/3d-column-interactive/">Dynamic Angles</a>
    */
  val beta: js.UndefOr[Double] = js.undefined

  /**
    * The total depth of the chart.
    */
  val depth: js.UndefOr[Double] = js.undefined

  /**
    * Wether to render the chart using the 3D functionality.
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * Whether the 3d box should automatically adjust to the chart plot area.
    */
  val fitToPlot: js.UndefOr[Boolean] = js.undefined

  /**
    * Provides the option to draw a frame around the charts by defining a bottom, front and back panel.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/3d/column-frame/">Framed column chart</a>
    */
  val frame: js.UndefOr[ChartOptions3dFrame] = js.undefined

  /**
    * Defines the distance the viewer is standing in front of the chart, this setting is important to calculate the perspective effect in column and scatter charts.
    * It is not used for 3D pie charts.
    */
  val viewDistance: js.UndefOr[Double] = js.undefined
}

object ChartOptions3d {
  import scala.scalajs.js.JSConverters._

  /**
    * @param alpha        One of the two rotation angles for the chart.
    * @param beta         One of the two rotation angles for the chart.
    * @param depth        The total depth of the chart.
    * @param enabled      Wether to render the chart using the 3D functionality.
    * @param fitToPlot    Whether the 3d box should automatically adjust to the chart plot area.
    * @param frame        Provides the option to draw a frame around the charts by defining a bottom, front and back panel.
    * @param viewDistance Defines the distance the viewer is standing in front of the chart, this setting is important to calculate the perspective effect in column and scatter charts.. It is not used for 3D pie charts.
    */
  def apply(alpha: js.UndefOr[Double] = js.undefined,
            beta: js.UndefOr[Double] = js.undefined,
            depth: js.UndefOr[Double] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            fitToPlot: js.UndefOr[Boolean] = js.undefined,
            frame: js.UndefOr[ChartOptions3dFrame] = js.undefined,
            viewDistance: js.UndefOr[Double] = js.undefined): ChartOptions3d = {
    val alphaOuter = alpha
    val betaOuter = beta
    val depthOuter = depth
    val enabledOuter = enabled
    val fitToPlotOuter = fitToPlot
    val frameOuter = frame
    val viewDistanceOuter = viewDistance

    new ChartOptions3d {
      override val alpha = alphaOuter
      override val beta = betaOuter
      override val depth = depthOuter
      override val enabled = enabledOuter
      override val fitToPlot = fitToPlotOuter
      override val frame = frameOuter
      override val viewDistance = viewDistanceOuter
    }
  }
}
