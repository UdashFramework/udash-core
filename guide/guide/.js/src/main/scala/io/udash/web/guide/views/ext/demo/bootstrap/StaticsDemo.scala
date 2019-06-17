package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object StaticsDemo extends AutoDemo with CssView {

  import JsDom.all._
  import io.udash.web.guide.components.BootstrapUtils._

  private val (rendered, source) = {
    import BootstrapStyles._

    div(GuideStyles.frame, Grid.row)(
      div(Grid.col(9), wellStyles,
        BootstrapStyles.Spacing.margin(
          side = Side.Bottom, size = SpacingSize.Normal
        )
      )(
        ".col-xs-9"
      ),
      div(Grid.col(4), wellStyles)(
        ".col-xs-4", br,
        "Since 9 + 4 = 13 > 12, this 4-column-wide div",
        "gets wrapped onto a new line as one contiguous unit."
      ),
      div(Grid.col(6), wellStyles)(
        ".col-xs-6", br,
        "Subsequent columns continue along the new line."
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (rendered, source.linesIterator.drop(1))
  }
}

