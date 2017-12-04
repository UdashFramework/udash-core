/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package credits

import io.udash.wrappers.highcharts.config.utils.Position

import scala.scalajs.js


@js.annotation.ScalaJSDefined
trait Credits extends js.Object {

  /**
    * Whether to show the credits text.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/credits/enabled-false/" target="_blank">Credits disabled</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * The URL for the credits label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/credits/href/" target="_blank">Custom URL and text</a>
    */
  val href: js.UndefOr[String] = js.undefined

  /**
    * Position configuration for the credits label. Supported properties are  <code>align</code>, <code>verticalAlign</code>, <code>x</code> and <code>y</code>. Defaults to 
    * <pre>position: {
    * align: 'right',
    * x: -10,
    * verticalAlign: 'bottom',
    * y: -5
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/credits/position-left/" target="_blank">Left aligned</a>
    */
  val position: js.UndefOr[Position] = js.undefined

  /**
    * CSS styles for the credits label. Defaults to:
    * <pre>style: {
    * cursor: 'pointer',
    * color: '#909090',
    * fontSize: '10px'
    *
    * }</pre>
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * The text for the credits label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/credits/href/" target="_blank">Custom URL and text</a>
    */
  val text: js.UndefOr[String] = js.undefined
}

object Credits {
  import scala.scalajs.js.JSConverters._

  /**
    * @param enabled  Whether to show the credits text.
    * @param href     The URL for the credits label.
    * @param position Position configuration for the credits label. Supported properties are  <code>align</code>, <code>verticalAlign</code>, <code>x</code> and <code>y</code>. Defaults to . <pre>position: {. 	align: 'right',. 	x: -10,. 	verticalAlign: 'bottom',. 	y: -5. }</pre>
    * @param style    CSS styles for the credits label. Defaults to:. <pre>style: {. 	cursor: 'pointer',. 	color: '#909090',. 	fontSize: '10px'. . }</pre>
    * @param text     The text for the credits label.
    */
  def apply(enabled: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            position: js.UndefOr[Position] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            text: js.UndefOr[String] = js.undefined): Credits = {
    val enabledOuter = enabled
    val hrefOuter = href
    val positionOuter = position
    val styleOuter = style.map(stringToStyleObject)
    val textOuter = text

    new Credits {
      override val enabled = enabledOuter
      override val href = hrefOuter
      override val position = positionOuter
      override val style = styleOuter
      override val text = textOuter
    }
  }
}
