package io.udash.web.guide.views.ext

import com.avsystem.commons._
import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.charts._
import io.udash.web.guide.views.{References, Versions}
import io.udash.wrappers.highcharts.config.HighchartsConfig
import org.scalajs.dom.Element
import scalatags.JsDom

case object ChartsExtViewFactory extends StaticViewFactory[ChartsExtState.type](() => new ChartsExtView)


class ChartsExtView extends FinalView {
  import Context._
  import JsDom.all._
  import io.udash.wrappers.highcharts.HighchartsUtils._
  import io.udash.wrappers.jquery._

  import scala.concurrent.duration.DurationInt
  import scala.scalajs.js.timers._

  val charts: Seq[(String, HighchartsConfig)] = Seq(
    ("Basic line", BasicLine.config),
//    ("With data labels", LineWithDataLabels.config),
    ("Time series, zoomable", ZoomableTimeSeries.config),
//    ("Spline with symbols", SplineWithSymbols.config),
    ("Spline with plot bands", SplineWithPlotBands.config),
    ("Logarithmic axis", LogarithmicAxis.config),
    ("Area with missing points", AreaWithMissingPoints.config),
    ("Bar with negative stack", BarWithNegativeStacks.config),
//    ("Column with drilldown", ColumnWithDrilldown.config),
    ("Column range", ColumnRange.config),
    ("Pie with drilldown", PieWithDrilldown.config),
//    ("Scatter plot", Scatter.config),
    ("Multiple axes", MultipleAxes.config),
//    ("Heat Map", HeatMap.config),
    ("Spiderweb", Spiderweb.config)
  )

  private def chartContainer(): Element =
    div().render

  override def getTemplate: Modifier = div(
    h1("Udash Charts"),
    p("To start development with the Highcharts wrapper add the following line in you frontend module dependencies: "),
    CodeBlock(
      s""""io.udash" %%% "udash-charts" % "${Versions.udashVersion}"""".stripMargin
    )(GuideStyles),
    p("You have to add also JavaScript dependencies manually, for example: "),
    CodeBlock(
      s"""val highchartsVarsion = ${Versions.highchartsVersion}
         |
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/highcharts.src.js" minified s"${"$highchartsVarsion"}/highcharts.js" dependsOn "jquery.js",
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/highcharts-3d.src.js" minified s"${"$highchartsVarsion"}/highcharts-3d.js" dependsOn s"${"$highchartsVarsion"}/highcharts.src.js",
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/highcharts-more.src.js" minified s"${"$highchartsVarsion"}/highcharts-more.js" dependsOn s"${"$highchartsVarsion"}/highcharts.src.js",
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/modules/exporting.src.js" minified s"${"$highchartsVarsion"}/modules/exporting.js" dependsOn s"${"$highchartsVarsion"}/highcharts.src.js",
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/modules/drilldown.src.js" minified s"${"$highchartsVarsion"}/modules/drilldown.js" dependsOn s"${"$highchartsVarsion"}/highcharts.src.js",
         |"org.webjars" % "highcharts" % highchartsVarsion / s"${"$highchartsVarsion"}/modules/heatmap.src.js" minified s"${"$highchartsVarsion"}/modules/heatmap.js" dependsOn s"${"$highchartsVarsion"}/highcharts.src.js"""".stripMargin
    )(GuideStyles),
    p("The wrapper provides a typed equivalent of the ", a(href := References.HighchartsHomepage)("Highcharts"), " API."),
    p(
      "Remember that ", i("Highcharts"), " library is free for non-commercial use. Take a look at ",
      a(href := References.HighchartsLicense)("licenses page"), "."
    ),
    h2("Examples"),
    p(
      "You can find these and more demos on ",
      a(href := "https://github.com/UdashFramework/udash-guide/tree/master/guide/src/main/scala/io/udash/web/guide/views/ext/demo/charts")("GitHub"), "."
    ),
    charts.map { case (name, config) =>
      // For some reason with new highcharts, they initialize before browser parses styles for their containers...
      // This hack is to delay highcharts initialization until container widths are calculated
      div()(h3(name), chartContainer().setup(chartContainer =>
        setTimeout(0.milliseconds) {
          jQ(chartContainer).highcharts(config)
        }
      ))
    },
    h2("What's next?"),
    p("You can check the ", a(href := BootstrapExtState.url)("Bootstrap Components"),
      " or ", a(href := JQueryExtState.url)("jQuery"),
      " wrapper.")
  )
}