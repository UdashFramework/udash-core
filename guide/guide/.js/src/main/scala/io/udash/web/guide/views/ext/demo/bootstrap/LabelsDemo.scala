package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.badge.UdashBadge
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object LabelsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    div(
      UdashBadge(badgeStyle = BootstrapStyles.Color.Primary.toProperty)(_ => "Primary"), " ",
      UdashBadge(BootstrapStyles.Color.Secondary.toProperty, pillStyle = true.toProperty)(_ => "Secondary Pill"), " ",
      UdashBadge.link(Property("https://udash.io/"), BootstrapStyles.Color.Success.toProperty)(_ => "Success Link"), " ",
      UdashBadge(BootstrapStyles.Color.Danger.toProperty)(_ => "Danger"), " ",
      UdashBadge(BootstrapStyles.Color.Warning.toProperty)(_ => "Warning"), " ",
      UdashBadge(BootstrapStyles.Color.Info.toProperty)(_ => "Info"), " ",
      UdashBadge(BootstrapStyles.Color.Light.toProperty)(_ => "Light"), " ",
      UdashBadge(BootstrapStyles.Color.Dark.toProperty)(_ => "Dark"), " ",
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

