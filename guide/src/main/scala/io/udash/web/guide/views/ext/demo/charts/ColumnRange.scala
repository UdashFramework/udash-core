package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.columnrnge.{SeriesColumnrange, SeriesColumnrangeData}
import io.udash.wrappers.highcharts.config.series.{SeriesDataLabels, SeriesRangeDataLabels}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip

object ColumnRange {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/columnrange"),
    chart = Chart(inverted = true),
    title = Title(
      text = "Temperature variation by month"
    ),
    subtitle = Subtitle(
      text = "Observed in Vik i Sogn, Norway"
    ),
    xAxis = Seq(
      XAxis(
        categories = Seq("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
      )
    ),
    yAxis = Seq(
      YAxis(title = AxisTitle(text = "Temperature (°C)"))
    ),
    plotOptions = PlotOptions(
      columnRange = SeriesColumnrange(
        dataLabels = SeriesRangeDataLabels(
          enabled = true,
          formatter = (data: SeriesDataLabels.FormatterData) => data.y + "°C"
        )
      )
    ),
    tooltip = Tooltip(valueSuffix = "°C"),
    legend = Legend(enabled = false),
    series = Seq(
      SeriesColumnrange(
        name = "Temperatures",
        data = Seq(
          (-9.7, 9.4),
          (-8.7, 6.5),
          (-3.5, 9.4),
          (-1.4, 19.9),
          (0.0, 22.6),
          (2.9, 29.5),
          (9.2, 30.7),
          (7.3, 26.5),
          (4.4, 18.0),
          (-3.1, 11.4),
          (-5.2, 10.4),
          (-13.5, 9.8)
        ).map {
          case (l, h) => SeriesColumnrangeData(low = l, high = h)
        }
      )
    )
  )
}
