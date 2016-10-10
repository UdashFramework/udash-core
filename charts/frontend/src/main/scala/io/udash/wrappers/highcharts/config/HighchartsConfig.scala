/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config

import io.udash.wrappers.highcharts.config.accessibility.Accessibility
import io.udash.wrappers.highcharts.config.axis.{ColorAxis, XAxis, YAxis, ZAxis}
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.drilldown.Drilldown
import io.udash.wrappers.highcharts.config.exporting.Exporting
import io.udash.wrappers.highcharts.config.labels.Labels
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.navigation.Navigation
import io.udash.wrappers.highcharts.config.pane.Pane
import io.udash.wrappers.highcharts.config.placeholders.{Loading, NoData}
import io.udash.wrappers.highcharts.config.plot._
import io.udash.wrappers.highcharts.config.responsive.Responsive
import io.udash.wrappers.highcharts.config.series._
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js
import scala.scalajs.js.|

@js.annotation.ScalaJSDefined
class HighchartsConfig extends js.Object {
  /**
    * <p>Options for configuring accessibility for the chart.
    * Requires the <a href="//code.highcharts.com/modules/accessibility.js">accessibility module</a> to be loaded.
    * For a description of the module and information on its features,
    * see <a href="http://www.highcharts.com/docs/chart-concepts/accessibility">Highcharts Accessibility</a>.</p>
    */
  val accessibility: js.UndefOr[Accessibility] = js.undefined

  /**
    * Options regarding the chart area and plot area as well as general chart options.
    */
  val chart: js.UndefOr[Chart] = js.undefined

  /**
    * <p>An array containing the default colors for the chart's series. When all colors are used, new colors are pulled from the start again. Defaults to:
    * <pre>colors: ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9',
    * '#f15c80', '#e4d354', '#2b908f', '#f45b5b', '#91e8e1']</pre>
    *
    * Default colors can also be set on a series or series.type basis, see <a href="#plotOptions.column.colors">column.colors</a>, <a href="#plotOptions.pie.colors">pie.colors</a>.
    * </p>
    *
    * <h3>Legacy</h3>
    * <p>In Highcharts 3.x, the default colors were:
    * <pre>colors: ['#2f7ed8', '#0d233a', '#8bbc21', '#910000', '#1aadce',
    * '#492970', '#f28f43', '#77a1e5', '#c42525', '#a6c96a']</pre>
    * </p>
    *
    * <p>In Highcharts 2.x, the default colors were:
    * <pre>colors: ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE',
    * '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92']</pre></p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/colors/" target="_blank">Assign a global color theme</a>
    */
  val colors: js.UndefOr[js.Array[String | js.Object]] = js.undefined

  /**
    * Highchart by default puts a credits label in the lower right corner of the chart.
    * This can be changed using these options.
    */
  val credits: js.UndefOr[Credits] = js.undefined

  /**
    * <p>The Data module provides a simplified interface for adding data to a chart from sources like CVS, HTML tables or grid views. See also the <a href="http://www.highcharts.com/docs/working-with-data/data-module">tutorial article on the Data module</a>.</p>
    *
    * <p>It requires the <code>modules/data.js</code> file to be loaded.</p>
    *
    * <p>Please note that the default way of adding data in Highcharts, without the need of a module, is through the <a href="#series.data">series.data</a> option.</p>
    *
    * @example Data from a <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/column-parsed/" target="_blank">HTML table</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/data/csv/" target="_blank">CSV</a>.
    */
  val data: js.UndefOr[Data] = js.undefined

  /**
    * <p>Options for drill down, the concept of inspecting increasingly high resolution data through clicking on chart items like columns or pie slices.</p>
    *
    * <p>The drilldown feature requires the <code>drilldown.js</code> file to be loaded, found in the <code>modules</code> directory of the download package, or online at <a href="http://code.highcharts.com/modules/drilldown.js">code.highcharts.com/modules/drilldown.js</a>.</p>
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/basic/" target="_blank">Basic drilldown</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/multi-series/" target="_blank">multi series drilldown</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/column-drilldown/" target="_blank">generated column drilldown</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/pie-drilldown/" target="_blank">pie drilldown</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/across-types/" target="_blank">drilldown across series types</a>.
    */
  val drilldown: js.UndefOr[Drilldown] = js.undefined

  /**
    * Options for the exporting module. For an overview on the matter, see <a href="http://www.highcharts.com/docs/export-module/export-module-overview">the docs</a>.
    */
  val exporting: js.UndefOr[Exporting] = js.undefined

