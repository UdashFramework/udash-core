package io.udash.web.guide.views.ext.demo.charts

import scala.scalajs.js.|
import scala.util.Random
import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{AxisPlotLine, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.chart.Chart
import io.udash.wrappers.highcharts.config.chart.Chart.ZoomType
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.SeriesMarker
import io.udash.wrappers.highcharts.config.series.line.SeriesLine
import io.udash.wrappers.highcharts.config.series.pie.{SeriesPie, SeriesPieData}
import io.udash.wrappers.highcharts.config.series.scatter.{SeriesScatter, SeriesScatterData}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.{Align, Color, Layout, VerticalAlign}

import scala.scalajs.js

object Scatter {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/scatter"),
    chart = Chart(zoomType = ZoomType.XY),
    title = Title(
      text = "Height Versus Weight of 507 Individuals by Gender"
    ),
    xAxis = Seq(
      XAxis(
        title = AxisTitle(text = "Source: Heinz  2003"),
        startOnTick = true,
        endOnTick = true,
        showLastLabel = true
      )
    ),
    yAxis = Seq(
      YAxis(title = AxisTitle(text = "Weight (kg)"))
    ),
    legend = Legend(enabled = true, layout = Layout.Vertical),
    plotOptions = PlotOptions(
      scatter = SeriesScatter(
        marker = SeriesMarker(radius = 5.0)
      )
    ),
    series = Seq(
      SeriesScatter(
        name = "Male",
        color = Color(223, 83, 83, 0.5),
        data = Seq.fill(207)((50 + Random.nextDouble() * 70, 150 + Random.nextDouble() * 50)).map {
          case (w, h) => SeriesScatterData(x = h, y = w).asInstanceOf[SeriesScatterData | Double]
        }
      ),
      SeriesScatter(
        name = "Female",
        color = Color(119, 152, 191, 0.5),
        data = Seq.fill(300)((40 + Random.nextDouble() * 60, 145 + Random.nextDouble() * 30)).map {
          case (w, h) => SeriesScatterData(x = h, y = w).asInstanceOf[SeriesScatterData | Double]
        }
      )
    )
  )
}
