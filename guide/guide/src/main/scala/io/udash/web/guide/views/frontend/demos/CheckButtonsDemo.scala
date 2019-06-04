package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.html.Input
import scalatags.JsDom

object CheckButtonsDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val ((firstCheckButtons, secondCheckButtons), source) = {
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

    def checkButtons() = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.appendCheckbox(
        CheckButtons(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )((els: Seq[(Input, String)]) => span(els.map {
          case (i: Input, l: String) => label(BootstrapStyles.Form.checkInline, attr("data-label") := l)(i, l)
        }).render).render
      ),
      UdashInputGroup.appendText(span(cls := "check-buttons-demo-fruits")(bind(favoriteFruits)))
    )

    (checkButtons(), checkButtons())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "check-buttons-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.Grid.row)(div(firstCheckButtons)),
        div(BootstrapStyles.Grid.row)(div(secondCheckButtons))
      )
    ), source.lines.slice(1, source.lines.size - 3))
  }
}
