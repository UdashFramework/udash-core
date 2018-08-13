package io.udash.bootstrap
package tooltip

import io.udash.wrappers.jquery._
import org.scalajs.dom

import scala.scalajs.js

final class UdashTooltip private(selector: UdashTooltip.UdashTooltipJQuery)
  extends Tooltip[TooltipEvent[UdashTooltip], UdashTooltip] {
  /** Shows the tooltip. */
  def show(): Unit =
    selector.tooltip("show")

  /** Hides the tooltip. */
  def hide(): Unit =
    selector.tooltip("hide")

  /** Toggles tooltip visibility. */
  def toggle(): Unit =
    selector.tooltip("toggle")

  /** Hides and destroys an element's popover.
    * Check <a href="https://getbootstrap.com/docs/3.3/javascript/#popovers-methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit =
    selector.tooltip("destroy")

  private[tooltip] def reloadContent(): Unit =
    selector.tooltip("setContent")

  selector.on("show.bs.tooltip", jQFire(TooltipEvent(this, TooltipEvent.EventType.Show)))
  selector.on("shown.bs.tooltip", jQFire(TooltipEvent(this, TooltipEvent.EventType.Shown)))
  selector.on("hide.bs.tooltip", jQFire(TooltipEvent(this, TooltipEvent.EventType.Hide)))
  selector.on("hidden.bs.tooltip", jQFire(TooltipEvent(this, TooltipEvent.EventType.Hidden)))
  selector.on("inserted.bs.tooltip", jQFire(TooltipEvent(this, TooltipEvent.EventType.Inserted)))
}

object UdashTooltip extends TooltipUtils[UdashTooltip] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashTooltip = {
    val tp: UdashTooltipJQuery = jQ(el).asInstanceOf[UdashTooltipJQuery]
    tp.tooltip(options)
    new UdashTooltip(tp)
  }

  override protected val defaultPlacement: (dom.Node, dom.Node) => Seq[Placement] = (_, _) => Seq(TopPlacement)
  override protected val defaultTemplate: String = {
    import scalatags.Text.all._
    import io.udash.css.CssView._
    div(BootstrapStyles.Tooltip.tooltip, role := "tooltip")(
      div(BootstrapStyles.Tooltip.arrow),
      div(BootstrapStyles.Tooltip.inner)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(HoverTrigger, FocusTrigger)

  @js.native
  private trait UdashTooltipJQuery extends JQuery {
    def tooltip(arg: js.Any): UdashTooltipJQuery = js.native
  }
}
