package io.udash.bootstrap.pagination

import io.udash._
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

class UdashPaginationTest extends UdashFrontendTest {
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

      showArrows.set(false)
      highlightActive.set(true)
      for (i <- pages.get.indices) {
        selected.set(i)
        jQ(paginationElement).find("li").at(i).hasClass("active") should be(true)
      }
      highlightActive.set(false)
      for (i <- pages.get.indices) {
        selected.set(i)
        jQ(paginationElement).find("li").hasClass("active") should be(false)
      }
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
      pages.listenersCount() should be(0)
      pages.structureListenersCount() should be(0)
      pages.elemProperties.foreach(_.listenersCount() should be(0))
      selected.listenersCount() should be(0)
    }
  }

  def checkDisabled(els: Element*)(expectFirst: Boolean, expectLast: Boolean) = {
    els.foreach(el => {
      jQ(el).find("li").first().hasClass("disabled") should be(expectFirst)
      jQ(el).find("li").last().hasClass("disabled") should be(expectLast)
    })
  }
}
