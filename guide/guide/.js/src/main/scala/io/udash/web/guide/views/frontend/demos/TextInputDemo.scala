package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object TextInputDemo extends AutoDemo {

  private val ((firstInputs, secondInputs), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import org.scalajs.dom.html.Div
    import scalatags.JsDom
    import scalatags.JsDom.all._

    val name = Property("")
    val password = Property("")
    val age = Property(1)

    def inputs: JsDom.TypedTag[Div] = div(Grid.row)(
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            TextInput(name)(placeholder := "Input your name...", maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(name)))
        ).render
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            PasswordInput(password)(placeholder := "Input your password...", maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(password)))
        ).render
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            NumberInput(age.transform(_.toString, _.toInt))(maxlength := "6").render
          ),
          UdashInputGroup.appendText(span(bind(age)))
        ).render
      )
    )

    inputs.render

    (inputs, inputs)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._

    (div(id := "inputs-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        firstInputs, br, secondInputs
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
