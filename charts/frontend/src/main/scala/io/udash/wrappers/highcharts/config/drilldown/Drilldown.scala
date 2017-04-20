/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package drilldown

import io.udash.wrappers.highcharts.config.series.Series
import io.udash.wrappers.highcharts.config.utils.Animation

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
class Drilldown extends js.Object {

  /**
    * Additional styles to apply to the X axis label for a point that has drilldown data. By default it is underlined and blue to invite to interaction. Defaults to:
    * <pre>activeAxisLabelStyle: {
    * cursor: 'pointer',
    * color: '#0d233a',
    * fontWeight: 'bold',
    * textDecoration: 'underline'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/labels/" target="_blank">Label styles</a>
    */
  val activeAxisLabelStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * Additional styles to apply to the data label of a point that has drilldown data. By default it is underlined and blue to invite to interaction. Defaults to:
    * <pre>activeAxisLabelStyle: {
    * cursor: 'pointer',
    * color: '#0d233a',
    * fontWeight: 'bold',
    * textDecoration: 'underline'
    * }</pre>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/labels/" target="_blank">Label styles</a>
    */
  val activeDataLabelStyle: js.UndefOr[js.Object] = js.undefined

  /**
    * When this option is false, clicking a single point will drill down all points in the same category, equivalent to clicking the X axis label.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/allowpointdrilldown-false/" target="_blank">Don't allow point drilldown</a>.
    */
  val allowPointDrilldown: js.UndefOr[Boolean] = js.undefined

  /**
    * <p>Set the animation for all drilldown animations. Animation of a drilldown occurs when drilling between
    * a column point and a column series, or a pie slice and a full pie series. Drilldown can still be used between
    * series and points of different types, but animation will not occur.</p>
    *
    * <p>The animation can either be set as a boolean or a configuration object. If <code>true</code>,
    * it will use the 'swing' jQuery easing and a duration of 500 ms. If used as a configuration object,
    * the following properties are supported:
    * </p><dl>
    * <dt>duration</dt>
    * <dd>The duration of the animation in milliseconds.</dd>
    *
    * <dt>easing</dt>
    * <dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>
    * </dl>
    */
  val animation: js.UndefOr[Boolean | js.Object] = js.undefined

  /**
    * Options for the drill up button that appears when drilling down on a series. The text for the button is defined in <a href="#lang.drillUpText">lang.drillUpText</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/drillupbutton/" target="_blank">Drill up button</a>
    */
  val drillUpButton: js.UndefOr[DrilldownDrillUpButton] = js.undefined

  /**
    * An array of series configurations for the drill down. Each series configuration uses the same syntax
    * as the <a href="#series">series</a> option set. These drilldown series are hidden by default.
    * The drilldown series is linked to the parent series' point by its <code>id</code>.
    */
  val series: js.UndefOr[js.Array[Series]] = js.undefined
}

object Drilldown {
  import scala.scalajs.js.JSConverters._

  /**
    * @param activeAxisLabelStyle Additional styles to apply to the X axis label for a point that has drilldown data. By default it is underlined and blue to invite to interaction. Defaults to:. <pre>activeAxisLabelStyle: {. 	cursor: 'pointer',. 	color: '#0d233a',. 	fontWeight: 'bold',. 	textDecoration: 'underline'			. }</pre>
    * @param activeDataLabelStyle Additional styles to apply to the data label of a point that has drilldown data. By default it is underlined and blue to invite to interaction. Defaults to:. <pre>activeAxisLabelStyle: {. 	cursor: 'pointer',. 	color: '#0d233a',. 	fontWeight: 'bold',. 	textDecoration: 'underline'			. }</pre>
    * @param allowPointDrilldown  When this option is false, clicking a single point will drill down all points in the same category, equivalent to clicking the X axis label.
    * @param animation            <p>Set the animation for all drilldown animations. Animation of a drilldown occurs when drilling between a column point and a column series, or a pie slice and a full pie series. Drilldown can still be used between series and points of different types, but animation will not occur.</p>.  .  <p>The animation can either be set as a boolean or a configuration object. If <code>true</code>,.  it will use the 'swing' jQuery easing and a duration of 500 ms. If used as a configuration object,.  the following properties are supported: .  </p><dl>.  	<dt>duration</dt>.  	<dd>The duration of the animation in milliseconds.</dd>.  	.  	<dt>easing</dt>.  	<dd>A string reference to an easing function set on the <code>Math</code> object. See <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-animation-easing/">the easing demo</a>.</dd>.  </dl>
    * @param drillUpButton        Options for the drill up button that appears when drilling down on a series. The text for the button is defined in <a href="#lang.drillUpText">lang.drillUpText</a>.
    * @param series               An array of series configurations for the drill down. Each series configuration uses the same syntax as the <a href="#series">series</a> option set. These drilldown series are hidden by default. The drilldown series is linked to the parent series' point by its <code>id</code>.
    */
  def apply(activeAxisLabelStyle: js.UndefOr[String] = js.undefined,
            activeDataLabelStyle: js.UndefOr[String] = js.undefined,
            allowPointDrilldown: js.UndefOr[Boolean] = js.undefined,
            animation: js.UndefOr[Animation] = js.undefined,
            drillUpButton: js.UndefOr[DrilldownDrillUpButton] = js.undefined,
            series: js.UndefOr[Seq[Series]] = js.undefined): Drilldown = {
    val activeAxisLabelStyleOuter = activeAxisLabelStyle.map(stringToStyleObject)
    val activeDataLabelStyleOuter = activeDataLabelStyle.map(stringToStyleObject)
    val allowPointDrilldownOuter = allowPointDrilldown
    val animationOuter = animation.map(_.value)
    val drillUpButtonOuter = drillUpButton
    val seriesOuter = series.map(_.toJSArray)

    new Drilldown {
      override val activeAxisLabelStyle = activeAxisLabelStyleOuter
      override val activeDataLabelStyle = activeDataLabelStyleOuter
      override val allowPointDrilldown = allowPointDrilldownOuter
      override val animation = animationOuter
      override val drillUpButton = drillUpButtonOuter
      override val series = seriesOuter
    }
  }
}
