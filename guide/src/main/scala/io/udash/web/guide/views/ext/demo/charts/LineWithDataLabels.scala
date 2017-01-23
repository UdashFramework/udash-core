package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{AxisPlotLine, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.SeriesDataLabels
import io.udash.wrappers.highcharts.config.series.line.SeriesLine
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.utils.Color

object LineWithDataLabels {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/line-labels"),
    title = Title(
      text = "Monthly Average Temperature",
      x = -20.0
    ),
    subtitle = Subtitle(
      text = "Source: WorldClimate.com",
      x = -20.0
    ),
    xAxis = Seq(
      XAxis(
        categories = Seq("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
      )
    ),
    yAxis = Seq(
      YAxis(
        title = AxisTitle(text = "Temperature (°C)"),
        plotLines = Seq(AxisPlotLine(value = 0.0, width = 1.0, color = Color(118, 118, 118)))
      )
    ),
    plotOptions = PlotOptions(
      line = SeriesLine(
        dataLabels = SeriesDataLabels(enabled = true),
        enableMouseTracking = false
      )
    ),
    series = Seq(
      SeriesLine(
        name = "Tokyo",
        data = Seq(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)
      ),
      SeriesLine(
        name = "Berlin",
        data = Seq(-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)
      )
    )
  )
}
