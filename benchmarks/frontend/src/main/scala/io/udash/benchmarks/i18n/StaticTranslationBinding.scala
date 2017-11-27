package io.udash.benchmarks.i18n

import io.udash._
import io.udash.i18n._

import scalatags.JsDom.all._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

import scala.concurrent.Future

object StaticTranslationBinding {

  val instantSuccessTranslations = Benchmark("instant success translation") {
    div(
      (1 to 50).map { _ =>
        span(
          translatedAttr(Future.successful(Translated("Test")), "data-test"),
          translated(Future.successful(Translated("Test")))
        ).render
      }
    ).render
  }

  val futureTranslations = Benchmark("future translation") {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    div(
      (1 to 50).map { _ =>
        span(
          translatedAttr(Future(Translated("Test")), "data-test"),
          translated(Future(Translated("Test")))
        ).render
      }
    ).render
  }

  val suite = GuiSuite(
    Suite("StaticTranslations")(
      instantSuccessTranslations,
      futureTranslations
    )
  )
}
