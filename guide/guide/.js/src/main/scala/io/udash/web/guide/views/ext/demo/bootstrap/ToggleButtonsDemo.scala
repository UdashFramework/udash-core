package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.button.UdashButtonOptions
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ToggleButtonsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    val buttons = Color.values.map { color =>
      color.name -> {
        val active = Property(false)
        val btn = UdashButton.toggle(
          active = active,
          options = UdashButtonOptions(
            color.opt
          )
        )(_ => Seq[Modifier](
          color.name,
          Spacing.margin(size = SpacingSize.ExtraSmall)
        ))
        (active, btn)
      }
    }

    div(
      div(
        Display.flex(),
        Flex.justifyContent(FlexContentJustification.Center),
        Spacing.margin(
          side = Side.Bottom,
          size = SpacingSize.Normal
        )
      )(
        buttons.map { case (_, (_, btn)) => btn.render }
      ),
      h4("Is active: "),
      div(Card.card, Card.body, Background.color(Color.Light))(
        buttons.map({ case (name, (active, _)) =>
          span(s"$name: ", bind(active), br)
        }).toSeq
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

