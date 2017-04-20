/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series

import scala.scalajs.js

@js.annotation.ScalaJSDefined
class SeriesHoverHalo extends js.Object {
  /**
    * A collection of SVG attributes to override the appearance of the halo,
    * for example <code>fill</code>, <code>stroke</code> and <code>stroke-width</code>.
    */
  val attributes: js.UndefOr[js.Object] = js.undefined

  /**
    * Opacity for the halo unless a specific fill is overridden using the <code>attributes</code> setting.
    * Note that Highcharts is only able to apply opacity to colors of hex or rgb(a) formats.
    */
  val opacity: js.UndefOr[Double] = js.undefined

  /**
    * The pixel size of the halo. For point markers this is the radius of the halo. For pie slices it is the width of
    * the halo outside the slice. For bubbles it defaults to 5 and is the width of the halo outside the bubble.
    */
  val size: js.UndefOr[Double] = js.undefined
}

object SeriesHoverHalo {
  import scala.scalajs.js.JSConverters._

  /**
    * @param attributes A collection of SVG attributes to override the appearance of the halo, for example <code>fill</code>, <code>stroke</code> and <code>stroke-width</code>.
    * @param opacity Opacity for the halo unless a specific fill is overridden using the <code>attributes</code> setting. Note that Highcharts is only able to apply opacity to colors of hex or rgb(a) formats.
    * @param size The pixel size of the halo. For point markers this is the radius of the halo. For pie slices it is the width of the halo outside the slice. For bubbles it defaults to 5 and is the width of the halo outside the bubble.
    */
  def apply(attributes: js.UndefOr[js.Object] = js.undefined, opacity: js.UndefOr[Double] = js.undefined, size: js.UndefOr[Double] = js.undefined): SeriesHoverHalo = {
    val attributesOuter = attributes
    val opacityOuter = opacity
    val sizeOuter = size

    new SeriesHoverHalo {
      override val attributes = attributesOuter
      override val opacity = opacityOuter
      override val size = sizeOuter
    }
  }
}
