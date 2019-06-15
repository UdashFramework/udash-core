package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.{BootstrapStyles, BootstrapTags}
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.html.Input
import scalatags.JsDom

object RadioButtonsDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val ((firstRadioButtons, secondRadioButtons), source) = {
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

    def radioButtons() = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.appendRadio(
        RadioButtons(favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty)(
          (els: Seq[(Input, String)]) => span(els.map {
            case (i: Input, l: String) => label(BootstrapStyles.Form.checkInline, BootstrapTags.dataLabel := l)(i, l)
          }).render
        ).render
      ),
      UdashInputGroup.appendText(span(cls := "radio-buttons-demo-fruits")(bind(favoriteFruit)))
    )

    (radioButtons(), radioButtons())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "radio-buttons-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.Grid.row)(firstRadioButtons),
        div(BootstrapStyles.Grid.row)(secondRadioButtons)
      )
    ), source.linesIterator.slice(1, source.linesIterator.size - 3))
  }
}
