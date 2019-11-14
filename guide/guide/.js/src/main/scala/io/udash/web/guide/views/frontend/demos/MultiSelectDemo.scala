package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object MultiSelectDemo extends AutoDemo {

  private val ((firstMultiSelect, secondMultiSelect), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    sealed trait Fruit
    case object Apple extends Fruit
    case object Orange extends Fruit
    case object Banana extends Fruit

    val favoriteFruits = SeqProperty(Apple, Banana)
    val favoriteFruitsStrings = favoriteFruits.transform(
      (f: Fruit) => f.toString,
      (s: String) => s match {
        case "Apple" => Apple
        case "Orange" => Orange
        case "Banana" => Banana
      }
    )

    def multiSelect: UdashInputGroup = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.select(
        Select(
          favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(Select.defaultLabel, Form.control).render
      ),
      UdashInputGroup.appendText(span(cls := "multi-select-demo-fruits")(bind(favoriteFruits)))
    )

    multiSelect.render

    (multiSelect, multiSelect)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._

    (div(id := "multi-select-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        div(Grid.row)(firstMultiSelect),
        div(Grid.row)(secondMultiSelect)
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
