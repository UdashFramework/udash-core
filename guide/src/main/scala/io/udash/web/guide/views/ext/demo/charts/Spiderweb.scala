package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{XAxis, YAxis}
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.pane.Pane
import io.udash.wrappers.highcharts.config.series.line.{SeriesLine, SeriesLineData}
import io.udash.wrappers.highcharts.config.title.Title
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.{PointPlacement, TickmarkPlacement}

import scala.scalajs.js.|

object Spiderweb {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/line-labels"),
    chart = Chart(polar = true),
    title = Title(
      text = "Budget vs spending"
    ),
    pane = Pane(size = "80%"),
    xAxis = Seq(
      XAxis(
        categories = Seq("Sales", "Marketing", "Development", "Customer Support", "Information Technology", "Administration"),
        tickmarkPlacement = TickmarkPlacement.On,
        lineWidth = 0.0
      )
    ),
    yAxis = Seq(
      YAxis(
        gridLineInterpolation = YAxis.GridLineInterpolation.Polygon,
        lineWidth = 0.0,
        min = 0.0
      )
    ),
    tooltip = Tooltip(shared = false),
    series = Seq(
      SeriesLine(
        name = "Allocated Budget",
        data = Seq[SeriesLineData | Double](43000, 19000, 60000, 35000, 17000, 10000),
        pointPlacement = PointPlacement.OnTick
      ),
      SeriesLine(
        name = "Actual Spending",
        data = Seq[SeriesLineData | Double](50000, 39000, 42000, 31000, 26000, 14000),
        pointPlacement = PointPlacement.OnTick
      )
    )
  )
}
