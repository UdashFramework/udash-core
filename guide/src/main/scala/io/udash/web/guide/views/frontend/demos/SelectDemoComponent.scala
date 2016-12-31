package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.{BootstrapStyles, BootstrapTags}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Select => _, _}

import scalatags.JsDom
import io.udash.web.commons.views.Component

class SelectDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

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

  override def getTemplate: Modifier = div(id := "select-demo", GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(
          selector()
        ),
        br(),
        div(
          selector()
        )
      )
    )
  )

  def selector() =
    UdashInputGroup()(
      UdashInputGroup.addon("Fruits:"),
      UdashInputGroup.addon(
        Select(
          favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString), Select.defaultLabel
        )(BootstrapStyles.Form.formControl).render
      ),
      UdashInputGroup.addon(span(cls := "select-demo-fruits")(bind(favoriteFruit)))
    ).render
}
