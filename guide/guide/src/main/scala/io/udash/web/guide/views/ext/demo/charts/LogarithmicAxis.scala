package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{Axis, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.series.line.SeriesLine
import io.udash.wrappers.highcharts.config.title.Title

object LogarithmicAxis {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/line-log-axis"),
    title = Title(
      text = "Logarithmic axis demo"
    ),
    xAxis = Seq(
      XAxis(
        tickInterval = 1.0
      )
    ),
    yAxis = Seq(
      YAxis(
        `type` = Axis.Type.Logarithmic,
        minorTickInterval = 0.1
      )
    ),
    series = Seq(
      SeriesLine(
        pointStart = 1.0,
        data = Seq(1, 2, 4, 8, 16, 32, 64, 128, 256, 512)
      )
    )
  )
}
