package io.udash.bootstrap.alert

import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.Button

class UdashAlertTest extends UdashFrontendTest {

  import scalatags.JsDom.all._

  "UdashAlert component" should {
    "maintain dismissed property on click" in {
      val alert = UdashAlert.dismissible(AlertStyle.Info)()
      alert.dismissed.get shouldBe false
      alert.render.children.apply(0).asInstanceOf[Button].click()
      alert.dismissed.get shouldBe true
    }

    "hide on dismiss" in {
      val alert = UdashAlert.dismissible(AlertStyle.Info)("lol")
      alert.dismissed.get shouldBe false
      val division = div(alert.render).render
      jQ("body").append(division)
      division.childElementCount shouldBe 1
      alert.dismiss()
      alert.dismissed.get shouldBe true
      division.childElementCount shouldBe 0
    }
  }
}
