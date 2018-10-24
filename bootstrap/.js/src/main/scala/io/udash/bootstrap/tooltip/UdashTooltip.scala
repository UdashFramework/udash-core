package io.udash.bootstrap
package tooltip

import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Element

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

  selector.on("show.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent.ShowEvent(this)))
  selector.on("shown.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent.ShownEvent(this)))
  selector.on("hide.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent.HideEvent(this)))
  selector.on("hidden.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent.HiddenEvent(this)))
  selector.on("inserted.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent.InsertedEvent(this)))
}

object UdashTooltip extends TooltipUtils[UdashTooltip] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashTooltip = {
    val tp: UdashTooltipJQuery = jQ(el).asInstanceOf[UdashTooltipJQuery]
    tp.tooltip(options)
    new UdashTooltip(tp)
  }

  override protected val defaultPlacement: (dom.Node, dom.Node) => Seq[Placement] = (_, _) => Seq(TopPlacement)
  override protected val defaultTemplate: String = {
    import io.udash.css.CssView._
    import scalatags.Text.all._
    div(BootstrapStyles.Tooltip.tooltip, role := "tooltip")(
      div(BootstrapStyles.Tooltip.tooltipArrow),
      div(BootstrapStyles.Tooltip.tooltipInner)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(HoverTrigger, FocusTrigger)

  @js.native
  private trait UdashTooltipJQuery extends JQuery {
    def tooltip(arg: js.Any): UdashTooltipJQuery = js.native
  }
}
