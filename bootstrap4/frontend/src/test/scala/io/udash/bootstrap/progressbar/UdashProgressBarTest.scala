package io.udash.bootstrap.progressbar

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest

class UdashProgressBarTest extends UdashFrontendTest {

  "UdashProgressBar component" should {
    "update progress value and clean up property listeners" in {
      val progress = Property(30)
      val showPercentage = Property(true)
      val barStyle = Property(Option[BootstrapStyles.Color](BootstrapStyles.Color.Primary))
      val stripped = Property(true)
      val animated = Property(true)
      val minValue = Property(10)
      val maxValue = Property(50)
      val minWidth = Property(1)
      val progressbar = UdashProgressBar(
        progress, showPercentage, barStyle, stripped, animated, minValue, maxValue, minWidth
      )()
      val el = progressbar.render
      el.textContent should be("50%")

      minValue.set(0)
      el.textContent should be("60%")
      maxValue.set(100)
      el.textContent should be("30%")
      progress.set(66)
      el.textContent should be("66%")

      progressbar.kill()
      progress.listenersCount() should be(0)
      showPercentage.listenersCount() should be(0)
      barStyle.listenersCount() should be(0)
      stripped.listenersCount() should be(0)
      animated.listenersCount() should be(0)
      minValue.listenersCount() should be(0)
      maxValue.listenersCount() should be(0)
      minWidth.listenersCount() should be(0)
    }
  }
}
