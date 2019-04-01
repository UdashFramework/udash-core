package io.udash.web.guide.views.ext.demo.charts

import java.util.Date

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{Axis, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.chart.Chart.ZoomType
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.area.SeriesArea
import io.udash.wrappers.highcharts.config.series.{SeriesAreaStates, SeriesAreaStatesHover, SeriesMarker}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.utils.{Color, PointIntervalUnit}

import scala.util.Random

object ZoomableTimeSeries {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/line-time-series"),
    chart = Chart(zoomType = ZoomType.X),
    title = Title(
      text = "USD to EUR exchange rate over time"
    ),
    subtitle = Subtitle(
      text = "Click and drag in the plot area to zoom in"
    ),
    xAxis = Seq(
      XAxis(
        `type` = Axis.Type.DateTime
      )
    ),
    yAxis = Seq(
      YAxis(
        title = AxisTitle(text = "Exchange rate")
      )
    ),
    legend = Legend(enabled = false),
    plotOptions = PlotOptions(
      area = SeriesArea(
        fillColor = Color.gradient(
          start = (0, 0), stop = (0, 1),
          stops = Seq(
            (0.0, Color("#058DC7")),
            (1.0, Color(5, 141, 199, 0))
          )
        ),
        marker = SeriesMarker(radius = 2.0),
        lineWidth = 1.0,
        states = SeriesAreaStates(hover = SeriesAreaStatesHover(lineWidth = 1.0))
      )
    ),
    series = Seq(
      SeriesArea(
        name = "USD to EUR",
        data = Seq.fill(1000)(Random.nextDouble() + 0.5),
        pointStart = new Date().getTime.toDouble,
        pointInterval = 1.0,
        pointIntervalUnit = PointIntervalUnit.Day
      )
    )
  )
}
