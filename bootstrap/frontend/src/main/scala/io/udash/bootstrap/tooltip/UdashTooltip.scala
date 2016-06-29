package io.udash.bootstrap
package tooltip

import io.udash.wrappers.jquery._
import org.scalajs.dom

import scala.language.postfixOps
import scala.scalajs.js

class UdashTooltip(selector: UdashTooltip.UdashTooltipJQuery) extends Listenable[UdashTooltip, UdashTooltip.TooltipEvent] {
  def show(): Unit =
    selector.tooltip("show")

  def hide(): Unit =
    selector.tooltip("hide")

  def toggle(): Unit =
    selector.tooltip("toggle")

  def destroy(): Unit =
    selector.tooltip("destroy")

  import UdashTooltip._
  selector.on("show.bs.tooltip", jQFire(TooltipShowEvent(this)))
  selector.on("shown.bs.tooltip", jQFire(TooltipShownEvent(this)))
  selector.on("hide.bs.tooltip", jQFire(TooltipHideEvent(this)))
  selector.on("hidden.bs.tooltip", jQFire(TooltipHiddenEvent(this)))
  selector.on("inserted.bs.tooltip", jQFire(TooltipInsertedEvent(this)))
}

object UdashTooltip extends TooltipUtils[UdashTooltip] {
  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashTooltip = {
    val tp: UdashTooltipJQuery = jQ(el).asTooltip()
    tp.tooltip(options)
    new UdashTooltip(tp)
  }

  protected val defaultPlacement: (dom.Element, dom.Element) => Seq[Placement] = (_, _) => Seq(TopPlacement)
  protected val defaultTemplate: String = "<div class=\"tooltip\" role=\"tooltip\"><div class=\"tooltip-arrow\"></div><div class=\"tooltip-inner\"></div></div>"
  protected val defaultTrigger: Seq[Trigger] = Seq(HoverTrigger, FocusTrigger)

  @js.native
  trait UdashTooltipJQuery extends JQuery {
    def tooltip(arg: js.Any): UdashTooltipJQuery = js.native
  }

  implicit class JQueryTooltipExt(jQ: JQuery) {
    def asTooltip(): UdashTooltipJQuery =
      jQ.asInstanceOf[UdashTooltipJQuery]
  }
}
