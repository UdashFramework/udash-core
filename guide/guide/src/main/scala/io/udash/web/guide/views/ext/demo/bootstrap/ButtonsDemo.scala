package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Color, Side, Size, SpacingSize}
import io.udash.css.{CssStyle, CssView}
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.{produce, _}
import scalatags.JsDom

import scala.util.Random

object ButtonsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val smallBtn = Some(Size.Small).toProperty[Option[Size]]
    val disabledButtons = Property(Set.empty[Int])

    def bottomMargin(): CssStyle = {
      BootstrapStyles.Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      )
    }

    def disabled(idx: Int): ReadableProperty[Boolean] = {
      disabledButtons.transform(_.contains(idx))
    }

    val buttons = Color.values.map(color =>
      UdashButton(
        color.toProperty,
        smallBtn,
        disabled = disabled(color.ordinal)
      )(_ => Seq[Modifier](
        color.name,
        GlobalStyles.smallMargin
      ))
    )

    val clicks = SeqProperty[String](Seq.empty)
    buttons.foreach(_.listen {
      case UdashButton.ButtonClickEvent(source, _) =>
        clicks.append(source.render.textContent)
    })

    val push = UdashButton(
      size = Some(Size.Large).toProperty,
      block = true.toProperty
    )("Disable random buttons!")
    push.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        clicks.set(Seq.empty)

        val disabledCount = Random.nextInt(buttons.size + 1)
        disabledButtons.set(Seq.fill(disabledCount)(
          Random.nextInt(buttons.size)
        ).toSet)
    }

    div(
      div(bottomMargin())(push),
      div(GlobalStyles.centerBlock, bottomMargin())(buttons),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(wellStyles)(seq.map(li(_))).render
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