  /**
    * HTML labels that can be positioned anywhere in the chart area.
    */
  val labels: js.UndefOr[Labels] = js.undefined

  /**
    * <p>The legend is a box containing a symbol and name for each series item or point item in the chart. Each series (or points in case of pie charts) is represented by a symbol and its name in the legend.</p>
    *
    * <p>It is also possible to override the symbol creator function and create <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/studies/legend-custom-symbol/">custom legend symbols</a>.</p>
    */
  val legend: js.UndefOr[Legend] = js.undefined

  /**
    * The loading options control the appearance of the loading screen that covers the 
    * plot area on chart operations. This screen only appears after an explicit call
    * to <code>chart.showLoading()</code>. It is a utility for developers to communicate
    * to the end user that something is going on, for example while retrieving new data
    * via an XHR connection. The "Loading..." text itself is not part of this configuration
    * object, but part of the <code>lang</code> object.
    */
  val loading: js.UndefOr[Loading] = js.undefined

  /**
    * A collection of options for buttons and menus appearing in the exporting module.
    */
  val navigation: js.UndefOr[Navigation] = js.undefined

  /**
    * Options for displaying a message like "No data to display". This feature requires the file <code>no-data-to-display.js</code> to be loaded in the page. The actual text to display is set in the <a href="#lang.noData">lang.noData</a> option.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/no-data-to-display/no-data-line/" target="_blank">Line series</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/no-data-to-display/no-data-pie/" target="_blank">pie series</a>.
    */
  val noData: js.UndefOr[NoData] = js.undefined

  /**
    * Applies only to polar charts and angular gauges. This configuration object holds general options for the combined X and Y axes set. Each xAxis or yAxis can reference the pane by index.
    */
  val pane: js.UndefOr[Pane] = js.undefined

  /**
    * <p>The plotOptions is a wrapper object for config objects for each series type.
    * The config objects for each series can also be overridden for each series
    * item as given in the series array.</p>
    * <p>Configuration options for the series are given in three levels. Options
    * for all series in a chart are given in the <a class="internal" href="#plotOptions.series">plotOptions.series</a> object. Then options for all series
    * of a specific type are given in the plotOptions of that type, for example plotOptions.line.
    * Next, options for one single series are given in <a class="internal" href="#series">the
    * series array</a>.</p>
    */
  val plotOptions: js.UndefOr[PlotOptions] = js.undefined

  /**
    * Allows setting a set of rules to apply for different screen or chart sizes. Each rule specifies additional chart options.
    * @example Responsive <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/axis/" target="_blank">axis</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/legend/" target="_blank">legend</a> and <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/responsive/classname/" target="_blank">class name</a>.
    */
  val responsive: js.UndefOr[Responsive] = js.undefined

  /**
    * The actual series to append to the chart. In addition to
    * the members listed below, any member of the <code>plotOptions</code> for that specific
    * type of plot can be added to a series individually. For example, even though a general
    * <code>lineWidth</code> is specified in <code>plotOptions.series</code>, an individual
    * <code>lineWidth</code> can be specified for each series.
    */
  val series: js.UndefOr[js.Array[Series]] = js.undefined

  /**
    * The chart's subtitle
    */
  val subtitle: js.UndefOr[Subtitle] = js.undefined

  /**
    * The chart's main title.
    */
  val title: js.UndefOr[Title] = js.undefined

  /**
    * Options for the tooltip that appears when the user hovers over a series or point.
    */
  val tooltip: js.UndefOr[Tooltip] = js.undefined

  /**
    * <p>The X axis or category axis. Normally this is the horizontal axis, though if the 
    * chart is inverted this is the vertical axis. In case of multiple axes, the xAxis
    * node is an array of configuration objects.</p>
    * <p>See <a class="internal" href="#axis.object">the Axis object</a> for programmatic
    * access to the axis.</p>
    */
  val xAxis: js.UndefOr[js.Array[XAxis]] = js.undefined

  /**
    * <p>The Y axis or value axis. Normally this is the vertical axis, though if the 
    * chart is inverted this is the horizontal axis. In case of multiple axes, the yAxis
    * node is an array of configuration objects.</p>
    * <p>See <a class="internal" href="#axis.object">the Axis object</a> for programmatic
    * access to the axis.</p>
    */
  val yAxis: js.UndefOr[js.Array[YAxis]] = js.undefined

  /**
    * The Z axis or depth axis for 3D plots.
    */
  val zAxis: js.UndefOr[ZAxis] = js.undefined

  /**
    * The color axis for heatmaps.
    */
  val colorAxis: js.UndefOr[ColorAxis] = js.undefined
}

