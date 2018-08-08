package io.udash.bootstrap
package pagination

import com.avsystem.commons.misc.{AbstractValueEnum, EnumCtx, ValueEnumCompanion}
import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.{HasModelPropertyCreator, ModelPropertyCreator, seq}
import org.scalajs.dom
import org.scalajs.dom.Event

sealed trait PaginationComponent[PageType, ElemType <: ReadableProperty[PageType]] extends UdashBootstrapComponent {
  /** Sequence of pagination elements. Pagination will automatically synchronize with this property changes. */
  def pages: seq.ReadableSeqProperty[PageType, ElemType]

  /** Index of selected page. */
  def selectedPage: Property[Int]

  /** Safely set selected page to the provided index. Will change index if it is out of bounds. */
  def changePage(pageIdx: Int): Unit = {
    import math._
    selectedPage.set(min(pages.get.size - 1, max(0, pageIdx)))
  }

  /** Safely selects the next page. */
  def next(): Unit = changePage(selectedPage.get + 1)

  /** Safely selects the previous page. */
  def previous(): Unit = changePage(selectedPage.get - 1)
}

final class UdashPagination[PageType : ModelPropertyCreator, ElemType <: ReadableProperty[PageType]] private(
  paginationSize: PaginationSize, showArrows: ReadableProperty[Boolean],
  highlightActive: ReadableProperty[Boolean], override val componentId: ComponentId
)(
  val pages: seq.ReadableSeqProperty[PageType, ElemType],
  val selectedPage: Property[Int]
)(
  itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element
)
  extends PaginationComponent[PageType, ElemType] {

  import io.udash.css.CssView._

  override val render: dom.Element = {
    import scalatags.JsDom.all._
    import scalatags.JsDom.tags2

    tags2.nav(
      ul(id := componentId, BootstrapStyles.Pagination.pagination, paginationSize)(
        arrow((idx, _) => idx <= 0, previous _, UdashPagination.ButtonType.PreviousPage),
        repeat(pages)(page => {
          def currentIdx: Int = pages.elemProperties.indexOf(page)
          val pageIdx = Property[Int](currentIdx)
          pages.listen(_ => pageIdx.set(currentIdx))
          li(BootstrapStyles.active.styleIf(selectedPage.combine(pageIdx)(_ == _).combine(highlightActive)(_ && _)))(
            itemFactory(page, UdashPagination.ButtonType.StandardPage)
          )(onclick :+= ((_: Event) => { changePage(pageIdx.get); false })).render
        }),
        arrow((idx, size) => idx >= size - 1, next _, UdashPagination.ButtonType.NextPage)
      )
    ).render
  }

  protected def arrow(highlightCond: (Int, Int) => Boolean, onClick: () => Any, buttonType: UdashPagination.ButtonType) = {
    import scalatags.JsDom.all._

    produce(showArrows.combine(pages)((_, _))) {
      case (true, _) =>
        val elements = pages.elemProperties
        li(BootstrapStyles.disabled.styleIf(selectedPage.transform((idx: Int) => highlightCond(idx, elements.size))))(
          produce(selectedPage)(idx => itemFactory(elements(math.min(elements.size - 1, idx + 1)), buttonType))
        )(onclick :+= ((_: Event) => { onClick(); false })).render
      case (false, _) =>
        span().render
    }
  }
}

object UdashPagination {
  import scalatags.JsDom.all._

  final class ButtonType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ButtonType extends ValueEnumCompanion[ButtonType] {
    final val StandardPage, PreviousPage, NextPage: Value = new ButtonType
  }

  /** Default pagination element model. */
  trait Page {
    def name: String
    def url: Url
  }
  object Page extends HasModelPropertyCreator[Page]

  case class DefaultPage(override val name: String, override val url: Url) extends Page
  object DefaultPage extends HasModelPropertyCreator[DefaultPage]

  private def bindHref(page: ModelProperty[Page]) =
    href.bind(page.subProp(_.url.value))

  /** Creates link for default pagination element model. */
  val defaultPageFactory: (CastableProperty[Page], UdashPagination.ButtonType) => dom.Element = {
    case (page, UdashPagination.ButtonType.PreviousPage) =>
      a(aria.label := "Previous", bindHref(page.asModel))(span(aria.hidden := true)("«")).render
    case (page, UdashPagination.ButtonType.NextPage) =>
      a(aria.label := "Next", bindHref(page.asModel))(span(aria.hidden := true)("»")).render
    case (page, _) => // default: UdashPagination.ButtonType.StandardPage
      a(bindHref(page.asModel))(bind(page.asModel.subProp(_.name))).render
  }

  /**
    * Creates default pagination with pages display. More: <a href="http://getbootstrap.com/components/#pagination">Bootstrap Docs</a>.
    *
    * @param size            Pagination component size.
    * @param showArrows      If property value is true, shows next/prev page arrows.
    * @param highlightActive If property value is true, highlights selected page.
    * @param componentId     Id of the root DOM node.
    * @param pages           Sequence of available pages.
    * @param selectedPage    Property containing selected page index.
    * @param itemFactory     Creates button for element in pagination.
    * @tparam PageType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashPagination` component, call render to create DOM element.
    */
  def apply[PageType : ModelPropertyCreator, ElemType <: ReadableProperty[PageType]](
    size: PaginationSize = PaginationSize.Default, showArrows: ReadableProperty[Boolean] = Property(true),
    highlightActive: ReadableProperty[Boolean] = Property(true), componentId: ComponentId = UdashBootstrap.newId()
  )(
    pages: seq.ReadableSeqProperty[PageType, ElemType],
    selectedPage: Property[Int]
  )(
    itemFactory: (ElemType, UdashPagination.ButtonType) => dom.Element
  ): UdashPagination[PageType, ElemType] =
    new UdashPagination(size, showArrows, highlightActive, componentId)(pages, selectedPage)(itemFactory)
}