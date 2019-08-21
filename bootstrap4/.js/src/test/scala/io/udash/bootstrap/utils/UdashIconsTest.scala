package io.udash.bootstrap.utils

import com.avsystem.commons.concurrent.RunNowEC
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.utils.UdashIcons.FontAwesome._
import io.udash.css.{CssStyle, CssView}
import io.udash.properties.single.Property
import io.udash.testing.AsyncUdashCoreFrontendTest
import org.scalajs.dom._
import org.scalatest.Succeeded
import scalatags.JsDom.all._

import scala.concurrent.Future

class UdashIconsTest extends AsyncUdashCoreFrontendTest with CssView {
  "Bootstrap icons" should {
    document.body.appendChild(UdashBootstrap.loadFontAwesome())

    "work with .styleIf" in {
      import UdashIcons.FontAwesome.Regular._
      val p: Property[Boolean] = Property(false)
      val el = span(
        addressBook.styleIf(p),
        angry.styleIf(p.transform(!_)),
        heart.styleIf(p)
      ).render

      Future.successful(assert(
        (Iterator(el.classList.length should be(2)) ++ (0 to 10).iterator.map { i =>
          p.set(!p.get)
          el.classList.length should be(if (i % 2 == 0) 3 else 2)
        }).forall(_ == Succeeded)
      ))
    }

    "apply appropriate FontAwesome Free unicodes to the :before pseudoselector" in {
      Future.sequence(
        Iterator(
          valuesOfType[CssStyle](Brands).iterator,
          valuesOfType[CssStyle](Regular).iterator,
          valuesOfType[CssStyle](Solid).iterator,
        ).flatten.map { iconStyle =>
          val icon = i(iconStyle).render
          document.body.appendChild(icon)

          (for {
            _ <- retrying {
              window.getComputedStyle(icon).fontFamily should (equal("\"Font Awesome 5 Free\"") or equal("\"Font Awesome 5 Brands\""))
            }
            r <- retrying {
              window.getComputedStyle(icon, ":before").content.replaceAll("\"|\'", "").head.toHexString should
                (startWith("f") and have length 4)
            }
          } yield r).andThen { case _ => document.body.removeChild(icon) }(RunNowEC)
        }
      ).map(assertions => assert(assertions.forall(_ == Succeeded)))
    }
  }
}
