package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.series.column.SeriesColumn
import io.udash.wrappers.highcharts.config.series.spline.SeriesSpline
import io.udash.wrappers.highcharts.config.series.{SeriesMarker, SeriesTooltip}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils._

import scala.scalajs.js
import scala.scalajs.js.|

object MultipleAxes {
  val defaultColors = js.Array(
    "#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9",
    "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"
  )

  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Multiple axes", href = "http://www.highcharts.com/demo/combo-multi-axes"),
    title = Title(
      text = "Average Monthly Weather Data for Tokyo"
    ),
    subtitle = Subtitle(
      text = "Source: WorldClimate.com"
    ),
    xAxis = Seq(
      XAxis(categories = Seq("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"))
    ),
    yAxis = Seq(
      YAxis(
        labels = YAxisLabel(
          format = "{value}°C",
          style = s"{color: '${defaultColors(2)}'}"
        ),
        title = AxisTitle(
          text = "Temperature",
          style = s"{color: '${defaultColors(2)}'}"
        ),
        opposite = true
      ),
      YAxis(
        labels = YAxisLabel(
          format = "{value}mm",
          style = s"{color: '${defaultColors(0)}'}"
        ),
        title = AxisTitle(
          text = "Rainfall",
          style = s"{color: '${defaultColors(0)}'}"
        ),
        opposite = false
      ),
      YAxis(
        labels = YAxisLabel(
          format = "{value}mb",
          style = s"{color: '${defaultColors(1)}'}"
        ),
        title = AxisTitle(
          text = "Sea-Level Pressure",
          style = s"{color: '${defaultColors(1)}'}"
        ),
        opposite = true
      )
    ),
    tooltip = Tooltip(shared = true),
    legend = Legend(enabled = true, layout = Layout.Vertical),
    series = Seq(
      SeriesColumn(
        name = "Rainfall",
        yAxis = 1: Int | String,
        data = Seq(49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4),
        tooltip = SeriesTooltip(valueSuffix = "mm")
      ),
      SeriesSpline(
        name = "Sea-Level Pressure",
        yAxis = 2: Int | String,
        data = Seq(1016, 1016, 1015.9, 1015.5, 1012.3, 1009.5, 1009.6, 1010.2, 1013.1, 1016.9, 1018.2, 1016.7),
        marker = SeriesMarker(enabled = false),
        dashStyle = DashStyle.ShortDot,
        tooltip = SeriesTooltip(valueSuffix = "mb")
      ),
      SeriesSpline(
        name = "Temperature",
        yAxis = 0: Int | String,
        data = Seq(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6),
        tooltip = SeriesTooltip(valueSuffix = "°C")
      )
    )
  )
}
