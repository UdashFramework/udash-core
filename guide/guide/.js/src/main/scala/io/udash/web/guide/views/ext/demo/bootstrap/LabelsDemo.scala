package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object LabelsDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.badge.UdashBadge
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    div(
      UdashBadge(badgeStyle = Color.Primary.toProperty)(_ => "Primary"), " ",
      UdashBadge(badgeStyle = Color.Secondary.toProperty, pillStyle = true.toProperty)(_ => "Secondary Pill"), " ",
      UdashBadge.link(link = Property("https://udash.io/"), badgeStyle = Color.Success.toProperty)(_ => "Success Link"), " ",
      UdashBadge(badgeStyle = Color.Danger.toProperty)(_ => "Danger"), " ",
      UdashBadge(badgeStyle = Color.Warning.toProperty)(_ => "Warning"), " ",
      UdashBadge(badgeStyle = Color.Info.toProperty)(_ => "Info"), " ",
      UdashBadge(badgeStyle = Color.Light.toProperty)(_ => "Light"), " ",
      UdashBadge(badgeStyle = Color.Dark.toProperty)(_ => "Dark"), " ",
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

