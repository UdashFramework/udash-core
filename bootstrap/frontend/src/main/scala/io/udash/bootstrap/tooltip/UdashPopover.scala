package io.udash.bootstrap
package tooltip

import io.udash.wrappers.jquery._
import org.scalajs.dom

import scala.language.postfixOps
import scala.scalajs.js

class UdashPopover(selector: UdashPopover.UdashPopoverJQuery) extends Listenable[UdashPopover, UdashPopover.TooltipEvent] {
  def show(): Unit =
    selector.popover("show")

  def hide(): Unit =
    selector.popover("hide")

  def toggle(): Unit =
    selector.popover("toggle")

  def destroy(): Unit =
    selector.popover("destroy")

  import UdashPopover._
  selector.on("show.bs.popover", jQFire(TooltipShowEvent(this)))
  selector.on("shown.bs.popover", jQFire(TooltipShownEvent(this)))
  selector.on("hide.bs.popover", jQFire(TooltipHideEvent(this)))
  selector.on("hidden.bs.popover", jQFire(TooltipHiddenEvent(this)))
  selector.on("inserted.bs.popover", jQFire(TooltipInsertedEvent(this)))
}

object UdashPopover extends TooltipUtils[UdashPopover] {
  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashPopover = {
    val tp: UdashPopoverJQuery = jQ(el).asPopover()
    tp.popover(options)
    new UdashPopover(tp)
  }

  protected val defaultPlacement: (dom.Element, dom.Element) => Seq[Placement] = (_, _) => Seq(RightPlacement)
  protected val defaultTemplate: String = "<div class=\"popover\" role=\"tooltip\"><div class=\"arrow\"></div><h3 class=\"popover-title\"></h3><div class=\"popover-content\"></div></div>"
  protected val defaultTrigger: Seq[Trigger] = Seq(ClickTrigger)

  @js.native
  trait UdashPopoverJQuery extends JQuery {
    def popover(arg: js.Any): UdashPopoverJQuery = js.native
  }

  implicit class JQueryPopoverExt(jQ: JQuery) {
    def asPopover(): UdashPopoverJQuery =
      jQ.asInstanceOf[UdashPopoverJQuery]
  }
}
