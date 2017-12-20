/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js

trait BaseSeriesMarkerStates[Hover <: BaseSeriesMarkerHover] extends js.Object {
  val hover: js.UndefOr[Hover] = js.undefined
}

trait SeriesMarkerStates extends BaseSeriesMarkerStates[SeriesMarkerHover] {
  val select: js.UndefOr[SeriesMarkerSelect] = js.undefined
}

trait SeriesDataMarkerStates extends BaseSeriesMarkerStates[SeriesDataMarkerHover]

object SeriesMarkerStates {
  import scala.scalajs.js.JSConverters._

  def apply(hover: js.UndefOr[SeriesMarkerHover] = js.undefined, select: js.UndefOr[SeriesMarkerSelect] = js.undefined): SeriesMarkerStates = {
    val hoverOuter = hover
    val selectOuter = select
    new SeriesMarkerStates {
      override val hover = hoverOuter
      override val select = selectOuter
    }
  }
}

object SeriesDataMarkerStates {
  import scala.scalajs.js.JSConverters._

  def apply(hover: js.UndefOr[SeriesDataMarkerHover] = js.undefined): SeriesDataMarkerStates = {
    val hoverOuter = hover
    new SeriesDataMarkerStates {
      override val hover = hoverOuter
    }
  }
}
