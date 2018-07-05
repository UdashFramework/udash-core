package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import scalatags.JsDom

class CheckboxDemoComponent extends CssView {
  import JsDom.all._

  val propA: Property[Boolean] = Property(true)
  val propB: Property[Boolean] = Property(false)
  val propC: Property[String] = Property("Yes")
  val propCAsBoolean = propC.transform(
    (s: String) => s.equalsIgnoreCase("yes"),
    (b: Boolean) => if (b) "Yes" else "No"
  )

  def getTemplate: Modifier = div(id := "checkbox-demo")(
    form(BootstrapStyles.containerFluid)(
      inputs(), br, inputs()
    )
  )

  private def inputs = div(BootstrapStyles.row)(
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property A:"),
        UdashInputGroup.addon(Checkbox(propA)(cls := "checkbox-demo-a").render),
        UdashInputGroup.addon(bind(propA))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property B:"),
        UdashInputGroup.addon(Checkbox(propB)(cls := "checkbox-demo-b").render),
        UdashInputGroup.addon(bind(propB))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property C:"),
        UdashInputGroup.addon(Checkbox(propCAsBoolean)(cls := "checkbox-demo-c").render),
        UdashInputGroup.addon(bind(propC))
      ).render
    )
  )
}
