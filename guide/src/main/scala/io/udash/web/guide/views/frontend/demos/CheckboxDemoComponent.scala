package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom

class CheckboxDemoComponent extends Component {
  import JsDom.all._

  val propA: Property[Boolean] = Property(true)
  val propB: Property[Boolean] = Property(false)
  val propC: Property[String] = Property("Yes")
  val propCAsBoolean = propC.transform(
    (s: String) => s.equalsIgnoreCase("yes"),
    (b: Boolean) => if (b) "Yes" else "No"
  )

  override def getTemplate: Modifier = div(id := "checkbox-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
    form(BootstrapStyles.containerFluid)(
      inputs(), br, inputs()
    )
  )

  private def inputs = div(BootstrapStyles.row)(
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property A:"),
        UdashInputGroup.addon(Checkbox(propA, cls := "checkbox-demo-a").render),
        UdashInputGroup.addon(bind(propA))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property B:"),
        UdashInputGroup.addon(Checkbox(propB, cls := "checkbox-demo-b").render),
        UdashInputGroup.addon(bind(propB))
      ).render
    ),
    div(BootstrapStyles.Grid.colMd4)(
      UdashInputGroup()(
        UdashInputGroup.addon("Property C:"),
        UdashInputGroup.addon(Checkbox(propCAsBoolean, cls := "checkbox-demo-c").render),
        UdashInputGroup.addon(bind(propC))
      ).render
    )
  )
}
