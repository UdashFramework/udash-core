package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing.FrontendRoutingDemosState
import io.udash.selenium.views.demos.frontend._
import scalatags.JsDom.all._

object FrontendRoutingDemosViewFactory extends ViewFactory[FrontendRoutingDemosState] {
  override def create(): (View, Presenter[FrontendRoutingDemosState]) = {
    val url = Property.blank[String]
    (new FrontendRoutingDemosView(url), new FrontendRoutingDemosPresenter(url))
  }
}

class FrontendRoutingDemosPresenter(url: Property[String]) extends Presenter[FrontendRoutingDemosState] {
  import io.udash.selenium.Launcher.applicationInstance

  override def handleState(state: FrontendRoutingDemosState) = {
    url.set(applicationInstance.currentUrl.value)
  }
}

class FrontendRoutingDemosView(url: Property[String]) extends FinalView with CssView {
  private val content = div(
    h3("Frontend Routing demos"),
    new RoutingLinkDemoComponent(url).getTemplate, hr
  )

  override def getTemplate: Modifier = content
}