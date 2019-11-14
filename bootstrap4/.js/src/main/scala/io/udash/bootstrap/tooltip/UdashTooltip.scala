package io.udash.bootstrap
package tooltip

import io.udash.bootstrap.utils.BootstrapStyles
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
    * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit = {
    off(selector)
    selector.tooltip("dispose")
  }

  private[tooltip] def reloadContent(): Unit =
    selector.tooltip("setContent")

  on(selector,"show.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Show)))
  on(selector,"shown.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Shown)))
  on(selector,"hide.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hide)))
  on(selector,"hidden.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Hidden)))
  on(selector,"inserted.bs.tooltip", (_: Element, _: JQueryEvent) => fire(TooltipEvent(this, TooltipEvent.EventType.Inserted)))
}

object UdashTooltip extends TooltipUtils[UdashTooltip] {
  override protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): UdashTooltip = {
    val tp: UdashTooltipJQuery = jQ(el).asInstanceOf[UdashTooltipJQuery]
    tp.tooltip(options)
    new UdashTooltip(tp)
  }

  override protected val defaultPlacement = Placement.Top
  override protected val defaultTemplate: String = {
    import scalatags.Text.all._
    div(BootstrapStyles.Tooltip.tooltip, role := "tooltip")(
      div(BootstrapStyles.Tooltip.arrow),
      div(BootstrapStyles.Tooltip.inner)
    ).render
  }
  override protected val defaultTrigger: Seq[Trigger] = Seq(Trigger.Hover, Trigger.Focus)

  @js.native
  private trait UdashTooltipJQuery extends JQuery {
    def tooltip(arg: js.Any): UdashTooltipJQuery = js.native
  }
}
