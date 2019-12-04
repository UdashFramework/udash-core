package io.udash.web.guide.views.frontend.demos

import io.udash.bootstrap.utils.BootstrapTags
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object RadioButtonsDemo extends AutoDemo {

  private val ((firstRadioButtons, secondRadioButtons), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.form.UdashInputGroup._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.html.Input
    import scalatags.JsDom.all._

    sealed trait Fruit
    case object Apple extends Fruit
    case object Orange extends Fruit
    case object Banana extends Fruit

    val favoriteFruit = Property[Fruit](Apple)
    val favoriteFruitString = favoriteFruit.bitransform(_.toString) {
        case "Apple" => Apple
        case "Orange" => Orange
        case "Banana" => Banana
      }

    def radioButtons: UdashInputGroup = UdashInputGroup()(
      prependText("Fruits:"),
      appendRadio(
        RadioButtons(favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty)(
          els => span(els.map {
            case (i: Input, l: String) => label(Form.checkInline, BootstrapTags.dataLabel := l)(i, l)
          }).render
        )
      ),
      appendText(span(cls := "radio-buttons-demo-fruits")(bind(favoriteFruit)))
    )

    radioButtons.render

    (radioButtons, radioButtons)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (div(id := "radio-buttons-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        div(Grid.row)(firstRadioButtons),
        div(Grid.row)(secondRadioButtons)
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
