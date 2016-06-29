package io.udash.bootstrap
package tooltip

import org.scalajs.dom
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import scala.scalajs.js

trait TooltipUtils[TooltipType <: Listenable[TooltipType, _]] {
  case class Delay(show: Duration, hide: Duration)
  case class Viewport(selector: String, padding: Int)

  sealed abstract class Placement(val name: String)
  case object AutoPlacement extends Placement("auto")
  case object TopPlacement extends Placement("top")
  case object BottomPlacement extends Placement("bottom")
  case object LeftPlacement extends Placement("left")
  case object RightPlacement extends Placement("right")

  sealed abstract class Trigger(val name: String)
  case object ClickTrigger extends Trigger("click")
  case object HoverTrigger extends Trigger("hover")
  case object FocusTrigger extends Trigger("focus")
  case object ManualTrigger extends Trigger("manual")

  sealed trait TooltipEvent extends ListenableEvent[TooltipType]
  case class TooltipShowEvent(source: TooltipType) extends TooltipEvent
  case class TooltipShownEvent(source: TooltipType) extends TooltipEvent
  case class TooltipHideEvent(source: TooltipType) extends TooltipEvent
  case class TooltipHiddenEvent(source: TooltipType) extends TooltipEvent
  case class TooltipInsertedEvent(source: TooltipType) extends TooltipEvent

  /**
    * Add tooltip/popover to provided element.
    * More: <a href="http://getbootstrap.com/javascript/#tooltips">Bootstrap Docs (Tooltip)</a>.
    * More: <a href="http://getbootstrap.com/javascript/#popovers">Bootstrap Docs (Popover)</a>.
    *
    * @param animation Apply a CSS fade transition to the popover.
    * @param container Appends the popover to a specific element.
    * @param content   Popover content.
    * @param delay     Show/hide delay.
    * @param html      Treat content and title as HTML.
    * @param placement Tooltip/popover placement.
    * @param template  Tooltip/popover template.
    * @param title     Component title.
    * @param trigger   Triggers to show/hide tooltip.
    * @param viewport  Keeps the popover within the bounds of this element.
    * @param el        Node which will own the created tooltip/popover.
    */
  def apply(animation: Boolean = true,
            container: Option[String] = None,
            content: (dom.Element) => String = (_) => "",
            delay: Delay = Delay(0 millis, 0 millis),
            html: Boolean = false,
            placement: (dom.Element, dom.Element) => Seq[Placement] = defaultPlacement,
            template: Option[String] = None,
            title: (dom.Element) => String = (_) => "",
            trigger: Seq[Trigger] = defaultTrigger,
            viewport: Viewport = Viewport("body", 0))(el: dom.Node): TooltipType =
    initTooltip(
      js.Dictionary(
        "animation" -> animation,
        "container" -> container.getOrElse(false),
        "content" -> js.ThisFunction.fromFunction1(content),
        "delay" -> js.Dictionary("show" -> delay.show.toMillis, "hide" -> delay.hide.toMillis),
        "html" -> html,
        "placement" -> scalajs.js.Any.fromFunction2((popover: dom.Element, trigger: dom.Element) => placement(popover, trigger).map(_.name).mkString(" ")),
        "template" -> template.getOrElse(defaultTemplate),
        "title" -> js.ThisFunction.fromFunction1(title),
        "trigger" -> trigger.map(_.name).mkString(" "),
        "viewport" -> js.Dictionary("selector" -> viewport.selector, "padding" -> viewport.padding)
      )
    )(el)

  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): TooltipType
  protected val defaultPlacement: (dom.Element, dom.Element) => Seq[Placement]
  protected val defaultTemplate: String
  protected val defaultTrigger: Seq[Trigger]
}
