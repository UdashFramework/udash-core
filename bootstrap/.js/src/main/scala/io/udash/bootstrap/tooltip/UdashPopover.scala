package io.udash.bootstrap
package tooltip

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
    * Check <a href="https://getbootstrap.com/docs/3.3/javascript/#popovers-methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit =
    selector.popover("destroy")

  private[tooltip] def reloadContent(): Unit =
    selector.popover("setContent")

  selector.on("show.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent.ShowEvent(this)))
  selector.on("shown.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent.ShownEvent(this)))
  selector.on("hide.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent.HideEvent(this)))
  selector.on("hidden.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent.HiddenEvent(this)))
  selector.on("inserted.bs.popover", (_: Element, _: JQueryEvent) => fire(TooltipEvent.InsertedEvent(this)))
}

object UdashPopover extends TooltipUtils[UdashPopover] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashPopover = {
    val tp: UdashPopoverJQuery = jQ(el).asInstanceOf[UdashPopoverJQuery]
    tp.popover(options)
    new UdashPopover(tp)
  }

  override protected val defaultPlacement: (dom.Node, dom.Node) => Seq[Placement] = (_, _) => Seq(RightPlacement)
  override protected val defaultTemplate: String = {
    import io.udash.css.CssView._
    import scalatags.Text.all._
    div(BootstrapStyles.Popover.popover, role := "tooltip")(
      div(BootstrapStyles.arrow),
      h3(BootstrapStyles.Popover.popoverTitle),
      div(BootstrapStyles.Popover.popoverContent)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(ClickTrigger)

  @js.native
  private trait UdashPopoverJQuery extends JQuery {
    def popover(arg: js.Any): UdashPopoverJQuery = js.native
  }
}
