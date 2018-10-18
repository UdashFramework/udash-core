package io.udash.bootstrap
package modal

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils._
import io.udash.component.{ComponentId, Listenable, ListenableEvent}
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.Element

import scala.scalajs.js

final class UdashModal private(
  modalSize: ReadableProperty[Option[BootstrapStyles.Size]],
  fade: ReadableProperty[Boolean],
  labelId: ReadableProperty[Option[String]],
  backdrop: ReadableProperty[UdashModal.BackdropType],
  keyboard: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  headerFactory: Option[Binding.NestedInterceptor => Element],
  bodyFactory: Option[Binding.NestedInterceptor => Element],
  footerFactory: Option[Binding.NestedInterceptor => Element]
) extends UdashBootstrapComponent with Listenable[UdashModal, UdashModal.ModalEvent] {

  import UdashModal._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._
  import scalatags.JsDom.all._

  /** Toggles modal visibility. */
  def toggle(): Unit = jQSelector().modal("toggle")
  /** Shows modal window. */
  def show(): Unit = jQSelector().modal("show")
  /** Hides modal window. */
  def hide(): Unit = jQSelector().modal("hide")
  /** Readjusts the modal's positioning to counter a scrollbar in case one should
    * appear, which would make the modal jump to the left. */
  def handleUpdate(): Unit = jQSelector().modal("handleUpdate")

  override val render: Element = {
    val content = Seq(
      (headerFactory, BootstrapStyles.Modal.header),
      (bodyFactory, BootstrapStyles.Modal.body),
      (footerFactory, BootstrapStyles.Modal.footer)
    ).collect { case (Some(factory), styleName) =>
      factory(nestedInterceptor).styles(styleName)
    }

    val el = div(
      BootstrapStyles.Modal.modal,
      tabindex := "-1", role := "dialog", id := componentId,
      nestedInterceptor(aria.labelledby.bindIf(labelId.transform(_.getOrElse("")), labelId.transform(_.isDefined))),
      nestedInterceptor(BootstrapTags.dataBackdrop.bind(backdrop.transform(_.jsValue))),
      nestedInterceptor(BootstrapTags.dataKeyboard.bind(keyboard.transform(_.toString))),
      nestedInterceptor(BootstrapStyles.fade.styleIf(fade))
    )(
      div(
        BootstrapStyles.Modal.dialog, role := "document",
        nestedInterceptor((BootstrapStyles.Modal.size _).reactiveOptionApply(modalSize))
      )(
        div(BootstrapStyles.Modal.content)(
          content
        )
      )
    ).render

    val jQEl = jQ(el)
    nestedInterceptor(new JQueryOnBinding(jQEl, "show.bs.modal", (_: Element, _: JQueryEvent) => fire(ModalEvent(this, ModalEvent.EventType.Show))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "shown.bs.modal", (_: Element, _: JQueryEvent) => fire(ModalEvent(this, ModalEvent.EventType.Shown))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hide.bs.modal", (_: Element, _: JQueryEvent) => fire(ModalEvent(this, ModalEvent.EventType.Hide))))
    nestedInterceptor(new JQueryOnBinding(jQEl, "hidden.bs.modal", (_: Element, _: JQueryEvent) => fire(ModalEvent(this, ModalEvent.EventType.Hidden))))
    el
  }

  override def kill(): Unit = {
    super.kill()
    jQSelector().modal("dispose")
  }

  private def jQSelector(): UdashModalJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashModalJQuery]
}

object UdashModal {
  final class BackdropType(val jsValue: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object BackdropType extends AbstractValueEnumCompanion[BackdropType] {
    final val Active: Value = new BackdropType("true")
    final val Static: Value = new BackdropType("static")
    final val None: Value = new BackdropType("false")
  }

  /** More: <a href="http://getbootstrap.com/docs/4.1/components/modal/#events">Bootstrap Docs</a> */
  final case class ModalEvent(
    override val source: UdashModal,
    tpe: ModalEvent.EventType
  ) extends AbstractCase with ListenableEvent[UdashModal]

  object ModalEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      /** This event fires immediately when the show instance method is called.
        * If caused by a click, the clicked element is available as the relatedTarget property of the event. */
      final val Show: Value = new EventType
      /** This event is fired when the modal has been made visible to the user (will wait for CSS transitions to complete).
        * If caused by a click, the clicked element is available as the relatedTarget property of the event. */
      final val Shown: Value = new EventType
      /** This event is fired immediately when the hide instance method has been called. */
      final val Hide: Value = new EventType
      /** This event is fired when the modal has finished being hidden from the user (will wait for CSS transitions to complete). */
      final val Hidden: Value = new EventType
    }
  }

  /**
    * Creates a modal window.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/modal/">Bootstrap Docs</a>.
    *
    * @param modalSize     A window size. One of the standard bootstrap sizes `BootstrapStyles.Size`.
    * @param fade          If true, show and hide will be animated.
    * @param labelId       An id of the label describing this modal.
    * @param backdrop      Modal backdrop type.
    * @param keyboard      If true, allows user to close modal with keyboard (Esc button).
    * @param componentId   An id of the root DOM node.
    * @param headerFactory Creates content of modal header. Modal will be rendered without the header if `None`.
    *                      Use the provided interceptor to properly clean up bindings inside the content.
    * @param bodyFactory   Creates content of modal body. Modal will be rendered without body if `None`.
    *                      Use the provided interceptor to properly clean up bindings inside the content.
    * @param footerFactory Creates content of modal footer. Modal without footer will be rendered if `None`.
    *                      Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashModal` component, call `render` to create a DOM element.
    */
  def apply(
    modalSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    fade: ReadableProperty[Boolean] = UdashBootstrap.True,
    labelId: ReadableProperty[Option[String]] = UdashBootstrap.None,
    backdrop: ReadableProperty[UdashModal.BackdropType] = BackdropType.Active.toProperty,
    keyboard: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.newId()
  )(
    headerFactory: Option[Binding.NestedInterceptor => Element],
    bodyFactory: Option[Binding.NestedInterceptor => Element],
    footerFactory: Option[Binding.NestedInterceptor => Element]
  ): UdashModal = {
    new UdashModal(
      modalSize, fade, labelId, backdrop, keyboard, componentId
    )(headerFactory, bodyFactory, footerFactory)
  }

  /** Attributes which should be added to button closing the modal window.
    * Example: `UdashButton()(_ => Seq[Modifier](UdashModal.CloseButtonAttr, "Close..."))` */
  lazy val CloseButtonAttr: scalatags.generic.AttrPair[Element, String] = {
    import scalatags.JsDom.all._
    BootstrapTags.dataDismiss := "modal"
  }

  @js.native
  private trait UdashModalJQuery extends JQuery {
    def modal(cmd: String): UdashModalJQuery = js.native
  }
}
