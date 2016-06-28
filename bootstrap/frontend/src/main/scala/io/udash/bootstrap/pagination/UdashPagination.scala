package io.udash.bootstrap
package pagination

import io.udash._
import org.scalajs.dom
import org.scalajs.dom.Event

import scala.concurrent.ExecutionContext

trait PaginationComponent[PageType, ElemType <: Property[PageType]] extends UdashBootstrapComponent {
  def pages: properties.SeqProperty[PageType, ElemType]

  def selectedPage: Property[Int]

  def changePage(pageIdx: Int): Unit = {
    import math._
    selectedPage.set(min(pages.get.size - 1, max(0, pageIdx)))
  }

  def next(): Unit = changePage(selectedPage.get + 1)
  def previous(): Unit = changePage(selectedPage.get - 1)
}

class UdashPagination[PageType, ElemType <: Property[PageType]] private
                     (size: PaginationSize, showArrows: Property[Boolean], highlightActive: Property[Boolean])
                     (val pages: properties.SeqProperty[PageType, ElemType], val selectedPage: Property[Int])
                     (itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element)(implicit ec: ExecutionContext)
  extends PaginationComponent[PageType, ElemType] {


  lazy val render: dom.Element = {
    import scalatags.JsDom.all._
    import scalatags.JsDom.tags2

    tags2.nav(
      ul(BootstrapStyles.Pagination.pagination)(
        arrow((idx, _) => idx <= 0, previous, UdashPagination.PreviousPage),
        repeat(pages)(page => {
          def currentIdx: Int = pages.elemProperties.indexOf(page)
          val pageIdx = Property[Int](currentIdx)
          pages.listen(_ => pageIdx.set(currentIdx))
          li(BootstrapStyles.active.styleIf(selectedPage.combine(pageIdx)(_ == _).combine(highlightActive)(_ && _)))(
            itemFactory(page, UdashPagination.StandardPage)
          )(onclick :+= ((_: Event) => { changePage(pageIdx.get); false })).render
        }),
        arrow((idx, size) => idx >= size - 1, next, UdashPagination.NextPage)
      )
    ).render
  }

  protected def arrow(highlightCond: (Int, Int) => Boolean, onClick: () => Any, buttonType: UdashPagination.ButtonType) = {
    import scalatags.JsDom.all._

    produce(pages.combine(showArrows)((_, _))) {
      case (_, true) =>
        val elements = pages.elemProperties
        li(BootstrapStyles.disabled.styleIf(selectedPage.transform(idx => highlightCond(idx, elements.size))))(
          produce(selectedPage)(idx => itemFactory(elements(math.min(elements.size - 1, idx + 1)), buttonType))
        )(onclick :+= ((_: Event) => { onClick(); false })).render
      case (_, false) =>
        span().render
    }
  }
}

class UdashPager[PageType, ElemType <: Property[PageType]] private[pagination](aligned: Boolean)
                (val pages: properties.SeqProperty[PageType, ElemType], val selectedPage: Property[Int])
                (itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element) extends PaginationComponent[PageType, ElemType] {

  lazy val render: dom.Element = {
    import scalatags.JsDom.all._
    import scalatags.JsDom.tags2

    tags2.nav(
      ul(BootstrapStyles.Pagination.pager)(
        arrow((idx, _) => idx <= 0, previous, UdashPagination.PreviousPage, BootstrapStyles.previous),
        arrow((idx, size) => idx >= size - 1, next, UdashPagination.NextPage, BootstrapStyles.next)
      )
    ).render
  }

  protected def arrow(highlightCond: (Int, Int) => Boolean, onClick: () => Any, buttonType: UdashPagination.ButtonType, alignStyle: BootstrapStyles.BootstrapClass) = {
    import scalatags.JsDom.all._

    produce(pages)(_ => {
      val elements = pages.elemProperties
      li(
        BootstrapStyles.disabled.styleIf(selectedPage.transform(idx => highlightCond(idx, elements.size))),
        alignStyle.styleIf(aligned)
      )(
        produce(selectedPage)(idx => itemFactory(elements(math.min(elements.size - 1, idx + 1)), buttonType))
      )(onclick :+= ((_: Event) => { onClick(); false })).render
    })
  }
}

object UdashPagination {
  import scalatags.JsDom.all._

  sealed trait ButtonType
  case object StandardPage extends ButtonType
  case object PreviousPage extends ButtonType
  case object NextPage extends ButtonType

  trait Page {
    def name: String
    def url: Url
  }
  case class DefaultPage(override val name: String, override val url: Url) extends Page

  private def bindHref(page: CastableProperty[Page]) =
    bindAttribute(page.asModel.subProp(_.url))((url, el) => el.setAttribute("href", url.value))

  val defaultPageFactory: (CastableProperty[Page], UdashPagination.ButtonType) => dom.Element = {
    case (page, UdashPagination.PreviousPage) =>
      a(aria.label := "Previous", bindHref(page))(span(aria.hidden := true)("«")).render
    case (page, UdashPagination.NextPage) =>
      a(aria.label := "Next", bindHref(page))(span(aria.hidden := true)("»")).render
    case (page, UdashPagination.StandardPage) =>
      a(bindHref(page))(bind(page.asModel.subProp(_.name))).render
  }

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def apply[PageType, ElemType <: Property[PageType]]
           (size: PaginationSize = PaginationSize.Default, showArrows: Property[Boolean] = Property(true), highlightActive: Property[Boolean] = Property(true))
           (pages: properties.SeqProperty[PageType, ElemType], selectedPage: Property[Int])
           (itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element)(implicit ec: ExecutionContext): UdashPagination[PageType, ElemType] =
    new UdashPagination(size, showArrows, highlightActive)(pages, selectedPage)(itemFactory)

  def pager[PageType, ElemType <: Property[PageType]]
           (aligned: Boolean = false)(pages: properties.SeqProperty[PageType, ElemType], selectedPage: Property[Int])
           (itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element)(implicit ec: ExecutionContext): UdashPager[PageType, ElemType] =
    new UdashPager(aligned)(pages, selectedPage)(itemFactory)
}