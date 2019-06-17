package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object ResponsiveEmbedDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    div(
      div(
        BootstrapStyles.EmbedResponsive.responsive,
        BootstrapStyles.EmbedResponsive.embed16by9,
        GlobalStyles.smallMargin
      )(
        iframe(
          BootstrapStyles.EmbedResponsive.item,
          src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0"
        )
      ),
      div(
        BootstrapStyles.EmbedResponsive.responsive,
        BootstrapStyles.EmbedResponsive.embed4by3,
        GlobalStyles.smallMargin
      )(
        iframe(
          BootstrapStyles.EmbedResponsive.item,
          src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0"
        )
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

