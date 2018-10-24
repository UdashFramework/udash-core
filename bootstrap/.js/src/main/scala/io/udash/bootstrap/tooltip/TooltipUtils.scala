package io.udash.bootstrap
package tooltip

import io.udash._
import io.udash.i18n.{LangProperty, TranslationKey, TranslationKey0, TranslationProvider}
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, DurationInt}
import scala.scalajs.js

trait Tooltip[EventType <: ListenableEvent[ThisType], ThisType <: Tooltip[EventType, ThisType]] extends Listenable[ThisType, EventType] {
  /** Shows the tooltip. */
  def show(): Unit

  /** Hides the tooltip. */
  def hide(): Unit

  /** Toggles tooltip visibility. */
  def toggle(): Unit

  /** Hides and destroys an element's popover.
    * Check <a href="https://getbootstrap.com/docs/3.3/javascript/#popovers-methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit

  private[tooltip] def reloadContent(): Unit
}

abstract class TooltipUtils[TooltipType <: Tooltip[_, TooltipType]] {
  case class Delay(show: Duration, hide: Duration)
  case class Viewport(selector: String, padding: Int)

  final class Placement(val name: String)
  val AutoPlacement = new Placement("auto")
  val TopPlacement = new Placement("top")
  val BottomPlacement = new Placement("bottom")
  val LeftPlacement = new Placement("left")
  val RightPlacement = new Placement("right")

  final class Trigger(val name: String)
  val ClickTrigger = new Trigger("click")
  val HoverTrigger = new Trigger("hover")
  val FocusTrigger = new Trigger("focus")
  val ManualTrigger = new Trigger("manual")

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
            content: (dom.Node) => String = (_) => "",
            delay: Delay = Delay(0 millis, 0 millis),
            html: Boolean = false,
            placement: (dom.Node, dom.Node) => Seq[Placement] = defaultPlacement,
            template: Option[String] = None,
            title: (dom.Node) => String = (_) => "",
            trigger: Seq[Trigger] = defaultTrigger,
            viewport: Viewport = Viewport("body", 0))(el: dom.Node): TooltipType =
    initTooltip(
      js.Dictionary(
        "animation" -> animation,
        "container" -> container.getOrElse(false),
        "content" -> js.ThisFunction.fromFunction1(content),
        "delay" -> js.Dictionary("show" -> delay.show.toMillis, "hide" -> delay.hide.toMillis),
        "html" -> html,
        "placement" -> scalajs.js.Any.fromFunction2((popover: dom.Node, trigger: dom.Node) => placement(popover, trigger).map(_.name).mkString(" ")),
        "template" -> template.getOrElse(defaultTemplate),
        "title" -> js.ThisFunction.fromFunction1(title),
        "trigger" -> trigger.map(_.name).mkString(" "),
        "viewport" -> js.Dictionary("selector" -> viewport.selector, "padding" -> viewport.padding)
      )
    )(el)

  /**
    * Add tooltip/popover to provided element with translated content.
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
  def i18n(animation: Boolean = true,
           container: Option[String] = None,
           content: (dom.Node) => TranslationKey0 = (_) => TranslationKey.untranslatable(""),
           delay: Delay = Delay(0 millis, 0 millis),
           html: Boolean = false,
           placement: (dom.Node, dom.Node) => Seq[Placement] = defaultPlacement,
           template: Option[String] = None,
           title: (dom.Node) => TranslationKey0 = (_) => TranslationKey.untranslatable(""),
           trigger: Seq[Trigger] = defaultTrigger,
           viewport: Viewport = Viewport("body", 0))
          (el: dom.Node)
          (implicit langProperty: LangProperty, translationProvider: TranslationProvider): TooltipType = {

    val tp = apply(animation, container, _ => "", delay, html, placement, template, _ => "", trigger, viewport)(el)

    var lastContent = ""
    var lastTitle = ""
    def updateContent(): Unit = {
      implicit val ec: ExecutionContext = com.avsystem.commons.concurrent.RunNowEC
      for {
        contentTxt <- content(el)()
        titleTxt <- title(el)()
      } yield if (lastTitle != titleTxt.string || lastContent != contentTxt.string) {
        import io.udash.wrappers.jquery._
        jQ(el)
          .attr("data-content", contentTxt.string)
          .attr("data-original-title", titleTxt.string)

        lastTitle = titleTxt.string
        lastContent = contentTxt.string

        tp.reloadContent()
      }
    }

    updateContent()
    tp.listen { case _: TooltipEvent.ShowEvent[TooltipType] => updateContent() }
    tp
  }

  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): TooltipType
  protected val defaultPlacement: (dom.Node, dom.Node) => Seq[Placement]
  protected val defaultTemplate: String
  protected val defaultTrigger: Seq[Trigger]
}
