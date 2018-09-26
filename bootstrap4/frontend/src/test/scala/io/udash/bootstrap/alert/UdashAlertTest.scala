package io.udash.bootstrap.alert

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.Button

class UdashAlertTest extends UdashFrontendTest {

  import scalatags.JsDom.all._

  "UdashAlert component" should {
    "maintain dismissed property on click" in {
      val alert = DismissibleUdashAlert(BootstrapStyles.Color.Info.toProperty)("lol")
      alert.dismissed.get shouldBe false
      alert.render.children.apply(0).asInstanceOf[Button].click()
      alert.dismissed.get shouldBe true
    }

    "hide on dismiss" in {
      val alert = DismissibleUdashAlert(BootstrapStyles.Color.Info.toProperty)("lol")
      alert.dismissed.get shouldBe false
      val division = div(alert.render).render
      jQ("body").append(division)
      division.childElementCount shouldBe 1
      alert.dismiss()
      alert.dismissed.get shouldBe true
      division.childElementCount shouldBe 0
    }

    "update style" in {
      val style = Property[BootstrapStyles.Color](BootstrapStyles.Color.Secondary)
      val alert = UdashAlert(style)("lol")
      val el = alert.render

      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Alert.alert.className)
      el.classList should contain(BootstrapStyles.Alert.color(BootstrapStyles.Color.Secondary).className)

      style.set(BootstrapStyles.Color.Info)
      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Alert.alert.className)
      el.classList should contain(BootstrapStyles.Alert.color(BootstrapStyles.Color.Info).className)

      style.listenersCount() should be(1)

      alert.kill()
      style.listenersCount() should be(0)
    }
  }
}
