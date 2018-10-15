/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.exporting.Exporting
import io.udash.wrappers.highcharts.config.series.{Series => SeriesConfig}
import io.udash.wrappers.highcharts.config.title.{Subtitle => SubtitleConfig, Title => TitleConfig}
import io.udash.wrappers.highcharts.config.utils.Animation
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.`|`


@js.native
trait Chart extends js.Object {

  /**
    * Set a new credits label for the chart.
    */
  def addCredits(options: Credits): Unit = js.native

  /**
    * Add a series to the chart as drilldown from a specific point in the parent series.
    * This method is used for async drilldown, when clicking a point in a series should result in loading and displaying
    * a more high-resolution series. When <i>not</i> async, the setup is simpler using the <a href="#drilldown.series">drilldown.series</a> options structure.
    *
    * @param point The existing Point object from which the drilldown will start.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/drilldown/async/" target="_blank">Async drilldown</a>
    */
  def addSeriesAsDrilldown(point: Point, seriesOptions: SeriesConfig): Unit = js.native

  /**
    * The chart's credits label. The label has an update method that allows setting new options.
    */
  val credits: Credits = js.native

  /** A reference to the containing HTML element, dynamically inserted into the element given in <code>chart.renderTo</code>. */
  val container: dom.Element = js.native

  /**
    * Removes the chart and purges memory. This method should be called before writing a new chart into the same container.
    * It is called internally on window unload to prevent leaks.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-destroy/" target="_blank">Destroy the chart from a button</a>
    */
  def destroy(): Unit = js.native

  /** When the chart is drilled down to a child series, calling <code>chart.drillUp()</code> will drill up to the parent series. */
  def drillUp(): Unit = js.native

  /**
    * Exporting module required. Submit an SVG version of the chart to a server along with some parameters for conversion.
    *
    * @param options Exporting options. Out of the <a class="internal" href="#exporting">exporting</a> options,
    *                the following options can be given as parameters to the exportChart method.
    *                All options default to the values given in the exporting config options.
    *                <code>filename</code>: the filename for the export without extension,
    *                <code>url</code>: the URL for the server module to do the conversion,
    *                <code>width</code>: the width of the PNG or JPEG image generated on the server,
    *                <code>type</code>: the MIME type of the converted image,
    *                <code>sourceWidth</code>: the width of the source (in-page) chart,
    *                <code>sourceHeight</code>: the height of the source chart.
    * @param chartOptions Additional chart options for the exported chart. For example a different background color can be added here.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-exportchart/" target="_blank">Export with no options</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-exportchart-filename/" target="_blank">PDF type and custom filename</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-exportchart-custom-background/" target="_blank">different chart background in export</a>
    */
  def exportChart(options: Exporting, chartOptions: config.chart.Chart): Unit = js.native

  /**
    * Export the chart to a PNG or SVG without sending it to a server.
    * Requires <code>modules/exporting.js</code> and <code>modules/offline-exporting.js</code>.
    */
  def exportChartLocal(options: Exporting, chartOptions: config.chart.Chart): Unit = js.native

  /**
    * Get an axis, series or point by its <code>id</code> as given in the configuration options.
    *
    * @param id The id of the axis, series or point to get.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-id/" target="_blank">Get series by id</a>
    */
  def get(id: String): Axis[_, _] | Series | Point = js.native

  /**
    * Exporting module required. Get an SVG string representing the chart.
    *
    * @param additionalOptions Chart options to add to the exported chart in addition to the options given for the original chart.
    *                          For example if series.lineWidth should be greater in the exported chart than in the original,
    *                          or the chart should have a different background color, this is added here.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-getsvg/" target="_blank">View the SVG from a button</a>
    */
  def getSVG(additionalOptions: config.HighchartsConfig): String = js.native

  /**
    * Hide the loading screen. Options for the loading screen are defined at <a class="internal" href="#loading">options.loading</a>.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-hideloading/" target="_blank">Show and hide loading from a button</a>
    */
  def hideLoading(): Unit = js.native

  def legend: Legend

  /**
    * The options structure for the chart.
    *
    */
  val options: HighchartsConfig = js.native

  /**
    * Exporting module required. Clears away other elements in the page and prints the chart as it is displayed.
    * By default, when the exporting module is enabled, a button at the upper left calls this method.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-print/" target="_blank">Print from a HTML button</a>
    */
  def print(): Unit = js.native


  /**
    * Reflows the chart to its container. By default, the chart reflows automatically to its container following a <code>window.resize</code> event, as per the <a href="#chart.reflow">chart.reflow</a> option. However, there are no reliable events for div resize, so if the container is resized without a window resize event, this must be called explicitly.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-reflow/" target="_blank">Resize div and reflow</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/chart/events-container/" target="_blank">pop up and reflow</a>
    */
  def reflow(): Unit = js.native

  /**
    * Set a new title or subtitle for the chart
    *
    * @param title A configuration object for the new title as defined at <a class="internal" href="#title">#title</a>.
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-settitle/" target="_blank">Set title text and styles</a>
    */
  def setTitle(title: TitleConfig, subtitle: SubtitleConfig, redraw: Boolean = js.native): Unit = js.native

  /**
    * Dim the chart's plot area and show a loading label text. Options for the loading screen are defined at
    * <a class="internal" href="#loading">options.loading</a>. A custom text can be given as a parameter for loading.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-hideloading/" target="_blank">Show and hide loading from a button</a>,<a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-showloading/" target="_blank">apply different text labels.</a>
    */
  def showLoading(text: String = js.native): Unit = js.native

  /**
    * The chart subtitle. The subtitle has an `update` method that allows modifying the options.
    */
  def subtitle: Subtitle = js.native

  /**
    * The chart title. The title has an `update` method that allows modifying the options.
    */
  def title: Title = js.native

