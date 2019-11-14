package io.udash.bootstrap
package collapse

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.AttrPair

import scala.scalajs.js

final class UdashCollapse private(
  parentSelector: Option[String],
  toggleOnInit: Boolean,
  override val componentId: ComponentId
)(
  content: Binding.NestedInterceptor => Modifier
) extends UdashBootstrapComponent with Listenable[UdashCollapse, UdashCollapse.CollapseEvent] {

  import UdashCollapse._
  import io.udash.bootstrap.utils.BootstrapTags._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  /** Toggle state of this collapse. */
  def toggle(): Unit = jQSelector().collapse("toggle")

  /** Shows this collapse. */
  def show(): Unit = jQSelector().collapse("show")

  /** Hides this collapse. */
  def hide(): Unit = jQSelector().collapse("hide")

  /** Attributes which should be added to the button toggling this collapse component.
    * Example: `UdashButton()(_ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle..."))`*/
  def toggleButtonAttrs(): Seq[AttrPair[Element, String]] = {
    import scalatags.JsDom.all._
    Seq(
      dataToggle := "collapse",
      dataTarget := s"#$componentId"
    )
  }

  override val render: Element = {
    val el = div(
      parentSelector.map(dataParent := _), dataToggle := toggleOnInit,
      BootstrapStyles.Collapse.collapse, id := componentId
    )(content(nestedInterceptor)).render

    val jQEl = jQ(el)
    nestedInterceptor(new JQueryOnBinding(jQEl, "show.bs.collapse", (_: Element, _: JQueryEvent) => fire(CollapseEvent(this, CollapseEvent.EventType.Show))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "shown.bs.collapse", (_: Element, _: JQueryEvent) => fire(CollapseEvent(this, CollapseEvent.EventType.Shown))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hide.bs.collapse", (_: Element, _: JQueryEvent) => fire(CollapseEvent(this, CollapseEvent.EventType.Hide))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hidden.bs.collapse", (_: Element, _: JQueryEvent) => fire(CollapseEvent(this, CollapseEvent.EventType.Hidden))))
    el
  }

  override def kill(): Unit = {
    super.kill()
    hide()
    jQSelector().collapse("dispose")
  }

  private def jQSelector(): UdashCollapseJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashCollapseJQuery]
}

object UdashCollapse {
  /** More: <a href="http://getbootstrap.com/docs/4.1/components/collapse/#events">Bootstrap Docs</a> */
  final case class CollapseEvent(
    override val source: UdashCollapse,
    tpe: CollapseEvent.EventType
  ) extends AbstractCase with ListenableEvent[UdashCollapse]

  object CollapseEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      /** This event fires immediately when the show instance method is called. */
      final val Show: Value = new EventType
      /** This event is fired when a collapse element has been made visible to the user (will wait for CSS transitions to complete). */
      final val Shown: Value = new EventType
      /** This event is fired immediately when the hide method has been called. */
      final val Hide: Value = new EventType
      /** This event is fired when a collapse element has been hidden from the user (will wait for CSS transitions to complete). */
      final val Hidden: Value = new EventType
    }
  }

  /**
    * Creates a collapsible component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/collapse/">Bootstrap Docs</a>.
    *
    * @param parentSelector If a selector is provided, all collapsible elements under the specified parent will be
    *                       closed when this collapsible item is shown.
    * @param toggleOnInit   Toggles the collapsible element on invocation.
    * @param componentId    An id of the root DOM node.
    * @param content        A content of the component.
    *                       Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashCollapse` component, call `render` to create a DOM element.
    */
  def apply(
    parentSelector: Option[String] = None,
    toggleOnInit: Boolean = true,
    componentId: ComponentId = ComponentId.generate()
  )(content: Binding.NestedInterceptor => Modifier): UdashCollapse = {
    new UdashCollapse(parentSelector, toggleOnInit, componentId)(content)
  }

  @js.native
  private trait UdashCollapseJQuery extends JQuery {
    def collapse(cmd: String): UdashCollapseJQuery = js.native
  }
}
