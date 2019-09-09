package io.udash.benchmarks.css

import io.udash.css.{CssStyleName, CssView}
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._
import scalatags.JsDom.all._

object CssStylesApply extends CssView {

  val styles = Benchmark("three styles") {
    div(
      CssStyleName("style-1"),
      CssStyleName("style-2"),
      CssStyleName("style-3"),
      CssStyleName("style-4"),
      CssStyleName("style-5"),
      CssStyleName("style-6"),
      Seq(CssStyleName("style-12"), CssStyleName("style-13"), CssStyleName("style-14")),
      Seq(CssStyleName("style-22"), CssStyleName("style-23"), CssStyleName("style-24")),
      Seq(CssStyleName("style-32"), CssStyleName("style-33"), CssStyleName("style-34")),
      Seq(CssStyleName("style-42"), CssStyleName("style-43"), CssStyleName("style-44")),
      Seq(CssStyleName("style-52"), CssStyleName("style-53"), CssStyleName("style-54"))
    ).render
  }

  val suite = GuiSuite(
    Suite("CssStyle apply")(
      styles
    )
  )
}
