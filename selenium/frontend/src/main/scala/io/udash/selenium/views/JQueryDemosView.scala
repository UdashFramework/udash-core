package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing.JQueryDemosState
import io.udash.selenium.views.demos.jquery._
import scalatags.JsDom.all._

object JQueryDemosViewFactory extends StaticViewFactory[JQueryDemosState.type](() => new JQueryDemosView)

class JQueryDemosView extends FinalView with CssView {
  private val content = div(
    h3("jQuery demos"),
    new JQueryCallbacksDemo().getTemplate, hr,
    new JQueryEventsDemo().getTemplate, hr
  )

  override def getTemplate: Modifier = content
}