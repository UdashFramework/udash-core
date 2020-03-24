package io.udash.bootstrap
package tooltip

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js

final class UdashPopover(element: Element) extends Tooltip {

  /** Shows popover. */
  def show(): Unit =
    element.popover("show")

  /** Hides popover. */
  def hide(): Unit =
    element.popover("hide")

  /** Toggles popover visibility. */
  def toggle(): Unit =
    element.popover("toggle")

  /** Hides and destroys an element's popover.
   * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit = {
    off(element)
    element.popover("dispose")
  }

  private[tooltip] def reloadContent(): Unit =
    element.popover("setContent")

  on(element, "show.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Show)))
  on(element, "shown.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Shown)))
  on(element, "hide.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hide)))
  on(element, "hidden.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hidden)))
  on(element, "inserted.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Inserted)))
}

object UdashPopover extends TooltipUtils[UdashPopover] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: Element): UdashPopover = {
    el.popover(options)
    new UdashPopover(el)
  }

  override protected val defaultPlacement = Placement.Right
  override protected val defaultTemplate: String = {
    import io.udash.css.CssView._
    import scalatags.Text.all._
    div(BootstrapStyles.Popover.popover, role := "tooltip")(
      div(BootstrapStyles.arrow),
      h3(BootstrapStyles.Popover.header),
      div(BootstrapStyles.Popover.body)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(Trigger.Click)
}
