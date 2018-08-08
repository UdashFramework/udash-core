package io.udash.bootstrap.pagination

import io.udash._
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom

class UdashPaginationTest extends UdashFrontendTest {
  "UdashPagination component" should {
    import UdashPagination._

    val showArrows = Property(true)
    val highlightActive = Property(true)

    val selected = Property(0)
    val pages = SeqProperty(Seq.tabulate[Page](7)(idx =>
      DefaultPage((idx+1).toString, Url(""))
    ))

    val pagination = UdashPagination(
      showArrows = showArrows, highlightActive = highlightActive
    )(pages, selected)(UdashPagination.defaultPageFactory)

    val paginationElement = pagination.render

    "show and hide arrows on property change" in {
      showArrows.set(true)
      jQ(paginationElement).find("li").length should be(pages.get.size + 2)
      showArrows.set(false)
      jQ(paginationElement).find("li").length should be(pages.get.size)
    }

    "show and hide highlight on property change" in {
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
      showArrows.set(false)
      highlightActive.set(true)
      selected.set(pages.get.size - 1)
      jQ(paginationElement).find("li").at(pages.get.size - 1).hasClass("active") should be(true)
      pages.insert(pages.get.size - 1, DefaultPage("999", Url("")))
      jQ(paginationElement).find("li").at(pages.get.size - 2).hasClass("active") should be(true)
      jQ(paginationElement).find("li").at(pages.get.size - 1).hasClass("active") should be(false)
    }
  }

  def checkDisabled(els: dom.Element*)(expectFirst: Boolean, expectLast: Boolean) = {
    els.foreach(el => {
      jQ(el).find("li").first().hasClass("disabled") should be(expectFirst)
      jQ(el).find("li").last().hasClass("disabled") should be(expectLast)
    })
  }
}
