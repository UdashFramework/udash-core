package io.udash.selenium.views

import io.udash.{FinalView, StaticViewFactory}
import io.udash.css.CssView
import io.udash.selenium.routing.RestDemosState
import io.udash.selenium.views.demos.rest._
import scalatags.JsDom.all._

object RestDemosViewFactory extends StaticViewFactory[RestDemosState.type](() => new RestDemosView)

class RestDemosView extends FinalView with CssView {
  private val content = div(
    h3("REST demos"),
    new EchoRestDemoComponent().getTemplate, hr,
    new SimpleRestDemoComponent().getTemplate, hr
  )

  override def getTemplate: Modifier = content
}