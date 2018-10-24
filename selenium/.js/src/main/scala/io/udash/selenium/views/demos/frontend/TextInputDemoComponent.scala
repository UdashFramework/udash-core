package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import org.scalajs.dom.Element
import scalatags.JsDom

class TextInputDemoComponent extends CssView {
  import JsDom.all._

  val name: Property[String] = Property("")
  val password: Property[String] = Property("")
  val age: Property[Int] = Property(1)

  def getTemplate: Modifier = div(id := "inputs-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory => Seq(
      inputs(factory), inputs(factory)
    )}
  )

  private def inputs(factory: UdashForm#FormElementsFactory): Element =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            factory.input.textInput(name)(Some(_ => Seq(placeholder := "Input your name...", maxlength := "6"))).render
          ),
          UdashInputGroup.appendText(span(bind(name)))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            factory.input.passwordInput(password)(Some(_ => Seq(placeholder := "Input your password...", maxlength := "6"))).render
          ),
          UdashInputGroup.appendText(span(bind(password)))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            factory.input.numberInput(age.transform(_.toString, Integer.parseInt))().render
          ),
          UdashInputGroup.appendText(span(bind(age)))
        ).render
      )
    ).render
}
