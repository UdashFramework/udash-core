package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.UdashForm
import io.udash.bootstrap.utils.ComponentId
import io.udash.css.CssView
import io.udash.selenium.views.demos.UrlLoggingDemoService
import scalatags.JsDom

class RoutingLoggerDemoComponent extends CssView {
  import JsDom.all._

  def getTemplate: Modifier = div(id := "routing-logger-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory =>
      factory.input.checkbox(UrlLoggingDemoService.enabled, inline = true.toProperty, inputId = ComponentId("turn-on-logger"))(
        labelContent = _ => Some("Turn on frontend routing logger")
      )
    },
    ul(id := "routing-history")(
      repeatWithNested(UrlLoggingDemoService.loadHistory) { case (item, nested) =>
        li(nested(bind(item.transform(_._2))), " -> ", nested(bind(item.transform(_._1)))).render
      }
    )
  )
}
