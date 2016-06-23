package io.udash.bootstrap.modal

import io.udash._
import io.udash.bootstrap.{BootstrapStyles, UdashBootstrap}
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.testing.{AsyncUdashFrontendTest, UdashFrontendTest}
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.Button

import scalatags.JsDom.all._

class UdashModalTest extends UdashFrontendTest {

  "UdashModal component" should {
    val header = () => div("Header ").render
    val body = () => div("Body ").render
    val footer = () => div("Footer ").render

    "call listeners on opening and closing" in {
      import UdashModal._
      val modal = UdashModal()(Some(header), Some(body), Some(footer))

      var showCounter = 0
      var hideCounter = 0
      modal.listen {
        case ModalShowEvent(_) => showCounter += 1
        case ModalHideEvent(_) => hideCounter += 1
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
  }
}
