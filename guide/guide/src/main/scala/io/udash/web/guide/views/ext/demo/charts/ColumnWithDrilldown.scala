package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{Axis, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.drilldown.Drilldown
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.series.column.{SeriesColumn, SeriesColumnData}
import io.udash.wrappers.highcharts.config.title.Title
import io.udash.wrappers.highcharts.config.utils.Animation

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object ColumnWithDrilldown {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/column-drilldown"),
    title = Title(
      text = "Browser market shares. January, 2015 to May, 2015"
    ),
    xAxis = Seq(
      XAxis(`type` = Axis.Type.Category)
    ),
    yAxis = Seq(
      YAxis(title = AxisTitle(text = "Total percent market share"))
    ),
    legend = Legend(enabled = false),
    series = Seq(
      SeriesColumn(
        name = "Brands",
        colorByPoint = true,
        data = Seq(
          SeriesColumnData(name = "Microsoft Internet Explorer", y = 56.33, drilldown = "MIE"),
          SeriesColumnData(name = "Google Chrome", y = 24.03, drilldown = "Chrome"),
          SeriesColumnData(name = "Mozilla Firefox", y = 10.38, drilldown = "Firefox"),
          SeriesColumnData(name = "Other", y = 4.77 + 0.91 + 0.2)
        ),
        animation = Animation.Custom(
          duration = 60 seconds,
          easing = {
            case pos if pos < 1 / 2.75 => 7.5625 * pos * pos
            case pos if pos < 2 / 2.75 =>
              val pos1 = pos - (1.5 / 2.75)
              7.5625 * pos1 * pos1 + 0.75
            case pos if pos < 2.5 / 2.75 =>
              val pos1 = pos - (2.25 / 2.75)
              7.5625 * pos1 * pos1 + 0.9375
            case pos =>
              val pos1 = pos - (2.625 / 2.75)
              7.5625 * pos1 * pos1 + 0.984375
          }: Double => Double
        )
      )
    ),
    drilldown = Drilldown(series = Seq(
      SeriesColumn(
        id = "MIE",
        name = "Microsoft Internet Explorer",
        data = Seq(
          SeriesColumnData(name = "v11.0", y = 24.13),
          SeriesColumnData(name = "v8.0", y = 17.2),
          SeriesColumnData(name = "v9.0", y = 8.11),
          SeriesColumnData(name = "v10.0", y = 5.33)
        )
      ),
      SeriesColumn(
        id = "Chrome",
        name = "Google Chrome",
        data = Seq(
          SeriesColumnData(name = "v40.0", y = 5.0),
          SeriesColumnData(name = "v41.0", y = 4.32),
          SeriesColumnData(name = "v42.0", y = 3.68),
          SeriesColumnData(name = "v39.0", y = 2.96)
        )
      ),
      SeriesColumn(
        id = "Firefox",
        name = "Mozilla Firefox",
        data = Seq(
          SeriesColumnData(name = "v35", y = 2.76),
          SeriesColumnData(name = "v36", y = 2.32),
          SeriesColumnData(name = "v37", y = 2.31),
          SeriesColumnData(name = "v34", y = 1.27)
        )
      )
    ))
  )
}
