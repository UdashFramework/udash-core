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

  val dialogId: ComponentId = UdashBootstrap.newId()

  def jQSelector(): UdashModalJQuery =
    jQ(s"#$dialogId").asModal()

  def toggle(): Unit = jQSelector().modal("toggle")
  def show(): Unit = jQSelector().modal("show")
  def hide(): Unit = jQSelector().modal("hide")
  def handleUpdate(): Unit = jQSelector().modal("handleUpdate")

  import scalatags.JsDom.all._

  def openButtonAttrs(): Seq[scalatags.generic.AttrPair[Element, String]] =
    Seq(
      dataToggle := "modal",
      dataTarget := s"#$dialogId"
    )

  lazy val render: Element = {

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
      id := dialogId.id, BootstrapTags.dataBackdrop := backdrop.jsValue,
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

  def apply(modalSize: ModalSize = ModalSize.Default, fade: Boolean = true, labelId: String = "",
            backdrop: BackdropType = ActiveBackdrop, keyboard: Boolean = true, autoInit: Boolean = true)
           (headerFactory: Option[() => Element] = None,
            bodyFactory: Option[() => Element] = None,
            footerFactory: Option[() => Element] = None): UdashModal =
    new UdashModal(modalSize, fade, labelId, backdrop, keyboard, autoInit)(headerFactory, bodyFactory, footerFactory)

  lazy val CloseButtonAttr: scalatags.generic.AttrPair[Element, String] = {
    import scalatags.JsDom.all._
    BootstrapTags.dataDismiss := "modal"
  }

  @js.native
  trait UdashModalJQuery extends JQuery {
    def modal(cmd: String): UdashModalJQuery = js.native
  }

  implicit class UdashModalJQueryExt(jQ: JQuery) {
    def asModal(): UdashModalJQuery =
      jQ.asInstanceOf[UdashModalJQuery]
  }
}
