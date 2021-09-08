package io.udash.bootstrap.button

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import io.udash.wrappers.jquery._

class UdashButtonTest extends UdashCoreFrontendTest {

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
      val disabled = Property(false)
      val button = UdashButton(disabled = disabled)("Test")
      var clickCounter = 0
      button.listen { case _ => clickCounter += 1 }

      val el = button.render
      val firstRoundClicks = 5
      for (i <- 1 to firstRoundClicks) {
        el.click()
        clickCounter should be(i)
      }

      disabled.set(true)
      for (i <- 1 to 10) {
        el.click()
        clickCounter should be(firstRoundClicks)
      }

      disabled.set(false)
      for (i <- firstRoundClicks + 1 to 2 * firstRoundClicks) {
        el.click()
        clickCounter should be(i)
      }

      button.kill()
      disabled.listenersCount() should be(0)
    }

    "work as toggle button" in {
      val activeProperty = Property(false)
      val button = UdashButton.toggle(active = activeProperty)("Toggle")

      val el = jQ(button.render)
      for (i <- 1 to 10) {
        el.trigger(EventName.click)
        activeProperty.get should be(true)
        el.hasClass(BootstrapStyles.active.className) should be(true)
        el.trigger(EventName.click)
        activeProperty.get should be(false)
        el.hasClass(BootstrapStyles.active.className) should be(false)
      }

      for (i <- 1 to 10) {
        activeProperty.set(true)
        el.hasClass(BootstrapStyles.active.className) should be(true)
        activeProperty.set(false)
        el.hasClass(BootstrapStyles.active.className) should be(false)
      }

      button.kill()
      activeProperty.listenersCount() should be(0)
    }

    "apply style changes" in {
      val buttonStyle = BootstrapStyles.Color.Secondary.opt
      val size = BootstrapStyles.Size.Large.opt
      val outline: Boolean = false
      val block: Boolean = true
      val active: Property[Boolean] = Property(false)
      val disabled: Property[Boolean] = Property(false)

      val btn = UdashButton(active = active, disabled = disabled, options = UdashButtonOptions(color = buttonStyle, size = size, outline = outline, block = block))("btn")
      val el = btn.render
      el.classList.length should be(4)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.size(BootstrapStyles.Size.Large).className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Secondary).className)

      active.set(true)

      el.classList.length should be(5)
      el.classList should contain(BootstrapStyles.active.className)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.size(BootstrapStyles.Size.Large).className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Secondary).className)

      disabled.set(true)

      el.classList.length should be(6)
      el.classList should contain(BootstrapStyles.active.className)
      el.classList should contain(BootstrapStyles.disabled.className)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.size(BootstrapStyles.Size.Large).className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Secondary).className)

      btn.kill()
      active.listenersCount() should be(0)
      disabled.listenersCount() should be(0)
    }
  }
}
