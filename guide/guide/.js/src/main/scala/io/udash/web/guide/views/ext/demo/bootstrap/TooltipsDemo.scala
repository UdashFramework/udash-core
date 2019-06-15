package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.badge.UdashBadge
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.tooltip.UdashTooltip
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

import scala.language.postfixOps

object TooltipsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    import scala.concurrent.duration.DurationInt
    val tooltipContainerId = ComponentId("tooltip-container")
    val label1 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip on hover with delay",
      GlobalStyles.smallMargin
    )).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Hover),
      delay = UdashTooltip.Delay(500 millis, 250 millis),
      title = (_) => "Tooltip...",
      container = Option(s"#$tooltipContainerId")
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip on click",
      GlobalStyles.smallMargin
    )).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Click),
      delay = UdashTooltip.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashTooltip.Placement.Bottom),
      title = (_) => "Tooltip 2...",
      container = Option(s"#$tooltipContainerId")
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip with JS toggler",
      GlobalStyles.smallMargin
    )).render
    val label3Tooltip = UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Manual),
      placement = (_, _) => Seq(UdashTooltip.Placement.Right),
      title = (_) => "Tooltip 3...",
      container = Option(s"#$tooltipContainerId")
    )(label3)

    val button = UdashButton()("Toggle tooltip")
    button.listen { case _ => label3Tooltip.toggle() }

    div(id := tooltipContainerId)(
      label1, label2, label3, button
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

