package io.udash.bootstrap.pagination

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.html.Button

class UdashPaginationTest extends UdashFrontendTest {
  import org.scalajs.dom._

  import scalatags.JsDom.all._
  import scalacss.ScalatagsCss._

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
    val pager = UdashPagination.pager()(pages, selected)(UdashPagination.defaultPageFactory)

    val paginationElement = pagination.render
    val pagerElement = pager.render

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

    "disable arrow on edge element selection" in {
      showArrows.set(true)
      selected.set(0)
      checkDisabled(paginationElement, pagerElement)(true, false)
      selected.set(1)
      checkDisabled(paginationElement, pagerElement)(false, false)
      selected.set(pages.get.size - 1)
      checkDisabled(paginationElement, pagerElement)(false, true)
    }

    "update arrows on pages property changes" in {
      showArrows.set(true)
      selected.set(pages.get.size - 1)
      checkDisabled(paginationElement, pagerElement)(false, true)
      pages.remove(pages.get.size - 1, 1)
      checkDisabled(paginationElement, pagerElement)(false, true)
      pages.append(DefaultPage("999", Url("")))
      checkDisabled(paginationElement, pagerElement)(false, true)
      pages.append(DefaultPage("987", Url("")))
      checkDisabled(paginationElement, pagerElement)(false, false)
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
