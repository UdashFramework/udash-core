package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import scalatags.JsDom

object CheckboxDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val ((firstCheckboxes, secondCheckboxes), source) = {
    val propA: Property[Boolean] = Property(true)
    val propB: Property[Boolean] = Property(false)
    val propC: Property[String] = Property("Yes")
    val propCAsBoolean = propC.transform(
      (s: String) => s.equalsIgnoreCase("yes"),
      (b: Boolean) => if (b) "Yes" else "No"
    )

    def inputs() = div(BootstrapStyles.Grid.row)(
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property A:"),
          UdashInputGroup.appendCheckbox(Checkbox(propA)(cls := "checkbox-demo-a").render),
          UdashInputGroup.appendText(bind(propA))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property B:"),
          UdashInputGroup.appendCheckbox(Checkbox(propB)(cls := "checkbox-demo-b").render),
          UdashInputGroup.appendText(bind(propB))
        ).render
      ),
      div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Property C:"),
          UdashInputGroup.appendCheckbox(Checkbox(propCAsBoolean)(cls := "checkbox-demo-c").render),
          UdashInputGroup.appendText(bind(propC))
        ).render
      )
    )

    (inputs(), inputs())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "checkbox-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        firstCheckboxes, br, secondCheckboxes
      )
    ), source.lines.slice(1, source.lines.size - 3))
  }
}
