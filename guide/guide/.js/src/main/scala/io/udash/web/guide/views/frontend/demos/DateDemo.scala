package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.demos.AutoDemo

import scalatags.JsDom.all._

object DateDemo extends AutoDemo {

  private val ((firstInput, secondInput), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._
    import org.scalajs.dom.html.Div
    import scalatags.JsDom

    val dateProperty = Property.blank[String]

    def input: JsDom.TypedTag[Div] = div(Grid.row)(
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            DateInput(dateProperty)().render
          ),
        )
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        produce(dateProperty) { date => span(s"Selected date: $date": Modifier).render }
      )
    )

    input.render
    (input, input)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (div(id := "date-input-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        firstInput, br, secondInput
      )
    ), source.linesWithSeparators.toList.dropRight(1).mkString)
  }
}
