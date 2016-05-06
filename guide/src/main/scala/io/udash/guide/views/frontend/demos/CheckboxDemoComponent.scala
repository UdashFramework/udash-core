package io.udash.guide.views.frontend.demos

import io.udash._
import io.udash.guide.styles.BootstrapStyles
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom

class CheckboxDemoComponent extends Component {
  import io.udash.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val propA: Property[Boolean] = Property(true)
  val propB: Property[Boolean] = Property(false)
  val propC: Property[String] = Property("Yes")
  val propCAsBoolean = propC.transform(
    (s: String) => if (s.equalsIgnoreCase("yes")) true else false,
    (b: Boolean) => if (b) "Yes" else "No"
  )

  override def getTemplate: Element = div(id := "checkbox-demo", GuideStyles.frame)(
    form(BootstrapStyles.containerFluid)(
      inputs, br, inputs
    )
  ).render

  private val inputs = div(BootstrapStyles.row)(
    div(BootstrapStyles.colMd4)(
      div(BootstrapStyles.inputGroup)(
        div(BootstrapStyles.inputGroupAddon)("Property A:"),
        div(BootstrapStyles.inputGroupAddon)(Checkbox(propA, cls := "checkbox-demo-a")),
        div(BootstrapStyles.inputGroupAddon, "data-bind".attr := "a")(bind(propA))
      )
    ),
    div(BootstrapStyles.colMd4)(
      div(BootstrapStyles.inputGroup)(
        div(BootstrapStyles.inputGroupAddon)("Property B:"),
        div(BootstrapStyles.inputGroupAddon)(Checkbox(propB, cls := "checkbox-demo-b")),
        div(BootstrapStyles.inputGroupAddon, "data-bind".attr := "b")(bind(propB))
      )
    ),
    div(BootstrapStyles.colMd4)(
      div(BootstrapStyles.inputGroup)(
        div(BootstrapStyles.inputGroupAddon)("Property C:"),
        div(BootstrapStyles.inputGroupAddon)(Checkbox(propCAsBoolean, cls := "checkbox-demo-c")),
        div(BootstrapStyles.inputGroupAddon, "data-bind".attr := "c")(bind(propC))
      )
    )
  )
}
