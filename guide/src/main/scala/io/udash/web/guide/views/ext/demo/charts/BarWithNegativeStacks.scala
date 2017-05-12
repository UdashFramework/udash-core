package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.bar.SeriesBar
import io.udash.wrappers.highcharts.config.title.Title
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.Stacking

import scalajs.js.`|`

object BarWithNegativeStacks {
  val categories = Seq(
    "0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49",
    "50-54", "55-59", "60-64", "65-69", "70-74", "75-79", "80-84", "85-89", "90-94", "95-99", "100+"
  )

  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/bar-negative-stack"),
    title = Title(
      text = "Population pyramid for Germany, 2015"
    ),
    xAxis = Seq(
      XAxis(
        categories = categories,
        reversed = false,
        labels = XAxisLabel(step = 1)
      ),
      XAxis(
        categories = categories,
        opposite = true,
        reversed = false,
        linkedTo = 0,
        labels = XAxisLabel(step = 1)
      )
    ),
    yAxis = Seq(
      YAxis(
        labels = YAxisLabel(formatter = (data: AxisLabel.FormatterData[YAxis]) => Math.abs(data.value.asInstanceOf[Double]) + "%")
      )
    ),
    tooltip = Tooltip(formatter = (data: Tooltip.FormatterData) => {
      s"""<b>${data.series.name}, age ${data.point.category}</b><br/>
          |Population: ${Math.abs(data.point.y)}""".stripMargin: String | Boolean
    }),
    plotOptions = PlotOptions(bar = SeriesBar(stacking = Stacking.Normal)),
    series = Seq(
      SeriesBar(
        name = "Male",
        data = Seq(-2.2, -2.2, -2.3, -2.5, -2.7, -3.1, -3.2,
          -3.0, -3.2, -4.3, -4.4, -3.6, -3.1, -2.4,
          -2.5, -2.3, -1.2, -0.6, -0.2, -0.0, -0.0)
      ),
      SeriesBar(
        name = "Female",
        data = Seq(2.1, 2.0, 2.2, 2.4, 2.6, 3.0, 3.1, 2.9,
          3.1, 4.1, 4.3, 3.6, 3.4, 2.6, 2.9, 2.9,
          1.8, 1.2, 0.6, 0.1, 0.0)
      )
    )
  )
}
