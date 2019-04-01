package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.html.Input

import scalatags.JsDom

class CheckButtonsDemoComponent extends Component {
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

  override def getTemplate: Modifier = div(id := "check-buttons-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.Grid.row)(
        div(checkboxes()),
        br(),
        div(checkboxes())
      )
    )
  )

  def checkboxes() =
    UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.appendCheckbox(
        CheckButtons(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )((els: Seq[(Input, String)]) => span(els.map {
          case (i: Input, l: String) => label(BootstrapStyles.Form.checkInline, attr("data-label") := l)(i, l)
        }).render).render
      ),
      UdashInputGroup.appendText(span(cls := "check-buttons-demo-fruits")(bind(favoriteFruits)))
    ).render
}
