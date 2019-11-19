package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object TooltipsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles._
    import io.udash.bootstrap.badge.UdashBadge
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.tooltip.UdashTooltip
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    import scala.concurrent.duration.DurationInt

    val tooltipContainerId = ComponentId("tooltip-container")
    val label1 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip on hover with delay",
      Spacing.margin(size = SpacingSize.Small)
    )).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Hover),
      delay = UdashTooltip.Delay(500.millis, 250.millis),
      title = "Tooltip...",
      container = Some(s"#$tooltipContainerId")
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip on click",
      Spacing.margin(size = SpacingSize.Small)
    )).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Click),
      delay = UdashTooltip.Delay(0.millis, 250.millis),
      placement = UdashTooltip.Placement.Bottom,
      title = "Tooltip 2...",
      container = Some(s"#$tooltipContainerId")
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier](
      "Tooltip with JS toggler",
      Spacing.margin(size = SpacingSize.Small)
    )).render
    val label3Tooltip = UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Manual),
      placement = UdashTooltip.Placement.Right,
      title = "Tooltip 3...",
      container = Some(s"#$tooltipContainerId")
    )(label3)

    val button = UdashButton()("Toggle tooltip")
    button.listen { case _ => label3Tooltip.toggle() }

    div(id := tooltipContainerId)(
      label1, label2, label3, button
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

