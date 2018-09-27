package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import org.scalajs.dom.Element
import scalatags.JsDom

class SelectDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  val favoriteFruit: Property[Fruit] = Property[Fruit](Apple)

  def getTemplate: Modifier = div(id := "select-demo")(
    UdashForm() { factory =>
      Seq(radios(factory), radios(factory))
    }
  )

  def radios(factory: UdashForm#FormElementsFactory): Element =
    div(BootstrapStyles.Grid.row, BootstrapStyles.Spacing.margin())(
      div(BootstrapStyles.Grid.col(12))(
        UdashInputGroup()(
          UdashInputGroup.prependText("Fruits:"),
          UdashInputGroup.select(
            factory.input.select(
              favoriteFruit, Seq[Fruit](Apple, Orange, Banana).toSeqProperty
            )(itemLabel = item => item.toString).render
          ),
          UdashInputGroup.appendText(span(cls := "select-demo-fruits")(bind(favoriteFruit)))
        )
      )
    ).render
}
