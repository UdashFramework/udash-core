package io.udash.bootstrap
package modal

import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js

class UdashModal private(modalSize: ModalSize, fade: Boolean, labelId: String,
                         backdrop: UdashModal.BackdropType, keyboard: Boolean, autoInit: Boolean)
                        (headerFactory: Option[() => dom.Element],
                         bodyFactory: Option[() => dom.Element],
                         footerFactory: Option[() => dom.Element])
  extends UdashBootstrapComponent with Listenable[UdashModal, UdashModal.ModalEvent] {

  import BootstrapTags._
  import UdashModal._
  import io.udash.wrappers.jquery._

  override val componentId: ComponentId = UdashBootstrap.newId()

  private def jQSelector(): UdashModalJQuery =
    jQ(s"#$componentId").asModal()

  /** Toggles modal visibility. */
  def toggle(): Unit = jQSelector().modal("toggle")
  /** Shows modal window. */
  def show(): Unit = jQSelector().modal("show")
  /** Hides modal window. */
  def hide(): Unit = jQSelector().modal("hide")
  /** Readjusts the modal's positioning to counter a scrollbar in case one should appear, which would make the modal jump to the left. */
  def handleUpdate(): Unit = jQSelector().modal("handleUpdate")

  import scalatags.JsDom.all._

  /** Attributes which should be added to button showing this modal window.
    * Example: `UdashButton()(collapse.toggleButtonAttrs(), "Toggle...")`*/
  def openButtonAttrs(): Seq[scalatags.generic.AttrPair[Element, String]] =
    Seq(
      dataToggle := "modal",
      dataTarget := s"#$componentId"
    )

  override lazy val render: Element = {
    val content = Seq(
      (headerFactory, BootstrapStyles.Modal.modalHeader),
      (bodyFactory, BootstrapStyles.Modal.modalBody),
      (footerFactory, BootstrapStyles.Modal.modalFooter)
    ).filter(_._1.nonEmpty).map { case (factory, styleName) =>
      val el = factory.get.apply()
      el.classList.add(styleName.cls)
      el
    }

    val el = div(
      BootstrapStyles.Modal.modal, BootstrapStyles.fade.styleIf(fade),
      tabindex := "-1", role := "dialog", aria.labelledby := labelId,
      id := componentId, BootstrapTags.dataBackdrop := backdrop.jsValue,
      BootstrapTags.dataKeyboard := keyboard, BootstrapTags.dataShow := autoInit
    )(
      div(BootstrapStyles.Modal.modalDialog, modalSize, role := "document")(
        div(BootstrapStyles.Modal.modalContent)(
          content
        )
      )
    ).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.modal", jQFire(ModalShowEvent(this)))
    jQEl.on("shown.bs.modal", jQFire(ModalShownEvent(this)))
    jQEl.on("hide.bs.modal", jQFire(ModalHideEvent(this)))
    jQEl.on("hidden.bs.modal", jQFire(ModalHiddenEvent(this)))
    el
  }
}

object UdashModal {
  sealed class BackdropType(val jsValue: String)
  case object ActiveBackdrop extends BackdropType("true")
  case object StaticBackdrop extends BackdropType("static")
  case object NoneBackdrop extends BackdropType("false")

  sealed trait ModalEvent extends ListenableEvent[UdashModal]
  case class ModalShowEvent(source: UdashModal) extends ModalEvent
  case class ModalShownEvent(source: UdashModal) extends ModalEvent
  case class ModalHideEvent(source: UdashModal) extends ModalEvent
  case class ModalHiddenEvent(source: UdashModal) extends ModalEvent

  /**
    * Creates modal window. More: <a href="http://getbootstrap.com/javascript/#modals">Bootstrap Docs</a>.
    *
    * @param modalSize     Modal window size.
    * @param fade          If true, show&hide will be animated.
    * @param labelId       ID of the label describing this modal.
    * @param backdrop      Modal backdrop type.
    * @param keyboard      If true, allows user to close modal with keyboard (Esc button).
    * @param autoInit      If true, automatically initializes modal on creation.
    * @param headerFactory Creates content of modal header. Modal will be rendered without the header if `None`.
    * @param bodyFactory   Creates content of modal body. Modal will be rendered without body if `None`.
    * @param footerFactory Creates content of modal footer. Modal without footer will be rendered if `None`.
    * @return `UdashModal` component, call render to create DOM element.
    */
  def apply(modalSize: ModalSize = ModalSize.Default, fade: Boolean = true, labelId: String = "",
            backdrop: BackdropType = ActiveBackdrop, keyboard: Boolean = true, autoInit: Boolean = true)
           (headerFactory: Option[() => Element] = None,
            bodyFactory: Option[() => Element] = None,
            footerFactory: Option[() => Element] = None): UdashModal =
    new UdashModal(modalSize, fade, labelId, backdrop, keyboard, autoInit)(headerFactory, bodyFactory, footerFactory)

  /** Attributes which should be added to button closing the modal window.
    * Example: `UdashButton()(UdashModal.CloseButtonAttr, "Close...")`*/
  lazy val CloseButtonAttr: scalatags.generic.AttrPair[Element, String] = {
    import scalatags.JsDom.all._
    BootstrapTags.dataDismiss := "modal"
  }

  @js.native
  private trait UdashModalJQuery extends JQuery {
    def modal(cmd: String): UdashModalJQuery = js.native
  }

  private implicit class UdashModalJQueryExt(jQ: JQuery) {
    def asModal(): UdashModalJQuery =
      jQ.asInstanceOf[UdashModalJQuery]
  }
}
