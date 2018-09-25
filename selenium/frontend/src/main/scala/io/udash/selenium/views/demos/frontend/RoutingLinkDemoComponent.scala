package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.routing.FrontendRoutingDemosState
import org.scalajs.dom
import scalatags.JsDom

import scala.scalajs.js

class RoutingLinkDemoComponent(url: Property[String]) extends CssView {
  import JsDom.all._
  import Launcher.applicationInstance

  def getTemplate: Modifier = div(
    p(
      span("The URL of this page is: "),
      span(id := "url-demo-link")(bind(url)), br(), br(),
      span("Click here to change URL: ")
    ),
    a(id := "url-demo-link-apple", href := s"${FrontendRoutingDemosState(Some("apple")).url}")("Apple"), " | ",
    a(id := "url-demo-link-orange", href := s"${FrontendRoutingDemosState(Some("orange")).url}")("Orange"), " | ",
    a(id := "url-demo-link-chocolate", href := s"${FrontendRoutingDemosState(Some("chocolate")).url}")("Chocolate"), " | ",
    a(id := "url-demo-link-pizza", href := s"${FrontendRoutingDemosState(Some("pizza")).url}")("Pizza"),
    br(), br(),
    input(BootstrapStyles.Form.formControl, id := "url-demo-input", placeholder := "Type anything in this field, it should not disappear on a state change..."),
    input(
      BootstrapStyles.Form.formControl, id := "url-demo-link-input", value := "",
      placeholder := "Type something in this field and look at the URL...", onkeyup :+= ((event: dom.Event) => {
        applicationInstance.goTo(FrontendRoutingDemosState(
          Some(js.Dynamic.global
            .encodeURIComponent(event.target.asInstanceOf[dom.html.Input].value)
            .asInstanceOf[String])
        ))
        true
      })
    ),
    p("This view was created with: ", span(id := "url-demo-link-init")(applicationInstance.currentUrl.value))
  )
}