  /**
    * A generic function to update any element of the chart. Elements can be enabled and disabled, moved, re-styled, re-formatted etc.
    */
  def update(options: HighchartsConfig, redraw: Boolean = js.native): Unit = js.native

  @deprecated("Updating the chart position after a move operation is no longer necessary.", "0.5.0")
  def updatePosition(): Unit = js.native
}

object Chart {

  implicit class ChartExt(val chart: Chart) extends AnyVal {
    /**
      * Add an axis to the chart after render time. Note that this method should never be used when adding data synchronously
      * at chart render time, as it adds expense to the calculations and rendering.
      * When adding data at the same time as the chart is initiated, add the axis as a configuration option instead.
      *
      * @param options The Axis options, as documented under <a href="#xAxis">xAxis</a> and <a href="#yAxis">yAxis</a>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-addaxis/" target="_blank">Add and remove axes</a>
      */
    def addXAxis(options: config.axis.XAxis, redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      chart.asInstanceOf[js.Dynamic].addAxis(options, true, redraw, animation.value.asInstanceOf[js.Any])

    /**
      * Add an axis to the chart after render time. Note that this method should never be used when adding data synchronously
      * at chart render time, as it adds expense to the calculations and rendering.
      * When adding data at the same time as the chart is initiated, add the axis as a configuration option instead.
      *
      * @param options The Axis options, as documented under <a href="#xAxis">xAxis</a> and <a href="#yAxis">yAxis</a>.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-addaxis/" target="_blank">Add and remove axes</a>
      */
    def addYAxis(options: config.axis.YAxis, redraw: Boolean = true, animation: Animation = Animation.Enabled): Unit =
      chart.asInstanceOf[js.Dynamic].addAxis(options, false, redraw, animation.value.asInstanceOf[js.Any])

    /**
      * Add a series to the chart after render time. Note that this method should never be used when adding data synchronously
      * at chart render time, as it adds expense to the calculations and rendering. When adding data at the same time as the chart is initiated,
      * add the series as a configuration option instead. With multiple axes, the <a href="/highcharts#xAxis.offset">offset</a> is dynamically adjusted.
      *
      * @param options The series options, as documented under <a href="#plotOptions.series">plotOptions.series</a> and under the plotOptions for each series type.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-addseries/" target="_blank">Add a series from a button</a>
      */
    def addSeries(options: SeriesConfig, redraw: Boolean = true, animation: Animation = Animation.Enabled): Series =
      chart.asInstanceOf[js.Dynamic].addSeries(options, redraw, animation.value.asInstanceOf[js.Any]).asInstanceOf[Series]

    /**
      * Returns an array of all currently selected points in the chart. Points can be selected either programmatically
      * by the <code>point.select()</code> method or by clicking.
      *
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/plotoptions/series-allowpointselect-line/" target="_blank">Get selected points</a>
      */
    def selectedPoints(): Seq[Point] =
      chart.asInstanceOf[js.Dynamic].getSelectedPoints().asInstanceOf[js.Array[Point]].toSeq

    /**
      * Returns an array of all currently selected series in the chart. Series can be selected either programmatically
      * by the <code>series.select()</code> method or by checking the checkbox next to the legend item if <code>series.showCheckBox</code> is true.
      *
      * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/members/chart-getselectedseries/" target="_blank">Get selected series</a>
      */
    def selectedSeries(): Seq[Series] =
      chart.asInstanceOf[js.Dynamic].getSelectedSeries().asInstanceOf[js.Array[Series]].toSeq

    /**
      * Redraw the chart after changes have been done to the data or axis extremes. All methods for updating axes, series
      * or points have a parameter for redrawing the chart. This is <code>true</code> by default.
      * But in many cases you want to do more than one operation on the chart before redrawing, for example add a number of points.
      * In those cases it is a waste of resources to redraw the chart for each new point added. So you add the points and call <code>chart.redraw()</code> after.
      *
      * @param animation Defaults to true. When true, the update will be animated with default animation options. The animation can also be a configuration object with properties <code>duration</code> and <code>easing</code>.
      */
    def redraw(animation: Animation = Animation.Enabled): Unit =
      chart.asInstanceOf[js.Dynamic].redraw(animation.value.asInstanceOf[js.Any])

    /** An array of all the chart's series. */
    def series: Seq[Series] =
      chart.asInstanceOf[js.Dynamic].series.asInstanceOf[js.Array[Series]].toSeq

    /**
      * Resize the chart to a given width and height. In order to set the width only, the height argument can be skipped.
      * To set the height only, pass <code>undefined</code> for the width.
      *
      * @param width The new pixel width of the chart. Since v4.2.6, the argument can be <code>undefined</code> in order to preserve the current value (when setting height only), or <code>null</code> to adapt to the width of the containing element.
      * @example <a href="http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/members/chart-setsize-button/" target="_blank">Test resizing from buttons</a>, <a href="http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/members/chart-setsize-jquery-resizable/" target="_blank">add a jQuery UI resizable</a>
      */
    def setSize(width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined, animation: Animation = Animation.Enabled): Unit =
      chart.asInstanceOf[js.Dynamic].setSize(width, height, animation.value.asInstanceOf[js.Any])

    /** An array of the chart's x axes. If only one x axis, it is referenced by <code>chart.xAxis[0]</code>. */
    def xAxis: Seq[XAxis] =
      chart.asInstanceOf[js.Dynamic].xAxis.asInstanceOf[js.Array[XAxis]].toSeq

    /** An array of the chart's y axes. If only one y axis, it is referenced by <code>chart.yAxis[0]</code>. */
    def yAxis: Seq[YAxis] =
      chart.asInstanceOf[js.Dynamic].yAxis.asInstanceOf[js.Array[YAxis]].toSeq
  }
}