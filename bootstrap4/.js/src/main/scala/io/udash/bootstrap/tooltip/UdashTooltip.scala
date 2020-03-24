package io.udash.bootstrap
package tooltip

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js

final class UdashTooltip private(element: Element) extends Tooltip {

  /** Shows the tooltip. */
  def show(): Unit =
    element.tooltip("show")

  /** Hides the tooltip. */
  def hide(): Unit =
    element.tooltip("hide")

  /** Toggles tooltip visibility. */
  def toggle(): Unit =
    element.tooltip("toggle")

  /** Hides and destroys an element's popover.
   * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit = {
    off(element)
    element.tooltip("dispose")
  }

  private[tooltip] def reloadContent(): Unit =
    element.tooltip("setContent")

  on(element, "show.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Show)))
  on(element, "shown.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Shown)))
  on(element, "hide.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hide)))
  on(element, "hidden.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hidden)))
  on(element, "inserted.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Inserted)))
}

object UdashTooltip extends TooltipUtils[UdashTooltip] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: Element): UdashTooltip = {
    el.tooltip(options)
    new UdashTooltip(el)
  }

  override protected val defaultPlacement = Placement.Top
  override protected val defaultTemplate: String = {
    import io.udash.css.CssView._
    import scalatags.Text.all._
    div(BootstrapStyles.Tooltip.tooltip, role := "tooltip")(
      div(BootstrapStyles.Tooltip.arrow),
      div(BootstrapStyles.Tooltip.inner)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(Trigger.Hover, Trigger.Focus)

}
