package io.udash.web.guide.views.ext.demo.charts

import java.util.Date

import com.github.ghik.silencer.silent
import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis._
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.plot.PlotOptions
import io.udash.wrappers.highcharts.config.series.spline.SeriesSpline
import io.udash.wrappers.highcharts.config.series.{SeriesAreaStates, SeriesAreaStatesHover, SeriesMarker}
import io.udash.wrappers.highcharts.config.title.{Subtitle, Title}
import io.udash.wrappers.highcharts.config.tooltip.Tooltip
import io.udash.wrappers.highcharts.config.utils.{Color, PointPlacement}

object SplineWithPlotBands {
  private def date(year: Int, month: Int, day: Int): Date = {
    import scalajs.js
    val jsd = new js.Date(year, month, day)
    new Date(jsd.getTime().toLong)
  }

  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/spline-plot-bands"),
    title = Title(
      text = "Wind speed during two days"
    ),
    subtitle = Subtitle(
      text = "May 31 and and June 1, 2015 at two locations in Vik i Sogn, Norway"
    ),
    xAxis = Seq(
      XAxis(
        `type` = Axis.Type.DateTime
      )
    ),
    yAxis = Seq(
      YAxis(
        title = AxisTitle(text = "Wind speed (m/s)"),
        minorGridLineWidth = 0.0,
        gridLineWidth = 0.0,
        plotBands = Seq[(Double, Double, String)](
          (0.3, 1.5, "Light air"),
          (1.5, 3.3, "Light breeze"),
          (3.3, 5.5, "Gentle breeze"),
          (5.5, 8, "Moderate breeze"),
          (8, 11, "Fresh breeze"),
          (11, 14, "Strong breeze"),
          (14, 15, "High wind")
        ).zipWithIndex.map {
          case ((from, to, text), idx) =>
            YAxisPlotBand(
              from = from, to = to, color = if (idx % 2 == 0) Color(68, 170, 213, 0.1) else Color(0, 0, 0, 0),
              label = AxisPlotLineOrBandLabel(text = text, style = "{color: \"#606060\"}")
            )
        }
      )
    ),
    tooltip = Tooltip(valueSuffix = " m/s"),
    plotOptions = PlotOptions(
      spline = SeriesSpline(
        lineWidth = 4.0,
        states = SeriesAreaStates(SeriesAreaStatesHover(lineWidth = 5.0)),
        marker = SeriesMarker(enabled = false),
        pointInterval = 1000.0 * 60 * 60, //1h
        pointStart = date(2015, 4, 31).getTime.toDouble,
        pointPlacement = PointPlacement.OnTick
      )
    ),
    series = Seq(
      SeriesSpline(
        name = "Hestavollane",
        data = Seq(0.2, 0.8, 0.8, 0.8, 1, 1.3, 1.5, 2.9, 1.9, 2.6, 1.6, 3, 4, 3.6, 4.5, 4.2, 4.5, 4.5, 4, 3.1, 2.7, 4, 2.7, 2.3, 2.3, 4.1, 7.7, 7.1, 5.6, 6.1, 5.8, 8.6, 7.2, 9, 10.9, 11.5, 11.6, 11.1, 12, 12.3, 10.7, 9.4, 9.8, 9.6, 9.8, 9.5, 8.5, 7.4, 7.6)
      ),
      SeriesSpline(
        name = "Vik",
        data = Seq(0, 0, 0.6, 0.9, 0.8, 0.2, 0, 0, 0, 0.1, 0.6, 0.7, 0.8, 0.6, 0.2, 0, 0.1, 0.3, 0.3, 0, 0.1, 0, 0, 0, 0.2, 0.1, 0, 0.3, 0, 0.1, 0.2, 0.1, 0.3, 0.3, 0, 3.1, 3.1, 2.5, 1.5, 1.9, 2.1, 1, 2.3, 1.9, 1.2, 0.7, 1.3, 0.4, 0.3)
      )
    )
  )
}
