package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object TextInputDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val ((firstInputs, secondInputs), source) = {
    val name: Property[String] = Property("")
    val password: Property[String] = Property("")
    val age: Property[Int] = Property(1)

    def inputs() = div(BootstrapStyles.Grid.row)(
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            TextInput(name)(placeholder := "Input your name...", maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(name)))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            PasswordInput(password)(placeholder := "Input your password...", maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(password)))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            NumberInput(age.transform(_.toString, Integer.parseInt))(maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(age)))
        ).render
      )
    )

    (inputs(), inputs())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "inputs-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        firstInputs, br, secondInputs
      )
    ), source.linesIterator.slice(1, source.linesIterator.size - 3))
  }
}
