/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package series.treemap

import io.udash.wrappers.highcharts.config.series.{BaseSeriesData, SeriesDataEvents, SeriesDataLabels}
import io.udash.wrappers.highcharts.config.utils.Color

import scala.scalajs.js

trait SeriesTreemapData extends BaseSeriesData[SeriesDataLabels] {
  /**
    * Serves a purpose only if a colorAxis object is defined in the chart options. This value will decide which color the point gets from the scale of the colorAxis.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/treemap-coloraxis">Treemap with a colorAxis</a>
    */
  val colorValue: js.UndefOr[Double] = js.undefined

  /**
    * Only for treemap. Use this option to build a tree structure. The value should be the id of the point which is the parent. If no points has a matching id, or this option is undefined, then the parent will be set to the root.
    *
    * @example <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/point/parent/" target="_blank">Point parent</a>, <a href="http://jsfiddle.net/gh/get/jquery/3.1.1/highcharts/highcharts/tree/master/samples/highcharts/demo/treemap-with-levels/" target="_blank">Example where parent id is not matching</a>
    */
  val parent: js.UndefOr[String] = js.undefined

  /**
    * The value of the point, resulting in a relative area of the point in the treemap.
    */
  val value: js.UndefOr[Double] = js.undefined
}

object SeriesTreemapData {

  /**
    * @param color      The color of the point. In heat maps the point color is rarely set explicitly, as we use the color to denote the <code>value</code>. Options for this are set in the <a href="#colorAxis">colorAxis</a> configuration.
    * @param colorValue Serves a purpose only if a colorAxis object is defined in the chart options. This value will decide which color the point gets from the scale of the colorAxis.
    * @param dataLabels Individual data label for each point. The options are the same as the ones for  <a class="internal" href="#plotOptions.series.dataLabels">plotOptions.series.dataLabels</a>
    * @param drilldown  The <code>id</code> of a series in the <a href="#drilldown.series">drilldown.series</a> array to use for a drilldown for this point.
    * @param events     Individual point events
    * @param id         An id for the point. This can be used after render time to get a pointer to the point object through <code>chart.get()</code>.
    * @param labelrank  The rank for this point's data label in case of collision. If two data labels are about to overlap, only the one with the highest <code>labelrank</code> will be drawn.
    * @param name       <p>The name of the point as shown in the legend, tooltip, dataLabel etc.</p>. . <p>If the <a href="#xAxis.type">xAxis.type</a> is set to <code>category</code>, and no <a href="#xAxis.categories">categories</a> option exists, the category will be pulled from the <code>point.name</code> of the last series defined. For multiple series, best practice however is to define <code>xAxis.categories</code>.</p>
    * @param parent     Only for treemap. Use this option to build a tree structure. The value should be the id of the point which is the parent. If no points has a matching id, or this option is undefined, then the parent will be set to the root.
    * @param selected   Whether the data point is selected initially.
    * @param value      The value of the point, resulting in a relative area of the point in the treemap.
    */
  def apply(color: js.UndefOr[Color] = js.undefined,
            colorValue: js.UndefOr[Double] = js.undefined,
            dataLabels: js.UndefOr[SeriesDataLabels] = js.undefined,
            description: js.UndefOr[String] = js.undefined,
            drilldown: js.UndefOr[String] = js.undefined,
            events: js.UndefOr[SeriesDataEvents] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            labelrank: js.UndefOr[Double] = js.undefined,
            name: js.UndefOr[String] = js.undefined,
            parent: js.UndefOr[String] = js.undefined,
            selected: js.UndefOr[Boolean] = js.undefined,
            value: js.UndefOr[Double] = js.undefined): SeriesTreemapData = {
    val colorOuter = color.map(_.c)
    val colorValueOuter = colorValue
    val dataLabelsOuter = dataLabels
    val descriptionOuter = description
    val drilldownOuter = drilldown
    val eventsOuter = events
    val idOuter = id
    val labelrankOuter = labelrank
    val nameOuter = name
    val parentOuter = parent
    val selectedOuter = selected
    val valueOuter = value

    new SeriesTreemapData {
      override val color = colorOuter
      override val colorValue = colorValueOuter
      override val dataLabels = dataLabelsOuter
      override val description = descriptionOuter
      override val drilldown = drilldownOuter
      override val events = eventsOuter
      override val id = idOuter
      override val labelrank = labelrankOuter
      override val name = nameOuter
      override val parent = parentOuter
      override val selected = selectedOuter
      override val value = valueOuter
    }
  }
}
