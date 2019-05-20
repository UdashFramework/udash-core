package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import org.scalajs.dom.html.{Select => _}
import scalatags.JsDom

object SelectDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val ((firstSelect, secondSelect), source) = {
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

    def select() = UdashInputGroup()(
      UdashInputGroup.prependText("Fruits:"),
      UdashInputGroup.select(
        Select(
          favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString).toSeqProperty
        )(Select.defaultLabel, BootstrapStyles.Form.control).render
      ),
      UdashInputGroup.appendText(span(cls := "select-demo-fruits")(bind(favoriteFruit)))
    )

    (select(), select())
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "select-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.Grid.row)(firstSelect),
        div(BootstrapStyles.Grid.row)(secondSelect)
      )
    ), source.lines.slice(1, source.lines.size - 3))
  }
}
