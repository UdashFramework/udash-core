/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js

trait SeriesStates[Hover <: SeriesStatesHover] extends js.Object {

  /**
    * Options for the hovered series
    */
  val hover: js.UndefOr[Hover] = js.undefined
}

trait SeriesAreaStates extends SeriesStates[SeriesAreaStatesHover]

trait SeriesBarStates extends SeriesStates[SeriesBarStatesHover]

object SeriesAreaStates {
  import scala.scalajs.js.JSConverters._

  /**
    * @param hover Options for the hovered series
    */
  def apply(hover: js.UndefOr[SeriesAreaStatesHover] = js.undefined): SeriesAreaStates = {
    val hoverOuter = hover
    new SeriesAreaStates {
      override val hover: js.UndefOr[SeriesAreaStatesHover] = hoverOuter
    }
  }
}

object SeriesBarStates {
  import scala.scalajs.js.JSConverters._

  /**
    * @param hover Options for the hovered series
    */
  def apply(hover: js.UndefOr[SeriesBarStatesHover] = js.undefined): SeriesBarStates = {
    val hoverOuter = hover
    new SeriesBarStates {
      override val hover: js.UndefOr[SeriesBarStatesHover] = hoverOuter
    }
  }
}
