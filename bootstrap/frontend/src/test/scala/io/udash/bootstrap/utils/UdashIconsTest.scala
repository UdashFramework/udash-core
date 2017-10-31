package io.udash.bootstrap.utils

import io.udash._
import io.udash.css.CssView
import io.udash.testing.UdashFrontendTest

import scala.language.postfixOps

class UdashIconsTest extends UdashFrontendTest with CssView {
  import scalatags.JsDom.all._

  "Bootstrap icons" should {
    "work with .styleIf" in {
      val p: Property[Boolean] = Property(false)
      val el = span(
        UdashIcons.FontAwesome.fa,
        UdashIcons.FontAwesome.addressBook.styleIf(p),
        UdashIcons.FontAwesome.addressBookO.styleIf(p.transform(!_)),
        UdashIcons.FontAwesome.dashboard.styleIf(p)
      ).render


      el.classList.length should be(2)

      for (i <- 0 to 10) {
        p.set(!p.get)
        el.classList.length should be(if (i % 2 == 0) 3 else 2)
      }
    }
  }
}