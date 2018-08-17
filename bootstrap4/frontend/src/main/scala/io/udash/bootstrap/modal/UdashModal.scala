package io.udash.bootstrap
package modal

import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom
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
  headerFactory: Option[() => dom.Element],
  bodyFactory: Option[() => dom.Element],
  footerFactory: Option[() => dom.Element]
) extends UdashBootstrapComponent with Listenable[UdashModal, UdashModal.ModalEvent] {

  import UdashModal._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._
  import scalatags.JsDom.all._


  private def jQSelector(): UdashModalJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashModalJQuery]

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
      factory().styles(styleName)
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
    jQEl.on("show.bs.modal", jQFire(ModalEvent(this, ModalEvent.EventType.Show)))
    jQEl.on("shown.bs.modal", jQFire(ModalEvent(this, ModalEvent.EventType.Shown)))
    jQEl.on("hide.bs.modal", jQFire(ModalEvent(this, ModalEvent.EventType.Hide)))
    jQEl.on("hidden.bs.modal", jQFire(ModalEvent(this, ModalEvent.EventType.Hidden)))
    el
  }
}

object UdashModal {
  final class BackdropType(val jsValue: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object BackdropType extends AbstractValueEnumCompanion[BackdropType] {
    final val Active: Value = new BackdropType("true")
    final val Static: Value = new BackdropType("static")
    final val None: Value = new BackdropType("false")
  }

  final case class ModalEvent(
    override val source: UdashModal,
    tpe: ModalEvent.EventType
  ) extends AbstractCase with ListenableEvent[UdashModal]

  object ModalEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      final val Show, Shown, Hide, Hidden: Value = new EventType
    }
  }

  /**
    * Creates modal window. More: <a href="http://getbootstrap.com/javascript/#modals">Bootstrap Docs</a>.
    *
    * @param modalSize     Modal window size.
    * @param fade          If true, show&hide will be animated.
    * @param labelId       ID of the label describing this modal.
    * @param backdrop      Modal backdrop type.
    * @param keyboard      If true, allows user to close modal with keyboard (Esc button).
    * @param componentId   Id of the root DOM node.
    * @param headerFactory Creates content of modal header. Modal will be rendered without the header if `None`.
    * @param bodyFactory   Creates content of modal body. Modal will be rendered without body if `None`.
    * @param footerFactory Creates content of modal footer. Modal without footer will be rendered if `None`.
    * @return `UdashModal` component, call render to create DOM element.
    */
  def apply(
    modalSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    fade: ReadableProperty[Boolean] = UdashBootstrap.True,
    labelId: ReadableProperty[Option[String]] = UdashBootstrap.None,
    backdrop: ReadableProperty[UdashModal.BackdropType] = BackdropType.Active.toProperty,
    keyboard: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.newId()
  )(
    headerFactory: Option[() => dom.Element],
    bodyFactory: Option[() => dom.Element],
    footerFactory: Option[() => dom.Element]
  ): UdashModal = {
    new UdashModal(
      modalSize, fade, labelId, backdrop, keyboard, componentId
    )(headerFactory, bodyFactory, footerFactory)
  }

  /** Attributes which should be added to button closing the modal window.
    * Example: `UdashButton()(UdashModal.CloseButtonAttr, "Close...")` */
  lazy val CloseButtonAttr: scalatags.generic.AttrPair[Element, String] = {
    import scalatags.JsDom.all._
    BootstrapTags.dataDismiss := "modal"
  }

  @js.native
  private trait UdashModalJQuery extends JQuery {
    def modal(cmd: String): UdashModalJQuery = js.native
  }
}
