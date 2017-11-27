/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package global

import scala.scalajs.js


@js.annotation.ScalaJSDefined
trait Lang extends js.Object {

  /**
    * Exporting module menu. The tooltip title for the context menu holding print and export menu items.
    */
  val contextButtonTitle: js.UndefOr[String] = js.undefined

  /**
    * The default decimal point used in the <code>Highcharts.numberFormat</code> method unless otherwise specified in the function arguments.
    */
  val decimalPoint: js.UndefOr[String] = js.undefined

  /**
    * Exporting module only. The text for the JPEG download menu item.
    */
  val downloadJPEG: js.UndefOr[String] = js.undefined

  /**
    * Exporting module only. The text for the PDF download menu item.
    */
  val downloadPDF: js.UndefOr[String] = js.undefined

  /**
    * Exporting module only. The text for the PNG download menu item.
    */
  val downloadPNG: js.UndefOr[String] = js.undefined

  /**
    * Exporting module only. The text for the SVG download menu item.
    */
  val downloadSVG: js.UndefOr[String] = js.undefined

  /**
    * The text for the button that appears when drilling down, linking back to the parent series. The parent series' name is inserted for <code>{series.name}</code>.
    */
  val drillUpText: js.UndefOr[String] = js.undefined

  /**
    * What to show in a date field for invalid dates. Defaults to an empty string.
    */
  val invalidDate: js.UndefOr[String] = js.undefined

  /**
    * The loading text that appears when the chart is set into the loading state following a call to <code>chart.showLoading</code>.
    */
  val loading: js.UndefOr[String] = js.undefined

  /**
    * An array containing the months names. Corresponds to the 
    *  <code>%B</code> format in <code>Highcharts.dateFormat()</code>.
    */
  val months: js.UndefOr[js.Array[String]] = js.undefined

  /**
    * The text to display when the chart contains no data. Requires the no-data module, see <a href="#noData">noData</a>.
    */
  val noData: js.UndefOr[String] = js.undefined

  /**
    * The magnitude of <a href="#lang.numericSymbol">numericSymbols</a> replacements. Use 10000 for Japanese,
    * Korean and various Chinese locales, which use symbols for 10^4, 10^8 and 10^12.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/lang/numericsymbolmagnitude/" target="_blank">10000 magnitude for Japanese</a>
    */
  val numericSymbolMagnitude: js.UndefOr[Double] = js.undefined

  /**
    * <a href="http://en.wikipedia.org/wiki/Metric_prefix">Metric prefixes</a> used to shorten high numbers in axis labels. Replacing any of the positions with <code>null</code> causes the full number to be written. Setting <code>numericSymbols</code> to <code>null</code> disables shortening altogether.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/lang/numericsymbols/" target="_blank">Replacing the symbols with text</a>
    */
  val numericSymbols: js.UndefOr[js.Array[String]] = js.undefined

  /**
    * Exporting module only. The text for the menu item to print the chart.
    */
  val printChart: js.UndefOr[String] = js.undefined

  /**
    * The text for the label appearing when a chart is zoomed.
    */
  val resetZoom: js.UndefOr[String] = js.undefined

  /**
    * The tooltip title for the label appearing when a chart is zoomed.
    */
  val resetZoomTitle: js.UndefOr[String] = js.undefined

  /**
    * An array containing the months names in abbreviated form. Corresponds to the 
    *  <code>%b</code> format in <code>Highcharts.dateFormat()</code>. 
    */
  val shortMonths: js.UndefOr[js.Array[String]] = js.undefined

  /**
    * Short week days, starting Sunday. If not specified, Highcharts uses the first three letters of the <code>lang.weekdays</code> option.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/lang/shortweekdays/" target="_blank">Finnish two-letter abbreviations</a>.
    */
  val shortWeekdays: js.UndefOr[js.Array[String]] = js.undefined

  /**
    * The default thousands separator used in the <code>Highcharts.numberFormat</code> method unless otherwise specified in the function arguments. Since Highcharts 4.1 it defaults to a single space character, which is compatible with ISO and works across Anglo-American and continental European languages.
    */
  val thousandsSep: js.UndefOr[String] = js.undefined

  /**
    * An array containing the weekday names.		 
    */
  val weekdays: js.UndefOr[js.Array[String]] = js.undefined
}

object Lang {
  import scala.scalajs.js.JSConverters._

