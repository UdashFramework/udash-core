package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{AxisPlotLine, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.series.line.SeriesLine
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.{Align, Color, Layout, VerticalAlign}

object BasicLine {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/line-basic"),
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
    tooltip = Tooltip(valueSuffix = "°C"),
    legend = Legend(
      layout = Layout.Vertical,
      align = Align.Right,
      verticalAlign = VerticalAlign.Middle,
      borderWidth = 0.0
    ),
    series = Seq(
      SeriesLine(
        name = "Tokyo",
        data = Seq(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)
      ),
      SeriesLine(
        name = "New York",
        data = Seq(-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)
      ),
      SeriesLine(
        name = "Berlin",
        data = Seq(-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)
      ),
      SeriesLine(
        name = "London",
        data = Seq(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8)
      )
    )
  )
}
