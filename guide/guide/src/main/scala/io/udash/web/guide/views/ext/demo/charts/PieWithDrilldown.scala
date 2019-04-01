package io.udash.web.guide.views.ext.demo.charts

import io.udash.wrappers.highcharts.config.HighchartsConfig
import io.udash.wrappers.highcharts.config.axis.{Axis, AxisTitle, XAxis, YAxis}
import io.udash.wrappers.highcharts.config.credits.Credits
import io.udash.wrappers.highcharts.config.drilldown.Drilldown
import io.udash.wrappers.highcharts.config.legend.Legend
import io.udash.wrappers.highcharts.config.series.pie.{SeriesPie, SeriesPieData}
import io.udash.wrappers.highcharts.config.title.Title

object PieWithDrilldown {
  val config = HighchartsConfig(
    credits = Credits(enabled = true, text = "Source from HighCharts", href = "http://www.highcharts.com/demo/pie-drilldown"),
    title = Title(
      text = "Browser market shares. January, 2015 to May, 2015"
    ),
    xAxis = Seq(
      XAxis(`type` = Axis.Type.Category)
    ),
    yAxis = Seq(
      YAxis(title = AxisTitle(text = "Total percent market share"))
    ),
    legend = Legend(enabled = false),
    series = Seq(
      SeriesPie(
        name = "Brands",
        data = Seq(
          SeriesPieData(name = "Microsoft Internet Explorer", y = 56.33, drilldown = "MIE"),
          SeriesPieData(name = "Google Chrome", y = 24.03, drilldown = "Chrome"),
          SeriesPieData(name = "Mozilla Firefox", y = 10.38, drilldown = "Firefox"),
          SeriesPieData(name = "Other", y = 4.77 + 0.91 + 0.2)
        )
      )
    ),
    drilldown = Drilldown(series = Seq(
      SeriesPie(
        id = "MIE",
        name = "Microsoft Internet Explorer",
        data = Seq(
          SeriesPieData(name = "v11.0", y = 24.13),
          SeriesPieData(name = "v8.0", y = 17.2),
          SeriesPieData(name = "v9.0", y = 8.11),
          SeriesPieData(name = "v10.0", y = 5.33)
        )
      ),
      SeriesPie(
        id = "Chrome",
        name = "Google Chrome",
        data = Seq(
          SeriesPieData(name = "v40.0", y = 5.0),
          SeriesPieData(name = "v41.0", y = 4.32),
          SeriesPieData(name = "v42.0", y = 3.68),
          SeriesPieData(name = "v39.0", y = 2.96)
        )
      ),
      SeriesPie(
        id = "Firefox",
        name = "Mozilla Firefox",
        data = Seq(
          SeriesPieData(name = "v35", y = 2.76),
          SeriesPieData(name = "v36", y = 2.32),
          SeriesPieData(name = "v37", y = 2.31),
          SeriesPieData(name = "v34", y = 1.27)
        )
      )
    ))
  )
}
