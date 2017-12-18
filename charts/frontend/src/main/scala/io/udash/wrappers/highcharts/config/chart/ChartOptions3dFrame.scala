/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package chart

import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.|


trait ChartOptions3dFrame extends js.Object {
  /**
    * Defines the back panel of the frame around 3D charts.
    */
  val back: js.UndefOr[ChartOptions3dFrame.ChartOptions3dFrameData] = js.undefined

  /**
    * The bottom of the frame around a 3D chart.
    */
  val bottom: js.UndefOr[ChartOptions3dFrame.ChartOptions3dFrameData] = js.undefined

  /**
    * The side for the frame around a 3D chart.
    */
  val side: js.UndefOr[ChartOptions3dFrame.ChartOptions3dFrameData] = js.undefined
}

object ChartOptions3dFrame {
  import scala.scalajs.js.JSConverters._

    class ChartOptions3dFrameData extends js.Object {
    /** The color of the panel. */
    val color: js.UndefOr[String | js.Object] = js.undefined

    /** Thickness of the panel. */
    val size: js.UndefOr[Double] = js.undefined
  }

  object ChartOptions3dFrameData {
    /**
      * @param color The color of the panel.
      * @param size Thickness of the panel.
      */
    def apply(color: js.UndefOr[Color] = js.undefined, size: js.UndefOr[Double] = js.undefined): ChartOptions3dFrameData = {
      val colorOuter = color.map(_.c)
      val sizeOuter = size

      new ChartOptions3dFrameData {
        override val color = colorOuter
        override val size = sizeOuter
      }
    }
  }

  /**
    * @param back Defines the back panel of the frame around 3D charts.
    * @param bottom The bottom of the frame around a 3D chart.
    * @param side The side for the frame around a 3D chart.
    */
  def apply(back: js.UndefOr[ChartOptions3dFrameData] = js.undefined,
            bottom: js.UndefOr[ChartOptions3dFrameData] = js.undefined,
            side: js.UndefOr[ChartOptions3dFrameData] = js.undefined): ChartOptions3dFrame = {
    val backOuter = back
    val bottomOuter = bottom
    val sideOuter = side

    new ChartOptions3dFrame {
      override val back = backOuter
      override val bottom = bottomOuter
      override val side = sideOuter
    }
  }
}
