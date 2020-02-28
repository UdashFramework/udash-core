package io.udash.bootstrap.pagination

import com.avsystem.commons._
import io.udash._
import io.udash.testing.AsyncUdashCoreFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.concurrent.Future

class UdashPaginationTest extends AsyncUdashCoreFrontendTest {
  "UdashPagination component" should {
    "show and hide arrows on property change" in {
      val showArrows = Property(true)
      val highlightActive = Property(true)

      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val pagination = UdashPagination(
        pages, selected, showArrows = showArrows, highlightActive = highlightActive
      )()

      val paginationElement = pagination.render

      showArrows.set(true)
      jQ(paginationElement).find("li").length should be(pages.get.size + 2)
      showArrows.set(false)
      jQ(paginationElement).find("li").length should be(pages.get.size)
    }

    "enable/disable arrows on a page change and pages list change" in {
      val showArrows = Property(true)
      val highlightActive = Property(true)

      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val pagination = UdashPagination(
        pages, selected, showArrows = showArrows, highlightActive = highlightActive
      )()

      val paginationElement = pagination.render

      jQ(paginationElement).find("li").first().hasClass("disabled") should be(true)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pagination.next()
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pagination.previous()
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(true)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.prepend(99)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.remove(0, 1)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(true)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      selected.set(6)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(true)

      pagination.previous()
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pagination.next()
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(true)

      pages.append(99)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.remove(6, 1)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(true)

      selected.set(3)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.remove(1, 1)
      selected.get should be(2)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.remove(3, 1)
      selected.get should be(2)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(false)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(false)

      pages.remove(1, 4)
      selected.get should be(0)
      jQ(paginationElement).find("li").first().hasClass("disabled") should be(true)
      jQ(paginationElement).find("li").last().hasClass("disabled") should be(true)
    }

    "show and hide highlight on property change" in {
      val showArrows = Property(true)
      val highlightActive = Property(true)

      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val pagination = UdashPagination(
        pages, selected, showArrows = showArrows, highlightActive = highlightActive
      )()

      val paginationElement = pagination.render

      for {
        _ <- Future {
          showArrows.set(false)
          highlightActive.set(true)
        }
        _ <- retrying {
          for (i <- pages.get.indices) {
            selected.set(i)
            jQ(paginationElement).find("li").at(i).hasClass("active") should be(true)
          }
        }
        _ <- Future {
          highlightActive.set(false)
        }
        r <- retrying {
          for (i <- pages.get.indices) {
            selected.set(i)
            jQ(paginationElement).find("li").hasClass("active") should be(false)
          }
        }
      } yield r
    }

    "update highlight on pages property changes" in {
      val showArrows = Property(true)
      val highlightActive = Property(true)

      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val pagination = UdashPagination(
        pages, selected, showArrows = showArrows, highlightActive = highlightActive
      )()

      val paginationElement = pagination.render

      showArrows.set(false)
      highlightActive.set(true)
      selected.set(pages.get.size - 1)
      jQ(paginationElement).find("li").at(pages.get.size - 1).hasClass("active") should be(true)
      pages.insert(pages.get.size - 1, 123)
      jQ(paginationElement).find("li").at(pages.get.size - 2).hasClass("active") should be(false)
      jQ(paginationElement).find("li").at(pages.get.size - 1).hasClass("active") should be(true)
    }

    "translate aria labels of arrows" in {
      import io.udash.i18n._

      implicit val tp = new LocalTranslationProvider(
        Map(
          Lang("test") -> Bundle(BundleHash("h"), Map("prev" -> "Poprzedni", "next" -> "Następny")),
          Lang("test2") -> Bundle(BundleHash("h"), Map("prev" -> "Prev", "next" -> "next"))
        )
      )
      val lang: LangProperty = Property(Lang("test"))

      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val previous = Property.blank[String]
      val next = Property.blank[String]

      val pagination = UdashPagination(pages, selected)(
        arrowFactory = UdashPagination.defaultArrowFactory(Some((previous, next)))
      ).setup(_.addRegistration(
        lang.listen(implicit lang =>
          for {
            p <- TranslationKey.key("prev")()
            n <- TranslationKey.key("next")()
          } yield CallbackSequencer().sequence {
            previous.set(p.string)
            next.set(n.string)
          },
          initUpdate = true)
      ))
      val el = pagination.render

      import scalatags.JsDom.all._
      for {
        _ <- retrying {
          el.getElementsByTagName("li")(0).firstElementChild.firstElementChild.getAttribute(aria.label.name) should be("Poprzedni")
          el.getElementsByTagName("li")(pages.size + 1).firstElementChild.firstElementChild.getAttribute(aria.label.name) should be("Następny")
        }
        _ <- Future {
          lang.set(Lang("test2"))
        }
        _ <- retrying {
          el.getElementsByTagName("li")(0).firstElementChild.firstElementChild.getAttribute(aria.label.name) should be("Prev")
          el.getElementsByTagName("li")(pages.size + 1).firstElementChild.firstElementChild.getAttribute(aria.label.name) should be("next")
        }
        _ <- Future {
          pagination.kill()
        }
        r <- retrying {
          lang.listenersCount() should be(0)
        }
      } yield r
    }

    "update on data properties update and clean up listeners properly" in {
      val selected = Property(0)
      val pages = SeqProperty(Seq.tabulate[Int](7)(identity))

      val pagination = UdashPagination(pages, selected, showArrows = false.toProperty) {
        (v, _, nested) =>
          import scalatags.JsDom.all._
          span(nested(bind(v)))
      }
      val paginationElement = pagination.render

      paginationElement.textContent should be("0123456")

      pages.append(7)
      paginationElement.textContent should be("01234567")

      pages.remove(2, 3)
      paginationElement.textContent should be("01567")

      pages.elemProperties(2).set(2)
      paginationElement.textContent should be("01267")

      pagination.kill()
      ensureNoListeners(pages)
      selected.listenersCount() should be(0)
    }
  }

  def checkDisabled(els: Element*)(expectFirst: Boolean, expectLast: Boolean): Unit = {
    els.foreach(el => {
      jQ(el).find("li").first().hasClass("disabled") should be(expectFirst)
      jQ(el).find("li").last().hasClass("disabled") should be(expectLast)
    })
  }
}
