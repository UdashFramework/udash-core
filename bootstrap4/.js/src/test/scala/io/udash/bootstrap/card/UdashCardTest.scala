package io.udash.bootstrap.card

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import scalatags.JsDom.all._

class UdashCardTest extends UdashCoreFrontendTest {

  "UdashCard component" should {
    "clean up property listeners" in {
      val headText = Property("head")
      val bodyText = Property("body")
      val backgroundColor: Property[Option[BootstrapStyles.Color]] = Property(None)
      val borderColor: Property[Option[BootstrapStyles.Color]] = Property(None)
      val textAlignment: Property[Option[BootstrapStyles.Align]] = Property(None)
      val textColor: Property[Option[BootstrapStyles.Color]] = Property(None)
      val card = UdashCard(
        backgroundColor, borderColor, textAlignment, textColor
      ) { factory => Seq(
        factory.header(nested => nested(bind(headText))),
        factory.body { nested =>
          factory.body(_ => nested(bind(bodyText)))
        }
      )}
      val el = card.render
      el.childNodes.length should be(2)
      el.textContent should be("headbody")

      headText.set("test")
      bodyText.set("test2")
      el.textContent should be("testtest2")

      backgroundColor.set(Some(BootstrapStyles.Color.Primary))
      borderColor.set(Some(BootstrapStyles.Color.Secondary))
      textAlignment.set(Some(BootstrapStyles.Align.Right))
      textColor.set(Some(BootstrapStyles.Color.White))

      el.classList should contain("bg-primary")
      el.classList should contain("border-secondary")
      el.classList should contain("text-white")
      el.classList should contain("text-right")

      card.kill()
      headText.listenersCount() should be(0)
      bodyText.listenersCount() should be(0)
      backgroundColor.listenersCount() should be(0)
      borderColor.listenersCount() should be(0)
      textAlignment.listenersCount() should be(0)
      textColor.listenersCount() should be(0)
    }
  }
}
