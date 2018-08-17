package io.udash.bootstrap.modal

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import scalatags.JsDom.all._

class UdashModalTest extends UdashFrontendTest {

  "UdashModal component" should {
    val header = () => div("Header ").render
    val body = () => div("Body ").render
    val footer = () => div("Footer ").render

    "call listeners on opening and closing" in {
      import UdashModal._
      // fade needs to be false to make this test work in a synchronous way
      val modal = UdashModal(fade = false.toProperty)(Some(header), Some(body), Some(footer))

      var showCounter = 0
      var hideCounter = 0
      modal.listen {
        case ModalEvent(_, ModalEvent.EventType.Show) => showCounter += 1
        case ModalEvent(_, ModalEvent.EventType.Hide) => hideCounter += 1
      }

      jQ("body").append(modal.render)

      for (i <- 1 to 10) {
        modal.show()
        showCounter should be(i)
        hideCounter should be(i-1)
        modal.hide()
        showCounter should be(i)
        hideCounter should be(i)
      }
    }

    "draw only passed elements" in {
      val modal = UdashModal()(
        Some(header),
        Some(body),
        Some(footer)
      )
      val modal1 = UdashModal()(
        None,
        Some(body),
        Some(footer)
      )
      val modal2 = UdashModal()(
        Some(header),
        None,
        Some(footer)
      )
      val modal3 = UdashModal()(
        Some(header),
        Some(body),
        None
      )

      modal.render.textContent should be("Header Body Footer ")
      // TODO replace `contains(...) should be(...)` with `should(Not) contain` everywhere
      modal1.render.textContent.split(" ") shouldNot contain("Header")
      modal1.render.textContent.split(" ") should contain("Body")
      modal1.render.textContent.split(" ") should contain("Footer")
      modal2.render.textContent.split(" ") should contain("Header")
      modal2.render.textContent.split(" ") shouldNot contain("Body")
      modal2.render.textContent.split(" ") should contain("Footer")
      modal3.render.textContent.split(" ") should contain("Header")
      modal3.render.textContent.split(" ") should contain("Body")
      modal3.render.textContent.split(" ") shouldNot contain("Footer")
    }

    "clean up listeners properly" in {
      val modalSize: Property[Option[BootstrapStyles.Size]] = Property(Some(BootstrapStyles.Size.Large))
      val fade: Property[Boolean] = Property(false)
      val labelId: Property[Option[String]] = Property(Some("Test"))
      val backdrop: Property[UdashModal.BackdropType] = Property(UdashModal.BackdropType.None)
      val keyboard: Property[Boolean] = Property(true)
      val modal = UdashModal(modalSize, fade, labelId, backdrop, keyboard)(Some(header), Some(body), Some(footer))
      val el = modal.render

      el.firstElementChild.classList should contain("modal-lg")
      el.firstElementChild.classList shouldNot contain("modal-sm")

      modalSize.set(Some(BootstrapStyles.Size.Small))
      el.firstElementChild.classList shouldNot contain("modal-lg")
      el.firstElementChild.classList should contain("modal-sm")

      el.classList shouldNot contain("fade")
      fade.set(true)
      el.classList should contain("fade")

      el.getAttribute(aria.labelledby.name) should be("Test")
      labelId.set(None)
      el.getAttribute(aria.labelledby.name) should be(null)

      modal.kill()

      modalSize.listenersCount() should be(0)
      fade.listenersCount() should be(0)
      labelId.listenersCount() should be(0)
      backdrop.listenersCount() should be(0)
      keyboard.listenersCount() should be(0)
    }
  }
}
