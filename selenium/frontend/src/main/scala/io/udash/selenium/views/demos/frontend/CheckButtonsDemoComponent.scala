package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import org.scalajs.dom.Element
import scalatags.JsDom

class CheckButtonsDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  private val favoriteFruits: SeqProperty[Fruit] = SeqProperty[Fruit](Apple, Banana)

  def getTemplate: Modifier = div(id := "check-buttons-demo")(
    UdashForm() { factory =>
      Seq(checkboxes(factory), checkboxes(factory))
    }
  )

  def checkboxes(factory: UdashForm#FormElementsFactory): Element =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(12))(
        UdashInputGroup()(
          UdashInputGroup.appendText("Fruits:"),
          UdashInputGroup.appendCheckbox(
            factory.input.checkButtons(
              favoriteFruits, Seq[Fruit](Apple, Orange, Banana).toSeqProperty, inline = true.toProperty
            )(labelContent = (item, _, _) => Some(Seq[Modifier](item.toString, id := s"check-label-${item.toString}")))
          ),
          UdashInputGroup.appendText(span(cls := "check-buttons-demo-fruits")(bind(favoriteFruits)))
        )
      )
    ).render
}
