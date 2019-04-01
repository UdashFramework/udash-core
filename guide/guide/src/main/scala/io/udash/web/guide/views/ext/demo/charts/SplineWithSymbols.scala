package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.spline.{SeriesSpline, SeriesSplineData}
import io.udash.wrappers.highcharts.config.series.{SeriesDataMarker, SeriesMarker}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.Color

object SplineWithSymbols {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/spline-symbols"),
    title = Title(
      text = "Monthly Average Temperature"
    ),
    subtitle = Subtitle(
      text = "Source: WorldClimate.com"
    ),
    xAxis = Seq(
      XAxis(
        `type` = Axis.Type.Category,
        categories = Seq("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
      )
    ),
    yAxis = Seq(
      YAxis(
        title = AxisTitle(text = "Temperature"),
        labels = YAxisLabel(
          formatter = (data: AxisLabel.FormatterData[YAxis]) => data.value.toString + "°"
        )
      )
    ),
    tooltip = Tooltip(shared = true),
    plotOptions = PlotOptions(
      spline = SeriesSpline(
        marker = SeriesMarker(radius = 4.0, lineColor = Color("#666666"), lineWidth = 1.0)
      )
    ),
    series = Seq(
      SeriesSpline(
        name = "Tokyo",
        marker = SeriesMarker(symbol = SeriesMarker.Symbol.Square),
        data = Seq(
          7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2,
          SeriesSplineData(y = 26.5, marker = SeriesDataMarker(symbol = SeriesMarker.Symbol.Url("https://www.highcharts.com/samples/graphics/sun.png"))),
          23.3, 18.3, 13.9, 9.6
        )
      ),
      SeriesSpline(
        name = "London",
        marker = SeriesMarker(symbol = SeriesMarker.Symbol.Diamond),
        data = Seq(
          SeriesSplineData(y = 3.9, marker = SeriesDataMarker(symbol = SeriesMarker.Symbol.Url("https://www.highcharts.com/samples/graphics/snow.png"))),
          4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8
        )
      )
    )
  )
}
