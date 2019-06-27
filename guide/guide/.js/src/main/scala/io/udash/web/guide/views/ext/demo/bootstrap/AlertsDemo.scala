package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object AlertsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles._
    import io.udash.bootstrap.alert._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.css.CssView._
    import org.scalajs.dom.Element
    import scalatags.JsDom.all._

    import scala.util.Random

    val dismissed = SeqProperty.blank[String]

    def contentCentered: Seq[Modifier] = {
      Seq(Display.flex(), Flex.justifyContent(FlexContentJustification.Center))
    }

    def randomDismissible: Element = {
      val title = Random.nextLong().toString
      val alert = DismissibleUdashAlert(
        alertStyle = Color.values(
          Random.nextInt(Color.values.size)
        ).toProperty
      )(div(title, contentCentered))
      alert.dismissed.listen(_ => dismissed.append(title))
      alert.render
    }

    val alerts = div()(
      UdashAlert(Color.Info.toProperty)(div("info", contentCentered)),
      UdashAlert(Color.Success.toProperty)(div("success", contentCentered)),
      UdashAlert(Color.Warning.toProperty)(div("warning", contentCentered)),
      UdashAlert(Color.Danger.toProperty)(div("danger", contentCentered))
    ).render

    val create = UdashButton()("Create dismissible alert")
    create.listen { case _ =>
      alerts.appendChild(randomDismissible)
    }

    div(
      alerts,
      create,
      div(Spacing.margin(
        side = Side.Top,
        size = SpacingSize.Normal
      ))(
        h4("Dismissed: "),
        div(Card.card, Card.body, Background.color(Color.Light))(
          produce(dismissed)(seq =>
            ul(seq.map(li(_))).render
          )
        )
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

