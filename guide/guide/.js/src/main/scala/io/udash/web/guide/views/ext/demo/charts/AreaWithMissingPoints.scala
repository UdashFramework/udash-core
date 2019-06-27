package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.XAxis
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.area.SeriesArea
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.utils.{Align, Spacing, VerticalAlign}

object AreaWithMissingPoints {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/area-missing"),
    chart = Chart(
      spacing = Spacing(0, 0, 30, 0),
      inverted = true
    ),
    title = Title(
      text = "Fruit consumption* (inverted)"
    ),
    subtitle = Subtitle(
      text = "* Jane's banana consumption is unknown",
      floating = true,
      align = Align.Right,
      verticalAlign = VerticalAlign.Bottom,
      y = 15.0
    ),
    xAxis = Seq(
      XAxis(
        categories = Seq("Apples", "Pears", "Oranges", "Bananas", "Grapes", "Plums", "Strawberries", "Raspberries")
      )
    ),
    plotOptions = PlotOptions(area = SeriesArea(fillOpacity = 0.5)),
    series = Seq(
      SeriesArea(
        name = "John",
        data = Seq(0, 1, 4, 4, 5, 2, 3, 7)
      ),
      SeriesArea(
        name = "Jane",
        data = Seq(
          1, 0, 3,
          null,
          3, 1, 2, 1
        )
      )
    )
  )
}
