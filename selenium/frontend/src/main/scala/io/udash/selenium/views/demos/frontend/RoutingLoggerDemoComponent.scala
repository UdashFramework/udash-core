package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.views.demos.UrlLoggingDemoService
import scalatags.JsDom

class RoutingLoggerDemoComponent extends CssView {
  import JsDom.all._

  def getTemplate: Modifier = div(id := "routing-logger-demo")(
    span("Turn on logging:"), Checkbox(UrlLoggingDemoService.enabled)(id := "turn-on-logger"),
    div(id := "routing-history")(
      repeatWithNested(UrlLoggingDemoService.loadHistory) { case (item, nested) =>
        span(nested(bind(item.transform(_._2))), " -> ", nested(bind(item.transform(_._1)))).render
      }
    )
  )
}
