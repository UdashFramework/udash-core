package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ResponsiveEmbedDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    div(
      div(
        EmbedResponsive.responsive,
        EmbedResponsive.embed16by9,
        Spacing.margin(size = SpacingSize.Small)
      )(
        iframe(
          EmbedResponsive.item,
          src := "https://www.youtube.com/embed/zpOULjyy-n8?rel=0"
        )
      ),
      div(
        EmbedResponsive.responsive,
        EmbedResponsive.embed4by3,
        Spacing.margin(size = SpacingSize.Small)
      )(
        iframe(
          EmbedResponsive.item,
          src := "https://www.youtube.com/embed/zpOULjyy-n8?rel=0"
        )
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

