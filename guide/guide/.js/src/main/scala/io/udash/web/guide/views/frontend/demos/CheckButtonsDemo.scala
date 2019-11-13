package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object CheckButtonsDemo extends AutoDemo {

  private val ((firstCheckButtons, secondCheckButtons), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.html.Input
    import scalatags.JsDom.all._

    sealed trait Fruit
    case object Apple extends Fruit
    case object Orange extends Fruit
    case object Banana extends Fruit

    val favoriteFruits = SeqProperty(Apple, Banana)
    val favoriteFruitsStrings = favoriteFruits.transformElements(
      _.toString,
      (s: String) => s match {
        case "Apple" => Apple
        case "Orange" => Orange
        case "Banana" => Banana
      }
    )

    def checkButtons: UdashInputGroup = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.appendCheckbox(
        CheckButtons(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(els => span(els.map {
          case (i: Input, l: String) => label(Form.checkInline, attr("data-label") := l)(i, l)
        }).render)
      ),
      UdashInputGroup.appendText(span(cls := "check-buttons-demo-fruits")(bind(favoriteFruits)))
    )

    checkButtons.render

    (checkButtons, checkButtons)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (div(id := "check-buttons-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        div(Grid.row)(div(firstCheckButtons)),
        div(Grid.row)(div(secondCheckButtons))
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
