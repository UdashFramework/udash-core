package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Color, Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object ToggleButtonsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val buttons = Color.values.map { color =>
      color.name -> {
        val active = Property(false)
        val btn = UdashButton.toggle(
          active,
          color.toProperty[Color]
        )(_ => Seq[Modifier](
          color.name,
          GlobalStyles.smallMargin
        ))
        (active, btn)
      }
    }

    div(
      div(
        GlobalStyles.centerBlock,
        BootstrapStyles.Spacing.margin(
          side = Side.Bottom,
          size = SpacingSize.Normal
        )
      )(
        buttons.map { case (_, (_, btn)) => btn.render }
      ),
      h4("Is active: "),
      div(wellStyles)(
        buttons.map({ case (name, (active, _)) =>
          span(s"$name: ", bind(active), br)
        }).toSeq
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

