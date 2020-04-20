package io.udash.bootstrap
package tooltip

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.wrappers.jquery._
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.scalajs.js
import scala.scalajs.js.|

trait Tooltip extends Listenable {

  override final type EventType = TooltipEvent

  /** Shows the tooltip. */
  def show(): Unit

  /** Hides the tooltip. */
  def hide(): Unit

  /** Toggles tooltip visibility. */
  def toggle(): Unit

  /** Hides and destroys an element's popover.
    * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit

  private[tooltip] def on(selector: JQuery, event: EventName, callback: JQueryCallback): Unit = {
    selector.on(event, callback)
    jqueryCallbacks.+=((event, callback))
  }

  private[tooltip] def off(selector: JQuery): Unit = {
    jqueryCallbacks.foreach { case (event, callback) =>
      selector.off(event, callback)
    }
    jqueryCallbacks.clear()
  }

  private[tooltip] val jqueryCallbacks: mutable.ArrayBuffer[(EventName, JQueryCallback)] = mutable.ArrayBuffer.empty
  private[tooltip] def reloadContent(): Unit
}

abstract class TooltipUtils[TooltipType <: Tooltip] {
  trait Delay extends js.Object {
    val show: Long
    val hide: Long
  }
  object Delay {
    def apply(show: Duration, hide: Duration): Delay = js.Dynamic.literal(show = show.toMillis, hide = hide.toMillis).asInstanceOf[Delay]
  }

  trait Placement {
    def jsValue: js.Any
  }
  final class StaticPlacement(val value: String) extends Placement {
    override def jsValue: js.Any = value
  }
  object Placement {
    final val Auto = new StaticPlacement("auto")
    final val Top = new StaticPlacement("top")
    final val Bottom = new StaticPlacement("bottom")
    final val Left = new StaticPlacement("left")
    final val Right = new StaticPlacement("right")
    def dynamic(f: (dom.Node, dom.Node) => Placement): Placement = new Placement {
      override def jsValue: js.Function2[dom.Node, dom.Node, Placement] = f
    }
  }

  final class Trigger(val jsValue: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Trigger extends AbstractValueEnumCompanion[Trigger] {
    final val Click: Value = new Trigger("click")
    final val Hover: Value = new Trigger("hover")
    final val Focus: Value = new Trigger("focus")
    final val Manual: Value = new Trigger("manual")
  }

  /**
    * Adds tooltip/popover to provided element.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/tooltips/">Bootstrap Docs (Tooltip)</a>.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/popovers/">Bootstrap Docs (Popover)</a>.
    *
    * @param animation Apply a CSS fade transition to the popover.
    * @param boundary  Keeps the popover within the bounds of this element.
    * @param container Appends the popover to a specific element.
    * @param content   Popover content.
    * @param delay     Show/hide delay.
    * @param html      Treat content and title as HTML.
    * @param offset    Offset of the popover relative to its target.
    * @param placement Tooltip/popover placement.
    * @param template  Tooltip/popover template.
    * @param title     Component title.
    * @param trigger   Triggers to show/hide tooltip.
    * @param el        Node which will own the created tooltip/popover.
    */
  def apply(
    animation: Boolean = true,
    boundary: String | dom.Node = "scrollParent",
    container: Option[String | dom.Node] = None,
    content: js.Function1[dom.Node, String] | dom.Node = io.udash.emptyStringNode(),
    delay: Delay | Long = Delay(0 millis, 0 millis),
    html: Boolean = false,
    offset: Int | String = "0",
    placement: Placement = defaultPlacement,
    template: Option[String] = None,
    title: String | js.Function1[dom.Node, String] | dom.Node = "",
    trigger: Seq[Trigger] = defaultTrigger
  )(el: dom.Node): TooltipType =
    initTooltip(
      js.Dictionary(
        "animation" -> animation,
        "boundary" -> boundary,
        "container" -> container.getOrElse(false),
        "content" -> content,
        "delay" -> delay,
        "html" -> html,
        "offset" -> offset,
        "placement" -> placement.jsValue,
        "template" -> template.getOrElse(defaultTemplate),
        "title" -> title,
        "trigger" -> trigger.map(_.jsValue).mkString(" ")
      )
    )(el)

  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): TooltipType
  protected val defaultPlacement: Placement
  protected val defaultTemplate: String
  protected val defaultTrigger: Seq[Trigger]
}
