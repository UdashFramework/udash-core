package io.udash.wrappers.highcharts
package config
package axis

import io.udash.wrappers.highcharts.config.utils.{Animation, Color}

import scala.scalajs.js
import scala.scalajs.js.`|`

/**
  * @note JavaScript name: <code>colorAxis-marker</code>
  */
@js.annotation.ScalaJSDefined
class ColorAxisMarker extends js.Object {

  /**
    * Animation for the marker as it moves between values. Set to <code>false</code> to disable animation. Defaults to <code>{ duration: 50 }</code>.
    */
  val animation: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * The color of the marker.
    */
  val color: js.UndefOr[String | js.Object] = js.undefined
}

object ColorAxisMarker {
  import scala.scalajs.js.JSConverters._

  /**
    * @param animation Animation for the marker as it moves between values. Set to <code>false</code> to disable animation. Defaults to <code>{ duration: 50 }</code>.
    * @param color     The color of the marker.
    */
  def apply(animation: js.UndefOr[Animation] = js.undefined, color: js.UndefOr[Color] = js.undefined): ColorAxisMarker = {
    val animationOuter = animation.map(_.value)
    val colorOuter = color.map(_.c)

    new ColorAxisMarker {
      override val animation = animationOuter
      override val color = colorOuter
    }
  }
}