package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import scalatags.JsDom

class MultiSelectDemoComponent extends CssView {
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

  def getTemplate: Modifier = div(id := "multi-select-demo")(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(
          selector()
        ),
        br(),
        div(
          selector()
        )
      )
    )
  )

  def selector() =
    UdashInputGroup()(
      UdashInputGroup.addon("Fruits:"),
      UdashInputGroup.addon(
        Select(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(Select.defaultLabel, BootstrapStyles.Form.formControl).render
      ),
      UdashInputGroup.addon(span(cls := "multi-select-demo-fruits")(bind(favoriteFruits)))
    ).render
}
