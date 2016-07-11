package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom

class MultiSelectDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

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

  override def getTemplate: Element = div(id := "multi-select-demo", GuideStyles.frame)(
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
  ).render

  def checkboxes() = div(BootstrapStyles.Form.inputGroup, GuideStyles.blockOnMobile)(
    div(BootstrapStyles.Form.inputGroupAddon, GuideStyles.blockOnMobile)("Fruits:"),
    div(BootstrapStyles.Form.inputGroupAddon)(
      Select(
        favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString),
        BootstrapStyles.Form.formControl
      )
    ),
    div(BootstrapStyles.Form.inputGroupAddon, GuideStyles.blockOnMobile)(span(cls := "multi-select-demo-fruits")(bind(favoriteFruits)))
  )
}
