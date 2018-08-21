package io.udash.bootstrap.tooltip

import io.udash._
import io.udash.i18n.{Bundle, BundleHash, Lang, LocalTranslationProvider, TranslationKey}
import io.udash.testing.AsyncUdashFrontendTest

import scala.concurrent.Future
import scala.util.Random

class TooltipTestUtils extends AsyncUdashFrontendTest {
  def tooltipTest(companion: TooltipUtils[_ <: Tooltip[_, _]], expectContent: Boolean): Unit = {
    "display translated content" in {
      import io.udash.wrappers.jquery._

      import scalatags.JsDom.all._

      val body = jQ("body")
      val item = button("btn").render
      body.append(item)

      val randMarker = Random.nextInt()
      implicit val lang = Property(Lang("test"))
      implicit val tp = new LocalTranslationProvider(
        Map(
          Lang("test") -> Bundle(BundleHash("h"), Map("a" -> s"$randMarker:AAA", "b" -> s"$randMarker:BBB")),
          Lang("test2") -> Bundle(BundleHash("h"), Map("a" -> s"$randMarker:ccc", "b" -> s"$randMarker:ddd"))
        )
      )

      val tooltip = companion.i18n(
        title = _ => TranslationKey.key("a"),
        content = _ => TranslationKey.key("b")
      )(item)

      def expectedText(): String =
        if (expectContent) s"$randMarker:AAA$randMarker:BBB"
        else s"$randMarker:AAA"

      def secondExpectedText(): String =
        if (expectContent) s"$randMarker:ccc$randMarker:ddd"
        else s"$randMarker:ccc"

      body.text().contains(expectedText()) should be(false)
      body.text().contains(secondExpectedText()) should be(false)

      for {
        _ <- Future(tooltip.show())
        _ <- retrying {
          body.text().contains(expectedText()) should be(true)
          body.text().contains(secondExpectedText()) should be(false)
        }

        _ <- Future(tooltip.hide())
        _ <- retrying {
          body.text().contains(expectedText()) should be(false)
          body.text().contains(secondExpectedText()) should be(false)
        }

        _ <- Future(lang.set(Lang("test2")))

        _ <- Future(tooltip.show())
        _ <- retrying {
          body.text().contains(expectedText()) should be(false)
          body.text().contains(secondExpectedText()) should be(true)
        }

        _ <- Future(tooltip.hide())
        r <- retrying {
          body.text().contains(expectedText()) should be(false)
          body.text().contains(secondExpectedText()) should be(false)
        }
      } yield r
    }
  }
}
