/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.heatmap

import io.udash.wrappers.highcharts.config.series.{BaseTwoDimSeriesData, SeriesDataEvents, SeriesDataLabels}
import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js

trait SeriesHeatmapData extends BaseTwoDimSeriesData[SeriesDataLabels] {
  /**
    * The value of the point, resulting in a color controled by options as set in the <a href="#colorAxis">colorAxis</a> configuration.
    */
  val value: js.UndefOr[Double] = js.undefined
}

object SeriesHeatmapData {
  import scala.scalajs.js.JSConverters._

  /**
    * @param color      Individual color for the point. By default the color is pulled from the global <code>colors</code> array.
    * @param dataLabels Individual data label for each point. The options are the same as the ones for  <a class="internal" href="#plotOptions.series.dataLabels">plotOptions.series.dataLabels</a>
    * @param drillDown  The <code>id</code> of a series in the <a href="#drilldown.series">drilldown.series</a> array to use for a drilldown for this point.
    * @param events     Individual point events
    * @param id         An id for the point. This can be used after render time to get a pointer to the point object through <code>chart.get()</code>.
    * @param labelRank  The rank for this point's data label in case of collision. If two data labels are about to overlap, only the one with the highest <code>labelrank</code> will be drawn.
    * @param name       <p>The name of the point as shown in the legend, tooltip, dataLabel etc.</p>. . <p>If the <a href="#xAxis.type">xAxis.type</a> is set to <code>category</code>, and no <a href="#xAxis.categories">categories</a> option exists, the category will be pulled from the <code>point.name</code> of the last series defined. For multiple series, best practice however is to define <code>xAxis.categories</code>.</p>
    * @param selected   Whether the data point is selected initially.
    * @param value      The value of the point, resulting in a color controled by options as set in the <a href="#colorAxis">colorAxis</a> configuration.
    * @param x          The x value of the point. For datetime axes, the X value is the timestamp in milliseconds since 1970.
    * @param y          The y value of the point.
    */
  def apply(color: js.UndefOr[Color] = js.undefined,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            drillDown: js.UndefOr[String] = js.undefined,
            events: js.UndefOr[SeriesDataEvents] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            labelRank: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            value: js.UndefOr[Double] = js.undefined,
            x: js.UndefOr[Double] = js.undefined,
            y: js.UndefOr[Double] = js.undefined): SeriesHeatmapData = {
    val colorOuter = color.map(_.c)
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val drilldownOuter = drillDown
    val eventsOuter = events
    val idOuter = id
    val labelrankOuter = labelRank
    val nameOuter = name
    val selectedOuter = selected
    val valueOuter = value
    val xOuter = x
    val yOuter = y

    new SeriesHeatmapData {
      override val color = colorOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val drilldown = drilldownOuter
      override val events = eventsOuter
      override val id = idOuter
      override val labelrank = labelrankOuter
      override val name = nameOuter
      override val selected = selectedOuter
      override val value: js.UndefOr[Double] = valueOuter
      override val x = xOuter
      override val y = yOuter
    }
  }
}
