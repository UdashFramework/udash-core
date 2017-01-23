package io.udash.web.guide.views.ext.demo.charts

import java.{util => ju}

import com.github.ghik.silencer.silent
import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.chart.Chart.ZoomType
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.heatmap.{SeriesHeatmap, SeriesHeatmapData}
import io.udash.wrappers.highcharts.config.series.scatter.{SeriesScatter, SeriesScatterData}
import io.udash.wrappers.highcharts.config.series.{SeriesMarker, SeriesTooltip}
import io.udash.wrappers.highcharts.config.title.Title
import io.udash.wrappers.highcharts.config.utils.{Align, Color, Layout}


import scala.util.Random

object HeatMap {
  private def dateFromYear(year: Int): ju.Date = {
    import scalajs.js
    val jsd = new js.Date(year, 0, 1)
    new ju.Date(jsd.getTime().toLong)
  }

  val startDate: ju.Date = dateFromYear(2013)
  val endDate: ju.Date = dateFromYear(2014)

  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/heatmap-canvas"),
    chart = Chart(zoomType = ZoomType.XY),
    title = Title(
      text = "Highcharts extended heat map",
      align = Align.Left,
      x = 40.0
    ),
    xAxis = Seq(
      XAxis(
        `type` = Axis.Type.DateTime,
//        min = startDate.getTime.toDouble,
//        max = endDate.getTime.toDouble,
        tickLength = 16.0
      )
    ),
    yAxis = Seq(
      YAxis(
        title = AxisTitle(text = null),
        maxPadding = 0.0,
        minPadding = 0.0,
        startOnTick = false,
        endOnTick = false,
        tickPositions = Seq[Double](0, 6, 12, 18, 24),
        tickWidth = 1.0,
        min = 0.0,
        max = 23.0,
        reversed = true
      )
    ),
    colorAxis = ColorAxis(
      stops = Seq[(Double, Color)](
        (0, Color("#3060cf")),
        (0.5, Color("#fffbbc")),
        (0.9, Color("#c4463a")),
        (1, Color("#c4463a"))
      ),
      min = -15.0,
      max = 25.0,
      startOnTick = false,
      endOnTick = false,
      labels = ColorAxisLabel(format = "{value}℃")
    ),
    legend = Legend(enabled = true, layout = Layout.Horizontal),
    series = Seq(
      SeriesHeatmap(
        borderWidth = 0.0,
        nullColor = Color("#EFEFEF"),
        turboThreshold = Double.MaxValue,
        colsize = 24 * 36e5,
        data = Seq.fill(24 * 365)(Random.nextInt(40) - 15).zipWithIndex.map {
          case (v, idx) => SeriesHeatmapData(
            x = startDate.getTime.toDouble + ((idx % 365).toDouble * 24.0 * 60.0 * 60.0 * 1000.0),
            y = (idx / 365).toDouble,
            value = v.toDouble
          )
        },
        tooltip = SeriesTooltip(
          headerFormat = "Temperature<br/>",
          pointFormat = "{point.x:%e %b, %Y} {point.y}:00: <b>{point.value} ℃</b>"
        )
      )
    )
  )
}
