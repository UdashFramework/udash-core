/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package exporting

import scala.scalajs.js


trait Exporting extends js.Object {

  /**
    * <p>Experimental setting to allow HTML inside the chart (added through the <code>useHTML</code> options),
    * directly in the exported image. This allows you to preserve complicated HTML structures like tables
    * or bi-directional text in exported charts.</p>
    *
    * <p>Disclaimer: The HTML is rendered in a <code>foreignObject</code> tag in the generated SVG.
    * The official export server is based on PhantomJS, which supports this, but other SVG clients, like Batik, does not support it.
    * This also applies to downloaded SVG that you want to open in a desktop client.</p>
    */
  val allowHTML: js.UndefOr[Boolean] = js.undefined

  /**
    * Options for the export related buttons, print and export. In addition to the default buttons listed here,
    * custom buttons can be added. See <a href="#navigation.buttonOptions">navigation.buttonOptions</a> for general options.
    */
  val buttons: js.UndefOr[ExportingButtons] = js.undefined

  /**
    * Additional chart options to be merged into an exported chart. For example, a common use case is to add data labels
    * to improve readaility of the exported chart, or to add a printer-friendly color scheme.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/chartoptions-data-labels/" target="_blank">Added data labels</a>.
    */
  val chartOptions: js.UndefOr[js.Object] = js.undefined

  /**
    * Whether to enable the exporting module. Disabling the module will hide the context button,
    * but API methods will still be available.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/enabled-false/" target="_blank">Exporting module is loaded but disabled</a>
    */
  val enabled: js.UndefOr[Boolean] = js.undefined

  /**
    * Function to call if the offline-exporting module fails to export a chart on the client side,
    * and fallbackToExportServer is disabled. If left undefined, an exception is thrown instead. Defaults to undefined.
    */
  val error: js.UndefOr[js.Function0[Any]] = js.undefined

  /**
    * Whether or not to fall back to the export server
    * if the offline-exporting module is unable to export the chart on the client side.
    */
  val fallbackToExportServer: js.UndefOr[Boolean] = js.undefined

  /**
    * The filename, without extension, to use for the exported chart.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/filename/" target="_blank">Custom file name</a>
    */
  val filename: js.UndefOr[String] = js.undefined

  /**
    * An object containing additional attributes for the POST form that sends the SVG to the export server. For example,
    * a <code>target</code> can be set to make sure the generated image is received in another frame, or a custom <code>enctype</code>
    * or <code>encoding</code> can be set.
    */
  val formAttributes: js.UndefOr[js.Object] = js.undefined

  /**
    * Path where Highcharts will look for export module dependencies to load on demand if they don't already exist on <code>window</code>.
    * Should currently point to location of <a href="https://github.com/canvg/canvg">CanVG</a> library,
    * <a href="https://github.com/canvg/canvg">RGBColor.js</a>, <a href="https://github.com/yWorks/jsPDF">jsPDF</a>
    * and <a href="https://github.com/yWorks/svg2pdf.js">svg2pdf.js</a>, required for client side export in certain browsers.
    */
  val libURL: js.UndefOr[String] = js.undefined

  /**
    * When printing the chart from the menu item in the burger menu, if the on-screen chart exceeds this width, it is resized.
    * After printing or cancelled, it is restored. The default width makes the chart fit into typical paper format.
    * Note that this does not affect the chart when printing the web page as a whole.
    */
  val printMaxWidth: js.UndefOr[Double] = js.undefined

  /**
    * Defines the scale or zoom factor for the exported image compared to the on-screen display.
    * While for instance a 600px wide chart may look good on a website, it will look bad in print.
    * The default scale of 2 makes this chart export to a 1200px PNG or JPG.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/scale/" target="_blank">Scale demonstrated</a>
    */
  val scale: js.UndefOr[Double] = js.undefined

  /**
    * Analogous to <a href="#exporting.sourceWidth">sourceWidth</a>
    */
  val sourceHeight: js.UndefOr[Double] = js.undefined

  /**
    * The width of the original chart when exported, unless an explicit <a href="#chart.width">chart.width</a> is set.
    * The width exported raster image is then multiplied by <a href="#exporting.scale">scale</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/sourcewidth/" target="_blank">Source size demo</a>
    */
  val sourceWidth: js.UndefOr[Double] = js.undefined

  /**
    * Default MIME type for exporting if <code>chart.exportChart()</code> is called without specifying a <code>type</code> option.
    * Possible values are <code>image/png</code>, <code>image/jpeg</code>, <code>application/pdf</code> and <code>image/svg+xml</code>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/type/" target="_blank">Default type set to <code>image/jpeg</code></a>
    */
  val `type`: js.UndefOr[String] = js.undefined

  /**
    * The URL for the server module converting the SVG string to an image format.
    * By default this points to Highchart's free web service.
    */
  val url: js.UndefOr[String] = js.undefined

  /**
    * The pixel width of charts exported to PNG or JPG. As of Highcharts 3.0,
    * the default pixel width is a function of the <a href="#chart.width">chart.width</a>
    * or <a href="#exporting.sourceWidth">exporting.sourceWidth</a> and the <a href="#exporting.scale">exporting.scale</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/exporting/width/" target="_blank">Export to 200px wide images</a>
    */
  val width: js.UndefOr[Double] = js.undefined
}

object Exporting {

  final class ExportType(val name: String) extends AnyVal
  object ExportType {
    val PNG = new ExportType("image/png")
    val JPEG = new ExportType("image/jpeg")
    val PDF = new ExportType("application/pdf")
    val SVG = new ExportType("image/svg+xml")
  }

