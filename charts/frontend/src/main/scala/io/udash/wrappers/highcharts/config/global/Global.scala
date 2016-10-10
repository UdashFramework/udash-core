/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package global

import scala.scalajs.js

@js.annotation.ScalaJSDefined
class Global extends js.Object {

  /**
    * A custom <code>Date</code> class for advanced date handling. For example, <a href="https://github.com/tahajahangir/jdate">JDate</a> can be hooked in to handle Jalali dates.
    */
  val Date: js.UndefOr[js.Object] = js.undefined

  /**
    * Path to the pattern image required by VML browsers in order to draw radial gradients.
    */
  val VMLRadialGradientURL: js.UndefOr[String] = js.undefined

  /**
    * The URL to the additional file to lazy load for Android 2.x devices. These devices don't 
    * support SVG, so we download a helper file that contains <a href="http://code.google.com/p/canvg/">canvg</a>,
    * its dependency rbcolor, and our own CanVG Renderer class. To avoid hotlinking to our site,
    * you can install canvas-tools.js on your own server and change this option accordingly.
    */
  val canvasToolsURL: js.UndefOr[String] = js.undefined

  /**
    * A callback to return the time zone offset for a given datetime. It takes the timestamp in terms of milliseconds
    * since January 1 1970, and returns the timezone offset in minutes. This provides a hook for drawing time based
    * charts in specific time zones using their local DST crossover dates, with the help of external libraries.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/global/gettimezoneoffset/" target="_blank">Use moment.js to draw Oslo time regardless of browser locale</a>
    */
  val getTimezoneOffset: js.UndefOr[js.Function1[Any, Int]] = js.undefined

  /**
    * Requires moment.js. If the timezone option is specified, it creates a default getTimezoneOffset
    * function that looks up the specified timezone in moment.js. If moment.js is not included,
    * this throws a Highcharts error in the console, but does not crash the chart. Defaults to undefined.
    */
  val timezone: js.UndefOr[String] = js.undefined

  /**
    * The timezone offset in minutes. Positive values are west, negative values are east of UTC, as in the
    * ECMAScript <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/getTimezoneOffset">getTimezoneOffset</a> method.
    * Use this to display UTC based data in a predefined time zone.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/global/timezoneoffset/" target="_blank">Timezone offset</a>
    */
  val timezoneOffset: js.UndefOr[Double] = js.undefined

  /**
    * Whether to use UTC time for axis scaling, tickmark placement and time display in  <code>Highcharts.dateFormat</code>.
    * Advantages of using UTC is that the time displays equally regardless of the user agent's time zone settings.
    * Local time can be used when the data is loaded in real time or when correct Daylight Saving Time transitions are required.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/global/useutc-true/" target="_blank">True by default</a> - the starting point which is
    *          at 00:00 UTC, is displayed as 00:00 in the axis labels and in the tooltip.
    *          <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/global/useutc-false/" target="_blank">False</a> - the starting point which is
    *          at 00:00 UTC, is displayed as your local browser time in the axis labels and in
    *          the tooltip.
    */
  val useUTC: js.UndefOr[Boolean] = js.undefined
}

object Global {
  import scala.scalajs.js.JSConverters._

  /**
    * @param date                 A custom <code>Date</code> class for advanced date handling. For example, <a href="https://github.com/tahajahangir/jdate">JDate</a> can be hooked in to handle Jalali dates.
    * @param VMLRadialGradientURL Path to the pattern image required by VML browsers in order to draw radial gradients.
    * @param canvasToolsURL       The URL to the additional file to lazy load for Android 2.x devices. These devices don't .  support SVG, so we download a helper file that contains <a href="http://code.google.com/p/canvg/">canvg</a>,.  its dependency rbcolor, and our own CanVG Renderer class. To avoid hotlinking to our site,.  you can install canvas-tools.js on your own server and change this option accordingly.
    * @param getTimezoneOffset    A callback to return the time zone offset for a given datetime. It takes the timestamp in terms of milliseconds since January 1 1970, and returns the timezone offset in minutes. This provides a hook for drawing time based charts in specific time zones using their local DST crossover dates, with the help of external libraries.
    * @param timezone             Requires moment.js. If the timezone option is specified, it creates a default getTimezoneOffset function that looks up the specified timezone in moment.js.
    * @param timezoneOffset       The timezone offset in minutes. Positive values are west, negative values are east of UTC, as in the ECMAScript <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/getTimezoneOffset">getTimezoneOffset</a> method. Use this to display UTC based data in a predefined time zone.
    * @param useUTC               Whether to use UTC time for axis scaling, tickmark placement and time display in  <code>Highcharts.dateFormat</code>. Advantages of using UTC is that the time displays equally regardless of the user agent's time zone settings. Local time can be used when the data is loaded in real time or when correct Daylight Saving Time transitions are required.
    */
  def apply(date: js.UndefOr[js.Object] = js.undefined,
            VMLRadialGradientURL: js.UndefOr[String] = js.undefined,
            canvasToolsURL: js.UndefOr[String] = js.undefined,
            getTimezoneOffset: js.UndefOr[(Any) => Int] = js.undefined,
            timezone: js.UndefOr[String] = js.undefined,
            timezoneOffset: js.UndefOr[Double] = js.undefined,
            useUTC: js.UndefOr[Boolean] = js.undefined): Global = {
    val dateOuter = date
    val VMLRadialGradientURLOuter = VMLRadialGradientURL
    val canvasToolsURLOuter = canvasToolsURL
    val getTimezoneOffsetOuter = getTimezoneOffset.map(js.Any.fromFunction1[Any, Int])
    val timezoneOuter = timezone
    val timezoneOffsetOuter = timezoneOffset
    val useUTCOuter = useUTC
    new Global {
      override val Date = dateOuter
      override val VMLRadialGradientURL = VMLRadialGradientURLOuter
      override val canvasToolsURL = canvasToolsURLOuter
      override val getTimezoneOffset = getTimezoneOffsetOuter
      override val timezone = timezoneOuter
      override val timezoneOffset = timezoneOffsetOuter
      override val useUTC = useUTCOuter
    }
  }
}
