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
      for (i <- firstRoundClicks + 1  to 2 * firstRoundClicks) {
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
      val buttonStyle: Property[BootstrapStyles.Color] = Property(BootstrapStyles.Color.Secondary)
      val size: Property[Option[BootstrapStyles.Size]] = Property(None)
      val outline: Property[Boolean] = Property(false)
      val block: Property[Boolean] = Property(false)
      val active: Property[Boolean] = Property(false)
      val disabled: Property[Boolean] = Property(false)

      val btn = UdashButton(buttonStyle, size, outline, block, active, disabled)("btn")
      val el = btn.render

      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Secondary).className)

      buttonStyle.set(BootstrapStyles.Color.Info)

      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Info).className)

      size.set(Some(BootstrapStyles.Size.Large))

      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Info).className)
      el.classList should contain(BootstrapStyles.Button.size(BootstrapStyles.Size.Large).className)

      size.set(Some(BootstrapStyles.Size.Small))

      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Info).className)
      el.classList should contain(BootstrapStyles.Button.size(BootstrapStyles.Size.Small).className)

      size.set(None)

      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.color(BootstrapStyles.Color.Info).className)

      outline.set(true)

      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.outline(BootstrapStyles.Color.Info).className)

      block.set(true)

      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.outline(BootstrapStyles.Color.Info).className)

      active.set(true)

      el.classList.length should be(4)
      el.classList should contain(BootstrapStyles.active.className)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.outline(BootstrapStyles.Color.Info).className)

      disabled.set(true)

      el.classList.length should be(5)
      el.classList should contain(BootstrapStyles.active.className)
      el.classList should contain(BootstrapStyles.disabled.className)
      el.classList should contain(BootstrapStyles.Button.btn.className)
      el.classList should contain(BootstrapStyles.Button.block.className)
      el.classList should contain(BootstrapStyles.Button.outline(BootstrapStyles.Color.Info).className)

      btn.kill()
      buttonStyle.listenersCount() should be(0)
      size.listenersCount() should be(0)
      outline.listenersCount() should be(0)
      block.listenersCount() should be(0)
      active.listenersCount() should be(0)
      disabled.listenersCount() should be(0)
    }
  }
}
