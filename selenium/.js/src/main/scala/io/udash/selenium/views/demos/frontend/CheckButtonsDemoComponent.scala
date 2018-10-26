package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import org.scalajs.dom.html.Input
import scalatags.JsDom

class CheckButtonsDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  val favoriteFruits: SeqProperty[Fruit] = SeqProperty[Fruit](Apple, Banana)
  val favoriteFruitsStrings = favoriteFruits.transform(
    (f: Fruit) => f.toString,
    (s: String) => s match {
      case "Apple" => Apple
      case "Orange" => Orange
      case "Banana" => Banana
    }
  )

  def getTemplate: Modifier = div(id := "check-buttons-demo")(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(checkboxes()),
        br(),
        div(checkboxes())
      )
    )
  )

  def checkboxes() =
    UdashInputGroup()(
      UdashInputGroup.addon("Fruits:"),
      UdashInputGroup.addon(
        CheckButtons(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )((els: Seq[(Input, String)]) => span(els.map {
          case (i: Input, l: String) => label(BootstrapStyles.Form.checkboxInline, attr("data-label") := l)(i, l)
        }).render).render
      ),
      UdashInputGroup.addon(span(cls := "check-buttons-demo-fruits")(bind(favoriteFruits)))
    ).render
}
