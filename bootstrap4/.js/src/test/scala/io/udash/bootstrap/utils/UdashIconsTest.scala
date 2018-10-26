package io.udash.bootstrap.utils

import io.udash._
import io.udash.css.CssView
import io.udash.testing.UdashCoreFrontendTest


class UdashIconsTest extends UdashCoreFrontendTest with CssView {

  import scalatags.JsDom.all._

  "Bootstrap icons" should {
    "work with .styleIf" in {
      import UdashIcons.FontAwesome.Regular._
      val p: Property[Boolean] = Property(false)
      val el = span(
        addressBook.styleIf(p),
        angry.styleIf(p.transform(!_)),
        heart.styleIf(p)
      ).render


      el.classList.length should be(2)

      for (i <- 0 to 10) {
        p.set(!p.get)
        el.classList.length should be(if (i % 2 == 0) 3 else 2)
      }
    }
  }
}