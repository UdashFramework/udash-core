package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.{BootstrapStyles, BootstrapTags}
import io.udash.css.CssView
import org.scalajs.dom.html.Input
import scalatags.JsDom

class RadioButtonsDemoComponent extends CssView {
  import JsDom.all._

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit

  val favoriteFruit: Property[Fruit] = Property[Fruit](Apple)
  val favoriteFruitString = favoriteFruit.transform(
    (f: Fruit) => f.toString,
    (s: String) => s match {
      case "Apple" => Apple
      case "Orange" => Orange
      case "Banana" => Banana
    }
  )

  def getTemplate: Modifier = div(id := "radio-buttons-demo")(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(
          checkboxes()
        ),
        br(),
        div(
          checkboxes()
        )
      )
    )
  )

  def checkboxes() =
    UdashInputGroup()(
      UdashInputGroup.addon("Fruits:"),
      UdashInputGroup.addon(
        RadioButtons(favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty)(
          (els: Seq[(Input, String)]) => span(els.map {
            case (i: Input, l: String) => label(BootstrapStyles.Form.radioInline, BootstrapTags.dataLabel := l)(i, l)
          }).render
        ).render
      ),
      UdashInputGroup.addon(span(cls := "radio-buttons-demo-fruits")(bind(favoriteFruit)))
    ).render
}
