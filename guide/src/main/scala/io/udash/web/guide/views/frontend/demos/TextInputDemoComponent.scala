package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom

class TextInputDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val name: Property[String] = Property("")
  val password: Property[String] = Property("")
  val age: Property[Int] = Property(1)

  override def getTemplate: Element = div(id := "inputs-demo", GuideStyles.frame)(
    form(BootstrapStyles.containerFluid)(
      inputs, br, inputs
    )
  ).render

  private val inputs = div(BootstrapStyles.row)(
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Name:"),
        TextInput.debounced(name)(BootstrapStyles.Form.formControl, placeholder := "Input your name...", maxlength := "6"),
        div(BootstrapStyles.Form.inputGroupAddon)(bind(name))
      )
    ),
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Password:"),
        PasswordInput.debounced(password)(BootstrapStyles.Form.formControl, placeholder := "Input your password...", maxlength := "6"),
        div(BootstrapStyles.Form.inputGroupAddon)(bind(password))
      )
    ),
    div(BootstrapStyles.Grid.colMd4)(
      div(BootstrapStyles.Form.inputGroup)(
        div(BootstrapStyles.Form.inputGroupAddon)("Age:"),
        NumberInput.debounced(age.transform(_.toString, Integer.parseInt), maxlength := "6")(BootstrapStyles.Form.formControl),
        div(BootstrapStyles.Form.inputGroupAddon)(bind(age))
      )
    )
  )
}
