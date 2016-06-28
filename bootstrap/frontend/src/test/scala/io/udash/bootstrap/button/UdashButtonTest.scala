package io.udash.bootstrap.button

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._

class UdashButtonTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "UdashButton component" should {
    "call listeners on click" in {
      val button = UdashButton()("Test")
      var clickCounter = 0
      button.listen { case _ => clickCounter += 1 }

      val el = button.render
      for (i <- 1 to 10) {
        el.click()
        clickCounter should be(i)
      }
    }

    "disable on property change" in {
      val button = UdashButton()("Test")
      var clickCounter = 0
      button.listen { case _ => clickCounter += 1 }

      val el = button.render
      val firstRoundClicks = 5
      for (i <- 1 to firstRoundClicks) {
        el.click()
        clickCounter should be(i)
      }

      button.disabled.set(true)
      for (i <- 1 to 10) {
        el.click()
        clickCounter should be(firstRoundClicks)
      }

      button.disabled.set(false)
      for (i <- firstRoundClicks + 1  to 2 * firstRoundClicks) {
        el.click()
        clickCounter should be(i)
      }
    }

    "work as toggle button" in {
      val activeProperty = Property(false)
      val button = UdashButton.toggle(active = activeProperty)("Toggle")

      val el = jQ(button.render)
      for (i <- 1 to 10) {
        el.click()
        activeProperty.get should be(true)
        el.hasClass(BootstrapStyles.active.cls) should be(true)
        el.click()
        activeProperty.get should be(false)
        el.hasClass(BootstrapStyles.active.cls) should be(false)
      }

      for (i <- 1 to 10) {
        activeProperty.set(true)
        el.hasClass(BootstrapStyles.active.cls) should be(true)
        activeProperty.set(false)
        el.hasClass(BootstrapStyles.active.cls) should be(false)
      }
    }
  }
}
