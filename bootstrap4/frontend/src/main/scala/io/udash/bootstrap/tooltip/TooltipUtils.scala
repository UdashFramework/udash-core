package io.udash.bootstrap
package tooltip

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.component.{Listenable, ListenableEvent}
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
    * Check <a href="http://getbootstrap.com/docs/4.1/components/popovers/#methods">Bootstrap Docs</a> for more details. */
  def destroy(): Unit

  private[tooltip] def reloadContent(): Unit
}

abstract class TooltipUtils[TooltipType <: Tooltip[_, TooltipType]] {
  case class Delay(show: Duration, hide: Duration) extends AbstractCase

  final class Placement(val jsValue: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Placement extends AbstractValueEnumCompanion[Placement] {
    final val Auto: Value = new Placement("auto")
    final val Top: Value = new Placement("top")
    final val Bottom: Value = new Placement("bottom")
    final val Left: Value = new Placement("left")
    final val Right: Value = new Placement("right")
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
    boundary: String = "scrollParent",
    container: Option[String] = None,
    content: dom.Node => String = _ => "",
    delay: Delay = Delay(0 millis, 0 millis),
    html: Boolean = false,
    offset: String = "0",
    placement: (dom.Node, dom.Node) => Seq[Placement] = defaultPlacement,
    template: Option[String] = None,
    title: dom.Node => String = _ => "",
    trigger: Seq[Trigger] = defaultTrigger
  )(el: dom.Node): TooltipType =
    initTooltip(
      js.Dictionary(
        "animation" -> animation,
        "boundary" -> boundary,
        "container" -> container.getOrElse(false),
        "content" -> js.ThisFunction.fromFunction1(content),
        "delay" -> js.Dictionary("show" -> delay.show.toMillis, "hide" -> delay.hide.toMillis),
        "html" -> html,
        "offset" -> offset,
        "placement" -> scalajs.js.Any.fromFunction2((popover: dom.Node, trigger: dom.Node) => placement(popover, trigger).map(_.jsValue).mkString(" ")),
        "template" -> template.getOrElse(defaultTemplate),
        "title" -> js.ThisFunction.fromFunction1(title),
        "trigger" -> trigger.map(_.jsValue).mkString(" ")
      )
    )(el)

  /**
    * Adds tooltip/popover to provided element with translated content.
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
  def i18n(
    animation: Boolean = true,
    boundary: String = "scrollParent",
    container: Option[String] = None,
    content: dom.Node => TranslationKey0 = _ => TranslationKey.untranslatable(""),
    delay: Delay = Delay(0 millis, 0 millis),
    html: Boolean = false,
    offset: String = "0",
    placement: (dom.Node, dom.Node) => Seq[Placement] = defaultPlacement,
    template: Option[String] = None,
    title: dom.Node => TranslationKey0 = _ => TranslationKey.untranslatable(""),
    trigger: Seq[Trigger] = defaultTrigger
  )(el: dom.Node)(implicit langProperty: LangProperty, translationProvider: TranslationProvider): TooltipType = {

    val tp = apply(animation, boundary, container, _ => "", delay, html, offset, placement, template, _ => "", trigger)(el)

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
    tp.listen { case TooltipEvent(_, TooltipEvent.EventType.Show) => updateContent() }
    tp
  }

  protected def initTooltip(options: js.Dictionary[Any])(el: dom.Node): TooltipType
  protected val defaultPlacement: (dom.Node, dom.Node) => Seq[Placement]
  protected val defaultTemplate: String
  protected val defaultTrigger: Seq[Trigger]
}
