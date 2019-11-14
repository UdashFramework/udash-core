package io.udash.bootstrap
package tooltip

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js

final class UdashPopover(selector: UdashPopover.UdashPopoverJQuery)
  extends Tooltip[TooltipEvent[UdashPopover], UdashPopover] {

  /** Shows popover. */
  def show(): Unit =
    selector.popover("show")

  /** Hides popover. */
  def hide(): Unit =
    selector.popover("hide")

  /** Toggles popover visibility. */
  def toggle(): Unit =
    selector.popover("toggle")

  /** Hides and destroys an element's popover.
    * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit = {
    off(selector)
    selector.popover("dispose")
  }

  private[tooltip] def reloadContent(): Unit =
    selector.popover("setContent")

  on(selector, "show.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Show)))
  on(selector,"shown.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Shown)))
  on(selector,"hide.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hide)))
  on(selector,"hidden.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hidden)))
  on(selector,"inserted.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Inserted)))
}

object UdashPopover extends TooltipUtils[UdashPopover] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashPopover = {
    val tp: UdashPopoverJQuery = jQ(el).asInstanceOf[UdashPopoverJQuery]
    tp.popover(options)
    new UdashPopover(tp)
  }

  override protected val defaultPlacement = Placement.Right
  override protected val defaultTemplate: String = {
    import scalatags.Text.all._
    div(BootstrapStyles.Popover.popover, role := "tooltip")(
      div(BootstrapStyles.arrow),
      h3(BootstrapStyles.Popover.header),
      div(BootstrapStyles.Popover.body)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(Trigger.Click)

  @js.native
  private trait UdashPopoverJQuery extends JQuery {
    def popover(arg: js.Any): UdashPopoverJQuery = js.native
  }
}
