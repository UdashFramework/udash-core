package io.udash.bootstrap.tooltip

import io.udash._
import io.udash.i18n.{Bundle, BundleHash, Lang, LocalTranslationProvider, TranslationKey}
import io.udash.testing.AsyncUdashCoreFrontendTest

import scala.concurrent.Future
import scala.util.Random

class TooltipTestUtils extends AsyncUdashCoreFrontendTest {
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

      body.text() shouldNot include(expectedText())
      body.text() shouldNot include(secondExpectedText())

      for {
        _ <- Future(tooltip.show())
        _ <- retrying {
          body.text() should include(expectedText())
          body.text() shouldNot include(secondExpectedText())
        }

        _ <- Future(tooltip.hide())
        _ <- retrying {
          body.text() shouldNot include(expectedText())
          body.text() shouldNot include(secondExpectedText())
        }

        _ <- Future(lang.set(Lang("test2")))

        _ <- Future(tooltip.show())
        _ <- retrying {
          body.text() shouldNot include(expectedText())
          body.text() should include(secondExpectedText())
        }

        _ <- Future(tooltip.hide())
        r <- retrying {
          body.text() shouldNot include(expectedText())
          body.text() shouldNot include(secondExpectedText())
        }
      } yield r
    }
  }
}
