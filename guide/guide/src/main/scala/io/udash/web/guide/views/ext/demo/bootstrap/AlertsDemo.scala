package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.alert.{DismissibleUdashAlert, UdashAlert}
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import scalatags.JsDom

import scala.util.Random

object AlertsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val dismissed = SeqProperty[String](Seq.empty)

    def randomDismissible(): dom.Element = {
      val title = Random.nextLong().toString
      val alert = DismissibleUdashAlert(
        alertStyle = BootstrapStyles.Color.values(
          Random.nextInt(BootstrapStyles.Color.values.size)
        ).toProperty
      )(title)
      alert.dismissed.listen(_ => dismissed.append(title))
      alert.render
    }

    val alerts = div(GlobalStyles.centerBlock)(
      UdashAlert(BootstrapStyles.Color.Info.toProperty)("info"),
      UdashAlert(BootstrapStyles.Color.Success.toProperty)("success"),
      UdashAlert(BootstrapStyles.Color.Warning.toProperty)("warning"),
      UdashAlert(BootstrapStyles.Color.Danger.toProperty)("danger")
    ).render

    val create = UdashButton()("Create dismissible alert")
    create.listen { case _ =>
      alerts.appendChild(randomDismissible())
    }

    div(
      alerts,
      create,
      div(BootstrapStyles.Spacing.margin(
        side = Side.Top, size = SpacingSize.Normal
      ))(
        h4("Dismissed: "),
        div(wellStyles)(produce(dismissed)(seq =>
          ul(seq.map(click => li(click))).render
        ))
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

