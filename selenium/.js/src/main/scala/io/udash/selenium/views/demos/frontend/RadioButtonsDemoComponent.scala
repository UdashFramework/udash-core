package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import org.scalajs.dom.Element
import scalatags.JsDom

class RadioButtonsDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  private val favoriteFruit: Property[Fruit] = Property[Fruit](Apple)

  def getTemplate: Modifier = div(id := "radio-buttons-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory =>
      Seq(radios(factory), radios(factory))
    }
  )

  def radios(factory: UdashForm#FormElementsFactory): Element =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(12))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Fruits:"),
          UdashInputGroup.appendCheckbox(
            factory.input.radioButtons(
              favoriteFruit, Seq[Fruit](Apple, Orange, Banana).toSeqProperty, inline = true.toProperty
            )(labelContent = (item, _, _) => Some(Seq[Modifier](item.toString, id := s"radio-label-${item.toString}")))()
          ),
          UdashInputGroup.appendText(span(cls := "radio-buttons-demo-fruits")(bind(favoriteFruit)))
        )
      )
    ).render
}
