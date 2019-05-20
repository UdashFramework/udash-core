package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import scalatags.JsDom

object MultiSelectDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val ((firstMultiSelect, secondMultiSelect), source) = {
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

    def multiSelect() = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.select(
        Select(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(Select.defaultLabel, BootstrapStyles.Form.control).render
      ),
      UdashInputGroup.appendText(span(cls := "multi-select-demo-fruits")(bind(favoriteFruits)))
    )

    (multiSelect(), multiSelect())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "multi-select-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.Grid.row)(firstMultiSelect),
        div(BootstrapStyles.Grid.row)(secondMultiSelect)
      )
    ), source.lines.slice(1, source.lines.size - 3))
  }
}
