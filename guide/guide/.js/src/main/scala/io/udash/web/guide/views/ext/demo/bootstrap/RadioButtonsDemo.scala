package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object RadioButtonsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    val options = SeqProperty("Radio 1", "Radio 2", "Radio 3")
    val selected = Property(options.get.head)

    div(
      div(
        Spacing.margin(
          side = Side.Bottom,
          size = SpacingSize.Normal
        )
      )(UdashButtonGroup.radio(selected, options)()),
      h4("Is active: "),
      div(Card.card, Card.body, Background.color(Color.Light))(
        repeatWithNested(options) { (option, nested) =>
          val checked = selected.transform(_ == option.get)
          div(
            nested(bind(option)), ": ",
            nested(bind(checked))
          ).render
        }
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

