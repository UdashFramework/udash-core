/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.columnrnge

import io.udash.wrappers.highcharts.config.series.{BaseXSeriesData, SeriesDataEvents, SeriesRangeDataLabels}
import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js

@js.annotation.ScalaJSDefined
class SeriesColumnrangeData extends BaseXSeriesData[SeriesRangeDataLabels] {
  /**
    * The high or maximum value for each data point.
    */
  val high: js.UndefOr[Double] = js.undefined

  /**
    * The low or minimum value for each data point.
    */
  val low: js.UndefOr[Double] = js.undefined
}

object SeriesColumnrangeData {
  import scala.scalajs.js.JSConverters._

  /**
    * @param color      Individual color for the point. By default the color is pulled from the global <code>colors</code> array.
    * @param dataLabels Individual data label for each point. The options are the same as the ones for  <a class="internal" href="#plotOptions.series.dataLabels">plotOptions.series.dataLabels</a>
    * @param drillDown  The <code>id</code> of a series in the <a href="#drilldown.series">drilldown.series</a> array to use for a drilldown for this point.
    * @param events     Individual point events
    * @param high       The high or maximum value for each data point.
    * @param id         An id for the point. This can be used after render time to get a pointer to the point object through <code>chart.get()</code>.
    * @param labelRank  The rank for this point's data label in case of collision. If two data labels are about to overlap, only the one with the highest <code>labelrank</code> will be drawn.
    * @param low        The low or minimum value for each data point.
    * @param name       <p>The name of the point as shown in the legend, tooltip, dataLabel etc.</p>. . <p>If the <a href="#xAxis.type">xAxis.type</a> is set to <code>category</code>, and no <a href="#xAxis.categories">categories</a> option exists, the category will be pulled from the <code>point.name</code> of the last series defined. For multiple series, best practice however is to define <code>xAxis.categories</code>.</p>
    * @param selected   Whether the data point is selected initially.
    * @param x          The x value of the point. For datetime axes, the X value is the timestamp in milliseconds since 1970.
    */
  def apply(color: js.UndefOr[Color] = js.undefined,
            dataLabels: js.UndefOr[SeriesRangeDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            drillDown: js.UndefOr[String] = js.undefined,
            events: js.UndefOr[SeriesDataEvents] = js.undefined,
            high: js.UndefOr[Double] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            labelRank: js.UndefOr[Double] = js.undefined,
            low: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            x: js.UndefOr[Double] = js.undefined): SeriesColumnrangeData = {
    val colorOuter = color.map(_.c)
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val drilldownOuter = drillDown
    val eventsOuter = events
    val highOuter: js.UndefOr[Double] = high
    val idOuter = id
    val labelrankOuter = labelRank
    val lowOuter: js.UndefOr[Double] = low
    val nameOuter = name
    val selectedOuter = selected
    val xOuter = x

    new SeriesColumnrangeData {
      override val color = colorOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val drilldown = drilldownOuter
      override val events = eventsOuter
      override val high = highOuter
      override val id = idOuter
      override val labelrank = labelrankOuter
      override val low = lowOuter
      override val name = nameOuter
      override val selected = selectedOuter
      override val x = xOuter
    }
  }
}