  /**
    * @param allowHTML              <p>Experimental setting to allow HTML inside the chart (added through the <code>useHTML</code> options), directly in the exported image. This allows you to preserve complicated HTML structures like tables or bi-directional text in exported charts.</p>. . <p>Disclaimer: The HTML is rendered in a <code>foreignObject</code> tag in the generated SVG. The official export server is based on PhantomJS, which supports this, but other SVG clients, like Batik, does not support it. This also applies to downloaded SVG that you want to open in a desktop client.</p>
    * @param buttons                Options for the export related buttons, print and export. In addition to the default buttons listed here, custom buttons can be added. See <a href="#navigation.buttonOptions">navigation.buttonOptions</a> for general options.
    * @param chartOptions           Additional chart options to be merged into an exported chart. For example, a common use case is to add data labels to improve readaility of the exported chart, or to add a printer-friendly color scheme.
    * @param enabled                Whether to enable the exporting module. Disabling the module will hide the context button, but API methods will still be available.
    * @param error                  Whether to enable the exporting module. Disabling the module will hide the context button, but API methods will still be available.
    * @param fallbackToExportServer Whether or not to fall back to the export server if the offline-exporting module is unable to export the chart on the client side.
    * @param filename               The filename, without extension, to use for the exported chart.
    * @param formAttributes         An object containing additional attributes for the POST form that sends the SVG to the export server. For example, a <code>target</code> can be set to make sure the generated image is received in another frame, or a custom <code>enctype</code> or <code>encoding</code> can be set.
    * @param libURL                 Path where Highcharts will look for export module dependencies to load on demand if they don't already exist on window.
    * @param printMaxWidth          When printing the chart from the menu item in the burger menu, if the on-screen chart exceeds this width, it is resized. After printing or cancelled, it is restored. The default width makes the chart fit into typical paper format. Note that this does not affect the chart when printing the web page as a whole.
    * @param scale                  Defines the scale or zoom factor for the exported image compared to the on-screen display. While for instance a 600px wide chart may look good on a website, it will look bad in print. The default scale of 2 makes this chart export to a 1200px PNG or JPG.
    * @param sourceHeight           Analogous to <a href="#exporting.sourceWidth">sourceWidth</a>
    * @param sourceWidth            The width of the original chart when exported, unless an explicit <a href="#chart.width">chart.width</a> is set. The width exported raster image is then multiplied by <a href="#exporting.scale">scale</a>.
    * @param `type`                 Default MIME type for exporting if <code>chart.exportChart()</code> is called without specifying a <code>type</code> option. Possible values are <code>image/png</code>, <code>image/jpeg</code>, <code>application/pdf</code> and <code>image/svg+xml</code>.
    * @param url                    The URL for the server module converting the SVG string to an image format. By default this points to Highchart's free web service.
    * @param width                  The pixel width of charts exported to PNG or JPG. As of Highcharts 3.0, the default pixel width is a function of the <a href="#chart.width">chart.width</a> or <a href="#exporting.sourceWidth">exporting.sourceWidth</a> and the <a href="#exporting.scale">exporting.scale</a>.
    */
  def apply(allowHTML: js.UndefOr[Boolean] = js.undefined,
            buttons: js.UndefOr[ExportingButtons] = js.undefined,
            chartOptions: js.UndefOr[HighchartsConfig] = js.undefined,
            enabled: js.UndefOr[Boolean] = js.undefined,
            error: js.UndefOr[() => Any] = js.undefined,
            fallbackToExportServer: js.UndefOr[Boolean] = js.undefined,
            filename: js.UndefOr[String] = js.undefined,
            formAttributes: js.UndefOr[js.Object] = js.undefined,
            libURL: js.UndefOr[String] = js.undefined,
            printMaxWidth: js.UndefOr[Double] = js.undefined,
            scale: js.UndefOr[Double] = js.undefined,
            sourceHeight: js.UndefOr[Double] = js.undefined,
            sourceWidth: js.UndefOr[Double] = js.undefined,
            `type`: js.UndefOr[ExportType] = js.undefined,
            url: js.UndefOr[String] = js.undefined,
            width: js.UndefOr[Double] = js.undefined): Exporting = {
    val allowHTMLOuter = allowHTML
    val buttonsOuter = buttons
    val chartOptionsOuter = chartOptions
    val enabledOuter = enabled
    val errorOuter = error.map(js.Any.fromFunction0)
    val fallbackToExportServerOuter = fallbackToExportServer
    val filenameOuter = filename
    val formAttributesOuter = formAttributes
    val libURLOuter = libURL
    val printMaxWidthOuter = printMaxWidth
    val scaleOuter = scale
    val sourceHeightOuter = sourceHeight
    val sourceWidthOuter = sourceWidth
    val typeOuter = `type`.map(_.name)
    val urlOuter = url
    val widthOuter = width

    new Exporting {
      override val allowHTML = allowHTMLOuter
      override val buttons = buttonsOuter
      override val chartOptions = chartOptionsOuter
      override val enabled = enabledOuter
      override val error = errorOuter
      override val fallbackToExportServer = fallbackToExportServerOuter
      override val filename = filenameOuter
      override val formAttributes = formAttributesOuter
      override val libURL = libURLOuter
      override val printMaxWidth = printMaxWidthOuter
      override val scale = scaleOuter
      override val sourceHeight = sourceHeightOuter
      override val sourceWidth = sourceWidthOuter
      override val `type` = typeOuter
      override val url = urlOuter
      override val width = widthOuter
    }
  }
}
