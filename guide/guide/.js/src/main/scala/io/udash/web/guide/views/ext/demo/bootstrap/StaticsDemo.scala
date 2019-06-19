package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object StaticsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    div(Grid.row)(
      div(
        Grid.col(9),
        Card.card, Card.body, Background.color(Color.Light)
      )(
        ".col-xs-9"
      ),
      div(
        Grid.col(4),
        Card.card, Card.body, Background.color(Color.Light)
      )(
        ".col-xs-4", br,
        "Since 9 + 4 = 13 > 12, this 4-column-wide div",
        "gets wrapped onto a new line as one contiguous unit."
      ),
      div(
        Grid.col(6),
        Card.card, Card.body, Background.color(Color.Light)
      )(
        ".col-xs-6", br,
        "Subsequent columns continue along the new line."
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