  /**
    * @param contextButtonTitle Exporting module menu. The tooltip title for the context menu holding print and export menu items.
    * @param decimalPoint The default decimal point used in the <code>Highcharts.numberFormat</code> method unless otherwise specified in the function arguments.
    * @param downloadJPEG Exporting module only. The text for the JPEG download menu item.
    * @param downloadPDF Exporting module only. The text for the PDF download menu item.
    * @param downloadPNG Exporting module only. The text for the PNG download menu item.
    * @param downloadSVG Exporting module only. The text for the SVG download menu item.
    * @param drillUpText The text for the button that appears when drilling down, linking back to the parent series. The parent series' name is inserted for <code>{series.name}</code>.
    * @param invalidDate What to show in a date field for invalid dates. Defaults to an empty string.
    * @param loading The loading text that appears when the chart is set into the loading state following a call to <code>chart.showLoading</code>.
    * @param months An array containing the months names. Corresponds to the .  <code>%B</code> format in <code>Highcharts.dateFormat()</code>.
    * @param noData The text to display when the chart contains no data. Requires the no-data module, see <a href="#noData">noData</a>.
    * @param numericSymbols <a href="http://en.wikipedia.org/wiki/Metric_prefix">Metric prefixes</a> used to shorten high numbers in axis labels. Replacing any of the positions with <code>null</code> causes the full number to be written. Setting <code>numericSymbols</code> to <code>null</code> disables shortening altogether.
    * @param numericSymbolMagnitude The magnitude of <a href="#lang.numericSymbol">numericSymbols</a> replacements.
    * @param printChart Exporting module only. The text for the menu item to print the chart.
    * @param resetZoom The text for the label appearing when a chart is zoomed.
    * @param resetZoomTitle The tooltip title for the label appearing when a chart is zoomed.
    * @param shortMonths An array containing the months names in abbreviated form. Corresponds to the .  <code>%b</code> format in <code>Highcharts.dateFormat()</code>. 
    * @param shortWeekdays Short week days, starting Sunday. If not specified, Highcharts uses the first three letters of the <code>lang.weekdays</code> option.
    * @param thousandsSep The default thousands separator used in the <code>Highcharts.numberFormat</code> method unless otherwise specified in the function arguments. Since Highcharts 4.1 it defaults to a single space character, which is compatible with ISO and works across Anglo-American and continental European languages.
    * @param weekdays An array containing the weekday names.		 
    */
  def apply(contextButtonTitle: js.UndefOr[String] = js.undefined,
            decimalPoint: js.UndefOr[String] = js.undefined,
            downloadJPEG: js.UndefOr[String] = js.undefined,
            downloadPDF: js.UndefOr[String] = js.undefined,
            downloadPNG: js.UndefOr[String] = js.undefined,
            downloadSVG: js.UndefOr[String] = js.undefined,
            drillUpText: js.UndefOr[String] = js.undefined,
            invalidDate: js.UndefOr[String] = js.undefined,
            loading: js.UndefOr[String] = js.undefined,
            months: js.UndefOr[Seq[String]] = js.undefined,
            noData: js.UndefOr[String] = js.undefined,
            numericSymbols: js.UndefOr[Seq[String]] = js.undefined,
            numericSymbolMagnitude: js.UndefOr[Double] = js.undefined,
            printChart: js.UndefOr[String] = js.undefined,
            resetZoom: js.UndefOr[String] = js.undefined,
            resetZoomTitle: js.UndefOr[String] = js.undefined,
            shortMonths: js.UndefOr[Seq[String]] = js.undefined,
            shortWeekdays: js.UndefOr[Seq[String]] = js.undefined,
            thousandsSep: js.UndefOr[String] = js.undefined,
            weekdays: js.UndefOr[Seq[String]] = js.undefined): Lang = {
    val contextButtonTitleOuter = contextButtonTitle
    val decimalPointOuter = decimalPoint
    val downloadJPEGOuter = downloadJPEG
    val downloadPDFOuter = downloadPDF
    val downloadPNGOuter = downloadPNG
    val downloadSVGOuter = downloadSVG
    val drillUpTextOuter = drillUpText
    val invalidDateOuter = invalidDate
    val loadingOuter = loading
    val monthsOuter = months.map(_.toJSArray)
    val noDataOuter = noData
    val numericSymbolsOuter = numericSymbols.map(_.toJSArray)
    val numericSymbolMagnitudeOuter = numericSymbolMagnitude
    val printChartOuter = printChart
    val resetZoomOuter = resetZoom
    val resetZoomTitleOuter = resetZoomTitle
    val shortMonthsOuter = shortMonths.map(_.toJSArray)
    val shortWeekdaysOuter = shortWeekdays.map(_.toJSArray)
    val thousandsSepOuter = thousandsSep
    val weekdaysOuter = weekdays.map(_.toJSArray)
    new Lang {
      override val contextButtonTitle = contextButtonTitleOuter
      override val decimalPoint = decimalPointOuter
      override val downloadJPEG = downloadJPEGOuter
      override val downloadPDF = downloadPDFOuter
      override val downloadPNG = downloadPNGOuter
      override val downloadSVG = downloadSVGOuter
      override val drillUpText = drillUpTextOuter
      override val invalidDate = invalidDateOuter
      override val loading = loadingOuter
      override val months = monthsOuter
      override val noData = noDataOuter
      override val numericSymbols = numericSymbolsOuter
      override val numericSymbolMagnitude = numericSymbolMagnitudeOuter
      override val printChart = printChartOuter
      override val resetZoom = resetZoomOuter
      override val resetZoomTitle = resetZoomTitleOuter
      override val shortMonths = shortMonthsOuter
      override val shortWeekdays = shortWeekdaysOuter
      override val thousandsSep = thousandsSepOuter
      override val weekdays = weekdaysOuter
    }
  }
}
