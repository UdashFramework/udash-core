package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import org.scalajs.dom.Element
import scalatags.JsDom

class MultiSelectDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  private val favoriteFruits: SeqProperty[Fruit] = SeqProperty[Fruit](Apple, Banana)

  def getTemplate: Modifier = div(id := "multi-select-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory =>
      Seq(selector(factory), selector(factory))
    }
  )

  def selector(factory: UdashForm#FormElementsFactory): Element =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(12))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Fruits:"),
          UdashInputGroup.select(
            factory.input.multiSelect(
              favoriteFruits, Seq[Fruit](Apple, Orange, Banana).toSeqProperty
            )(itemLabel = item => item.toString).render
          ),
          UdashInputGroup.appendText(span(cls := "multi-select-demo-fruits")(bind(favoriteFruits)))
        )
      )
    ).render
}
