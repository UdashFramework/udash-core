package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object CheckboxDemo extends AutoDemo {

  private val ((firstCheckboxes, secondCheckboxes), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.form.UdashInputGroup._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.html.Div
    import scalatags.JsDom
    import scalatags.JsDom.all._

    val propA = Property(true)
    val propB = Property(false)
    val propC = Property("Yes")
    val propCAsBoolean = propC.bitransform(_.equalsIgnoreCase("yes"))(if (_) "Yes" else "No")

    def checkboxes: JsDom.TypedTag[Div] = div(Grid.row)(
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          prependText("Property A:"),
          appendCheckbox(Checkbox(propA)(cls := "checkbox-demo-a").render),
          appendText(bind(propA))
        ).render
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          prependText("Property B:"),
          appendCheckbox(Checkbox(propB)(cls := "checkbox-demo-b").render),
          appendText(bind(propB))
        ).render
      ),
      div(Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          prependText("Property C:"),
          appendCheckbox(Checkbox(propCAsBoolean)(cls := "checkbox-demo-c").render),
          appendText(bind(propC))
        ).render
      )
    )

    checkboxes.render
    (checkboxes, checkboxes)
    }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (
      div(
        id := "checkbox-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(
        form(containerFluid)(
          firstCheckboxes, br, secondCheckboxes
        )
      ),
      source.dropFinalLine
    )
  }
}
