package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash._

import scalatags.JsDom.all._


object DateTimeLocalDemo extends AutoDemo {

  private val dateTimeProperty = Property("")

  private val ((firstInput, secondInput), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._
    import org.scalajs.dom.html.Div
    import scalatags.JsDom

    def input: JsDom.TypedTag[Div] = div(Grid.row)(
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.input(
            DateTimeLocalInput(dateTimeProperty)().render
          ),
        ).render
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        produce(dateTimeProperty) { date => span(s"Selected date: $date": Modifier).render }
      )
    )

    input.render

    (input, input)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (div(id := "datetimelocal-input-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        firstInput, br, secondInput
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
