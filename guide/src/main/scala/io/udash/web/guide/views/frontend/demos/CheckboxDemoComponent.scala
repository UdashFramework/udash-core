package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.{BootstrapStyles, BootstrapTags}
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom

class CheckboxDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val propA: Property[Boolean] = Property(true)
  val propB: Property[Boolean] = Property(false)
  val propC: Property[String] = Property("Yes")
  val propCAsBoolean = propC.transform(
    (s: String) => s.equalsIgnoreCase("yes"),
    (b: Boolean) => if (b) "Yes" else "No"
  )

  override def getTemplate: Element = div(id := "checkbox-demo", GuideStyles.frame)(
    form(BootstrapStyles.containerFluid)(
      inputs, br, inputs
    )
  ).render

  private val inputs = div(BootstrapStyles.row)(
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Property A:"),
        div(BootstrapStyles.Form.inputGroupAddon)(Checkbox(propA, cls := "checkbox-demo-a")),
        div(BootstrapStyles.Form.inputGroupAddon, BootstrapTags.dataBind := "a")(bind(propA))
      )
    ),
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Property B:"),
        div(BootstrapStyles.Form.inputGroupAddon)(Checkbox(propB, cls := "checkbox-demo-b")),
        div(BootstrapStyles.Form.inputGroupAddon, BootstrapTags.dataBind := "b")(bind(propB))
      )
    ),
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Property C:"),
        div(BootstrapStyles.Form.inputGroupAddon)(Checkbox(propCAsBoolean, cls := "checkbox-demo-c")),
        div(BootstrapStyles.Form.inputGroupAddon, BootstrapTags.dataBind := "c")(bind(propC))
      )
    )
  )
}
