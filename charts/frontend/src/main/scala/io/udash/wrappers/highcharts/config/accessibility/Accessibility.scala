/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts.config.accessibility

import io.udash.wrappers.highcharts.api

import scala.scalajs.js
import scala.scalajs.js.`|`

@js.annotation.ScalaJSDefined
trait Accessibility extends js.Object {

  /**
    * Whether or not to add series descriptions to charts with a single series.
    */
  val describeSingleSeries: js.UndefOr[Boolean] = js.undefined

  /**
    * Enable accessibility features for the chart.
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * Options for keyboard navigation.
    */
  val keyboardNavigation: js.UndefOr[AccessibilityKeyboardNavigation] = js.undefined

  /**
    * <p>Function to run upon clicking the "View as Data Table" link in the screen reader region.</p>
    *
    * <p>By default Highcharts will insert and set focus to a data table representation of the chart.</p>
    */
  val onTableAnchorClick: js.UndefOr[js.Function0[Unit]] = js.undefined

  /**
    * <p>Date format to use for points on datetime axes when describing them to screen reader users.</p>
    * <p>Defaults to the same format as in tooltip.</p>
    * <p>For an overview of the replacement codes, see <a href="#Highcharts.dateFormat">dateFormat</a>.</p>
    */
  val pointDateFormat: js.UndefOr[String] = js.undefined

  /**
    * <p>Formatter function to determine the date/time format used with points on datetime axes when describing
    * them to screen reader users. Receives one argument, <code>point</code>, referring to the point to describe.
    * Should return a date format string compatible with <a href="#Highcharts.dateFormat">dateFormat</a>.</p>
    */
  val pointDateFormatter: js.UndefOr[js.Function1[api.Point, String]] = js.undefined

  /**
    * <p>Formatter function to use instead of the default for point descriptions. Receives one argument, <code>point</code>,
    * referring to the point to describe. Should return a String with the description of the point for a screen reader user.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/accessibility/advanced-accessible/">Complex accessible chart</a>
    */
  val pointDescriptionFormatter: js.UndefOr[js.Function1[api.Point, String]] = js.undefined

  /**
    * <p>When a series contains more points than this, we no longer expose information about individual points to screen readers.</p>
    * <p>Set to <code>false</code> to disable.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/stock/accessibility/accessible-stock/">Accessible stock chart</a>
    */
  val pointDescriptionThreshold: js.UndefOr[Double | Boolean] = js.undefined

  /**
    * <p>A formatter function to create the HTML contents of the hidden screen reader information region.
    * Receives one argument, <code>chart</code>, referring to the chart object. Should return a String with the HTML content of the region.</p>
    * <p>The link to view the chart as a data table will be added automatically after the custom HTML content.</p>
    */
  val screenReaderSectionFormatter: js.UndefOr[js.Function1[api.Chart, String]] = js.undefined

  /**
    * <p>Formatter function to use instead of the default for series descriptions. Receives one argument, <code>series</code>,
    * referring to the series to describe. Should return a String with the description of the series for a screen reader user.</p>
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/accessibility/advanced-accessible/">Accessible complex chart</a>
    */
  val seriesDescriptionFormatter: js.UndefOr[js.Function1[api.Series, String]] = js.undefined
}

object Accessibility {
  import scala.scalajs.js.JSConverters._

  /**
    * @param describeSingleSeries Whether or not to add series descriptions to charts with a single series.
    * @param enabled Enable accessibility features for the chart.
    * @param keyboardNavigation Options for keyboard navigation.
    * @param onTableAnchorClick <p>Function to run upon clicking the "View as Data Table" link in the screen reader region.</p>. . <p>By default Highcharts will insert and set focus to a data table representation of the chart.</p>
    * @param pointDateFormat <p>Date format to use for points on datetime axes when describing them to screen reader users.</p>. <p>Defaults to the same format as in tooltip.</p>. <p>For an overview of the replacement codes, see <a href="#Highcharts.dateFormat">dateFormat</a>.</p>
    * @param pointDateFormatter <p>Formatter function to determine the date/time format used with points on datetime axes when describing them to screen reader users. Receives one argument, <code>point</code>, referring to the point to describe. Should return a date format string compatible with <a href="#Highcharts.dateFormat">dateFormat</a>.</p>
    * @param pointDescriptionFormatter <p>Formatter function to use instead of the default for point descriptions. Receives one argument, <code>point</code>, referring to the point to describe. Should return a String with the description of the point for a screen reader user.</p>
    * @param pointDescriptionThreshold <p>When a series contains more points than this, we no longer expose information about individual points to screen readers.</p>. <p>Set to <code>false</code> to disable.</p>
    * @param screenReaderSectionFormatter <p>A formatter function to create the HTML contents of the hidden screen reader information region. Receives one argument, <code>chart</code>, referring to the chart object. Should return a String with the HTML content of the region.</p>. <p>The link to view the chart as a data table will be added automatically after the custom HTML content.</p>
    * @param seriesDescriptionFormatter <p>Formatter function to use instead of the default for series descriptions. Receives one argument, <code>series</code>, referring to the series to describe. Should return a String with the description of the series for a screen reader user.</p>
    */
  def apply(describeSingleSeries: js.UndefOr[Boolean] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            keyboardNavigation: js.UndefOr[AccessibilityKeyboardNavigation] = js.undefined,
            onTableAnchorClick: js.UndefOr[() => Unit] = js.undefined,
            pointDateFormat: js.UndefOr[String] = js.undefined,
            pointDateFormatter: js.UndefOr[(api.Point) => String] = js.undefined,
            pointDescriptionFormatter: js.UndefOr[(api.Point) => String] = js.undefined,
            pointDescriptionThreshold: js.UndefOr[Double | Boolean] = js.undefined,
            screenReaderSectionFormatter: js.UndefOr[(api.Chart) => String] = js.undefined,
            seriesDescriptionFormatter: js.UndefOr[(api.Series) => String] = js.undefined): Accessibility = {
    val describeSingleSeriesOuter = describeSingleSeries
    val enabledOuter = enabled
    val keyboardNavigationOuter = keyboardNavigation
    val onTableAnchorClickOuter = onTableAnchorClick.map(js.Any.fromFunction0)
    val pointDateFormatOuter = pointDateFormat
    val pointDateFormatterOuter = pointDateFormatter.map(js.Any.fromFunction1[api.Point, String])
    val pointDescriptionFormatterOuter = pointDescriptionFormatter.map(js.Any.fromFunction1[api.Point, String])
    val pointDescriptionThresholdOuter = pointDescriptionThreshold
    val screenReaderSectionFormatterOuter = screenReaderSectionFormatter.map(js.Any.fromFunction1[api.Chart, String])
    val seriesDescriptionFormatterOuter = seriesDescriptionFormatter.map(js.Any.fromFunction1[api.Series, String])

    new Accessibility {
      override val describeSingleSeries = describeSingleSeriesOuter
      override val enabled = enabledOuter
      override val keyboardNavigation = keyboardNavigationOuter
      override val onTableAnchorClick = onTableAnchorClickOuter
      override val pointDateFormat = pointDateFormatOuter
      override val pointDateFormatter = pointDateFormatterOuter
      override val pointDescriptionFormatter = pointDescriptionFormatterOuter
      override val pointDescriptionThreshold = pointDescriptionThresholdOuter
      override val screenReaderSectionFormatter = screenReaderSectionFormatterOuter
      override val seriesDescriptionFormatter = seriesDescriptionFormatterOuter
    }
  }
}
