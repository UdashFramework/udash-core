package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object SelectDemo extends AutoDemo {

  private val ((firstSelect, secondSelect), source) = {
    import io.udash._
    import io.udash.bootstrap.form.UdashInputGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
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

    def select: UdashInputGroup = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.select(
        Select(
          favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(Select.defaultLabel, Form.control).render
      ),
      UdashInputGroup.appendText(span(cls := "select-demo-fruits")(bind(favoriteFruit)))
    )

    select.render

    (select, select)
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    (div(id := "select-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(containerFluid)(
        div(Grid.row)(firstSelect),
        div(Grid.row)(secondSelect)
      )
    ), source.linesIterator.take(source.linesIterator.size - 2))
  }
}
