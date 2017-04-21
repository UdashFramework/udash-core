/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js

@js.annotation.ScalaJSDefined
class BaseSeriesMarkerStates[Hover <: BaseSeriesMarkerHover] extends js.Object {
  val hover: js.UndefOr[Hover] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesMarkerStates extends BaseSeriesMarkerStates[SeriesMarkerHover] {
  val select: js.UndefOr[SeriesMarkerSelect] = js.undefined
}

@js.annotation.ScalaJSDefined
class SeriesDataMarkerStates extends BaseSeriesMarkerStates[SeriesDataMarkerHover]

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
