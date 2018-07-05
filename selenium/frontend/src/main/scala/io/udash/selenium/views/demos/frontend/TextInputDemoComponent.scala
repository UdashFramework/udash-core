package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import scalatags.JsDom

class TextInputDemoComponent extends CssView {
  import JsDom.all._

  val name: Property[String] = Property("")
  val password: Property[String] = Property("")
  val age: Property[Int] = Property(1)

  def getTemplate: Modifier = div(id := "inputs-demo")(
    form(BootstrapStyles.containerFluid)(
      inputs(), br, inputs()
    )
  )

  private def inputs() = div(BootstrapStyles.row)(
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.input(
          TextInput(name)(placeholder := "Input your name...", maxlength := "6").render
        ),
        UdashInputGroup.addon(span(bind(name)))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.input(
          PasswordInput(password)(placeholder := "Input your password...", maxlength := "6").render
        ),
        UdashInputGroup.addon(span(bind(password)))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.input(
          NumberInput(age.transform(_.toString, Integer.parseInt))(maxlength := "6").render
        ),
        UdashInputGroup.addon(span(bind(age)))
      ).render
    )
  )
}
