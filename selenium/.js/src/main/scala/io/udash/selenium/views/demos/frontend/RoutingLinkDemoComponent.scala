package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.form.UdashForm
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.routing.FrontendRoutingDemosState
import io.udash.utils.URLEncoder
import scalatags.JsDom

class RoutingLinkDemoComponent(url: Property[String]) extends CssView {
  import JsDom.all._
  import Launcher.applicationInstance

  private val urlArg = Property("")
  urlArg.listen { value =>
    applicationInstance.goTo(FrontendRoutingDemosState(
      Some(URLEncoder.encode(value, spaceAsPlus = false))
    ))
  }

  def getTemplate: Modifier = div(
    p(BootstrapStyles.Spacing.margin())(
      span("The URL of this page is: "),
      span(id := "url-demo-link")(bind(url))
    ),
    p(BootstrapStyles.Spacing.margin())(
      "Click here to change URL: ",
      a(id := "url-demo-link-apple", href := s"${FrontendRoutingDemosState(Some("apple")).url}")("Apple"), " | ",
      a(id := "url-demo-link-orange", href := s"${FrontendRoutingDemosState(Some("orange")).url}")("Orange"), " | ",
      a(id := "url-demo-link-chocolate", href := s"${FrontendRoutingDemosState(Some("chocolate")).url}")("Chocolate"), " | ",
      a(id := "url-demo-link-pizza", href := s"${FrontendRoutingDemosState(Some("pizza")).url}")("Pizza")
    ),
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory => Seq[Modifier](
      factory.input.formGroup()(
        input = _ => factory.input.textInput(Property(""), inputId = ComponentId("url-demo-input"))(
          Some(_ => placeholder := "Type anything in this field, it should not disappear on a state change...")
        ).render
      ),
      factory.input.formGroup()(
        input = _ => factory.input.textInput(urlArg, inputId = ComponentId("url-demo-link-input"))(
          Some(_ => placeholder := "Type something in this field and look at the URL...")
        ).render
      )
    )},
    p(BootstrapStyles.Spacing.margin())(
      "This view was created with: ", span(id := "url-demo-link-init")(applicationInstance.currentUrl.value)
    )
  )
}
