/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package placeholders

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js


@js.annotation.ScalaJSDefined
class Loading extends js.Object {

  /**
    * The duration in milliseconds of the fade out effect.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/loading/hideduration/" target="_blank">Fade in and out over a second</a>
    */
  val hideDuration: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for the loading label <code>span</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/loading/labelstyle/" target="_blank">Vertically centered</a>
    */
  val labelStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * The duration in milliseconds of the fade in effect.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/loading/hideduration/" target="_blank">Fade in and out over a second</a>
    */
  val showDuration: js.UndefOr[Double] = js.undefined

  /**
    * CSS styles for the loading screen that covers the plot area. Defaults to:
    * <pre>style: {
    * position: 'absolute',
    * backgroundColor: 'white',
    * opacity: 0.5,
    * textAlign: 'center'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/loading/style/" target="_blank">Gray plot area, white text</a>
    */
  val style: js.UndefOr[js.Object] = js.undefined
}

object Loading {
  import scala.scalajs.js.JSConverters._

  /**
    * @param hideDuration The duration in milliseconds of the fade out effect.
    * @param labelStyle   CSS styles for the loading label <code>span</code>.
    * @param showDuration The duration in milliseconds of the fade in effect.
    * @param style        CSS styles for the loading screen that covers the plot area. Defaults to:. <pre>style: {. 	position: 'absolute',. 	backgroundColor: 'white',. 	opacity: 0.5,. 	textAlign: 'center'. }</pre>
    */
  def apply(hideDuration: js.UndefOr[FiniteDuration] = js.undefined,
            labelStyle: js.UndefOr[String] = js.undefined,
            showDuration: js.UndefOr[FiniteDuration] = js.undefined,
            style: js.UndefOr[String] = js.undefined): Loading = {

    val hideDurationOuter = hideDuration.map(_.toMillis.toDouble)
    val labelStyleOuter = labelStyle.map(stringToStyleObject)
    val showDurationOuter = showDuration.map(_.toMillis.toDouble)
    val styleOuter = style.map(stringToStyleObject)

    new Loading {
      override val hideDuration = hideDurationOuter
      override val labelStyle = labelStyleOuter
      override val showDuration = showDurationOuter
      override val style = styleOuter
    }
  }
}