object HighchartsConfig {
  import scala.scalajs.js.JSConverters._

  /**
    * @param series      The actual series to append to the chart. In addition to . 	the members listed below, any member of the <code>plotOptions</code> for that specific. 	type of plot can be added to a series individually. For example, even though a general. 	<code>lineWidth</code> is specified in <code>plotOptions.series</code>, an individual. 	<code>lineWidth</code> can be specified for each series.
    * @param accessibility <p>Options for configuring accessibility for the chart. Requires the <a href="//code.highcharts.com/modules/accessibility.js">accessibility module</a> to be loaded. For a description of the module and information on its features, see <a href="http://www.highcharts.com/docs/chart-concepts/accessibility">Highcharts Accessibility</a>.</p>
    * @param chart       Options regarding the chart area and plot area as well as general chart options.
    * @param colors      <p>An array containing the default colors for the chart's series. When all colors are used, new colors are pulled from the start again. Defaults to:. <pre>colors: ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9', .    '#f15c80', '#e4d354', '#2b908f', '#f45b5b', '#91e8e1']</pre>. . Default colors can also be set on a series or series.type basis, see <a href="#plotOptions.column.colors">column.colors</a>, <a href="#plotOptions.pie.colors">pie.colors</a>.. </p>. . <h3>Legacy</h3>. <p>In Highcharts 3.x, the default colors were:. <pre>colors: ['#2f7ed8', '#0d233a', '#8bbc21', '#910000', '#1aadce', .    '#492970', '#f28f43', '#77a1e5', '#c42525', '#a6c96a']</pre>. </p>. . <p>In Highcharts 2.x, the default colors were:. <pre>colors: ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE', .    '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92']</pre></p>
    * @param credits     Highchart by default puts a credits label in the lower right corner of the chart.. 		This can be changed using these options.
    * @param data        <p>The Data module provides a simplified interface for adding data to a chart from sources like CVS, HTML tables or grid views. See also the <a href="http://www.highcharts.com/docs/working-with-data/data-module">tutorial article on the Data module</a>.</p>. . <p>It requires the <code>modules/data.js</code> file to be loaded.</p>. . <p>Please note that the default way of adding data in Highcharts, without the need of a module, is through the <a href="#series.data">series.data</a> option.</p>
    * @param drilldown   <p>Options for drill down, the concept of inspecting increasingly high resolution data through clicking on chart items like columns or pie slices.</p>. . <p>The drilldown feature requires the <code>drilldown.js</code> file to be loaded, found in the <code>modules</code> directory of the download package, or online at <a href="http://code.highcharts.com/modules/drilldown.js">code.highcharts.com/modules/drilldown.js</a>.</p>
    * @param exporting   Options for the exporting module. For an overview on the matter, see <a href="http://www.highcharts.com/docs/export-module/export-module-overview">the docs</a>.
    * @param labels      HTML labels that can be positioned anywhere in the chart area.
    * @param legend      <p>The legend is a box containing a symbol and name for each series item or point item in the chart. Each series (or points in case of pie charts) is represented by a symbol and its name in the legend.</p>. . <p>It is also possible to override the symbol creator function and create <a href="http://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/studies/legend-custom-symbol/">custom legend symbols</a>.</p>
    * @param loading     The loading options control the appearance of the loading screen that covers the . 	plot area on chart operations. This screen only appears after an explicit call. 	to <code>chart.showLoading()</code>. It is a utility for developers to communicate. 	to the end user that something is going on, for example while retrieving new data. 	via an XHR connection. The "Loading..." text itself is not part of this configuration. 	object, but part of the <code>lang</code> object.
    * @param navigation  A collection of options for buttons and menus appearing in the exporting module.
    * @param noData      Options for displaying a message like "No data to display". This feature requires the file <code>no-data-to-display.js</code> to be loaded in the page. The actual text to display is set in the <a href="#lang.noData">lang.noData</a> option.
    * @param pane        Applies only to polar charts and angular gauges. This configuration object holds general options for the combined X and Y axes set. Each xAxis or yAxis can reference the pane by index.
    * @param plotOptions <p>The plotOptions is a wrapper object for config objects for each series type.. 		The config objects for each series can also be overridden for each series . 		item as given in the series array.</p>. 		<p>Configuration options for the series are given in three levels. Options. 		for all series in a chart are given in the <a class="internal" href="#plotOptions.series">plotOptions.series</a> object. Then options for all series. 		of a specific type are given in the plotOptions of that type, for example plotOptions.line.. 		Next, options for one single series are given in <a class="internal" href="#series">the . 		series array</a>.</p>
    * @param responsive Allows setting a set of rules to apply for different screen or chart sizes. Each rule specifies additional chart options.
    * @param subtitle    The chart's subtitle
    * @param title       The chart's main title.
    * @param tooltip     Options for the tooltip that appears when the user hovers over a series or point.
    * @param xAxis       <p>The X axis or category axis. Normally this is the horizontal axis, though if the . 	chart is inverted this is the vertical axis. In case of multiple axes, the xAxis. 	node is an array of configuration objects.</p>. 	<p>See <a class="internal" href="#axis.object">the Axis object</a> for programmatic. 	access to the axis.</p>
    * @param yAxis       <p>The Y axis or value axis. Normally this is the vertical axis, though if the . 	chart is inverted this is the horizontal axis. In case of multiple axes, the yAxis. 	node is an array of configuration objects.</p>. 	<p>See <a class="internal" href="#axis.object">the Axis object</a> for programmatic. 	access to the axis.</p>
    * @param zAxis       The Z axis or depth axis for 3D plots.
    * @param colorAxis   The color axis for heatmaps.
    */
  def apply(accessibility: js.UndefOr[Accessibility] = js.undefined,
            chart: js.UndefOr[Chart] = js.undefined,
            colors: js.UndefOr[Seq[Color]] = js.undefined,
            credits: js.UndefOr[Credits] = js.undefined,
            data: js.UndefOr[Data] = js.undefined,
            drilldown: js.UndefOr[Drilldown] = js.undefined,
            exporting: js.UndefOr[Exporting] = js.undefined,
            labels: js.UndefOr[Labels] = js.undefined,
            legend: js.UndefOr[Legend] = js.undefined,
            loading: js.UndefOr[Loading] = js.undefined,
            navigation: js.UndefOr[Navigation] = js.undefined,
            noData: js.UndefOr[NoData] = js.undefined,
            pane: js.UndefOr[Pane] = js.undefined,
            plotOptions: js.UndefOr[PlotOptions] = js.undefined,
            responsive: js.UndefOr[Responsive] = js.undefined,
            series: js.UndefOr[Seq[Series]] = js.undefined,
            subtitle: js.UndefOr[Subtitle] = js.undefined,
            title: js.UndefOr[Title] = js.undefined,
            tooltip: js.UndefOr[Tooltip] = js.undefined,
            xAxis: js.UndefOr[Seq[XAxis]] = js.undefined,
            yAxis: js.UndefOr[Seq[YAxis]] = js.undefined,
            zAxis: js.UndefOr[ZAxis] = js.undefined,
            colorAxis: js.UndefOr[ColorAxis] = js.undefined): HighchartsConfig = {
    val seriesOuter = series.map(_.toJSArray)
    val accessibilityOuter = accessibility
    val chartOuter = chart
    val colorsOuter = colors.map(_.map(_.c).toJSArray)
    val creditsOuter = credits
    val dataOuter = data
    val drilldownOuter = drilldown
    val exportingOuter = exporting
    val labelsOuter = labels
    val legendOuter = legend
    val loadingOuter = loading
    val navigationOuter = navigation
    val noDataOuter = noData
    val paneOuter = pane
    val plotOptionsOuter = plotOptions
    val responsiveOuter = responsive
    val subtitleOuter = subtitle
    val titleOuter = title
    val tooltipOuter = tooltip
    val xAxisOuter = xAxis.map(_.toJSArray)
    val yAxisOuter = yAxis.map(_.toJSArray)
    val zAxisOuter = zAxis
    val colorAxisOuter = colorAxis

    new HighchartsConfig {
      override val series = seriesOuter
      override val accessibility = accessibilityOuter
      override val chart = chartOuter
      override val colors = colorsOuter
      override val credits = creditsOuter
      override val data = dataOuter
      override val drilldown = drilldownOuter
      override val exporting = exportingOuter
      override val labels = labelsOuter
      override val legend = legendOuter
      override val loading = loadingOuter
      override val navigation = navigationOuter
      override val noData = noDataOuter
      override val pane = paneOuter
      override val plotOptions = plotOptionsOuter
      override val responsive = responsiveOuter
      override val subtitle = subtitleOuter
      override val title = titleOuter
      override val tooltip = tooltipOuter
      override val xAxis = xAxisOuter
      override val yAxis = yAxisOuter
      override val zAxis = zAxisOuter
      override val colorAxis = colorAxisOuter
    }
  }
}
