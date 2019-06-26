package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.css.CssView
import scalatags.JsDom

class CheckboxDemoComponent extends CssView {
  import JsDom.all._

  private val propA: Property[Boolean] = Property(true)
  private val propB: Property[Boolean] = Property(false)
  private val propC: Property[String] = Property("Yes")
  private val propCAsBoolean = propC.transform(
    (s: String) => s.equalsIgnoreCase("yes"),
    (b: Boolean) => if (b) "Yes" else "No"
  )

  def getTemplate: Modifier = div(id := "checkbox-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory =>
      Seq(inputs(factory), inputs(factory))
    }
  )

  private def inputs(factory: UdashForm#FormElementsFactory): Modifier =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property A:"),
          UdashInputGroup.appendCheckbox(
            factory.input.checkbox(propA, inputId = ComponentId("checkbox-demo-a"))()
          ),
          UdashInputGroup.appendText(bind(propA))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property B:"),
          UdashInputGroup.appendCheckbox(
            factory.input.checkbox(propB, inputId = ComponentId("checkbox-demo-b"))()
          ),
          UdashInputGroup.appendText(bind(propB))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property C:"),
          UdashInputGroup.appendCheckbox(
            factory.input.checkbox(propCAsBoolean, inputId = ComponentId("checkbox-demo-c"))()
          ),
          UdashInputGroup.appendText(bind(propC))
        ).render
      )
    )
}
