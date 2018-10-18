package io.udash.selenium.views

import io.udash._
import io.udash.css.CssView
import io.udash.selenium.routing.FrontendDemosState
import io.udash.selenium.views.demos.frontend._
import scalatags.JsDom.all._

object FrontendDemosViewFactory extends StaticViewFactory[FrontendDemosState.type](() => new FrontendDemosView)

class FrontendDemosView extends FinalView with CssView {
  private val content = div(
    h3("Frontend demos"),
    new BindAttributeDemoComponent().getTemplate, hr,
    new BindDemoComponent().getTemplate, hr,
    new BindValidationDemoComponent().getTemplate, hr,
    new CheckboxDemoComponent().getTemplate, hr,
    new CheckButtonsDemoComponent().getTemplate, hr,
    new FileInputDemoComponent().getTemplate, hr,
    new IntroFormDemoComponent().getTemplate, hr,
    new MultiSelectDemoComponent().getTemplate, hr,
    new ProduceDemoComponent().getTemplate, hr,
    new RadioButtonsDemoComponent().getTemplate, hr,
    new RepeatDemoComponent().getTemplate, hr,
    new SelectDemoComponent().getTemplate, hr,
    new ShowIfDemoComponent().getTemplate, hr,
    new TextAreaDemoComponent().getTemplate, hr,
    new TextInputDemoComponent().getTemplate, hr
  )

  override def getTemplate: Modifier = content
}