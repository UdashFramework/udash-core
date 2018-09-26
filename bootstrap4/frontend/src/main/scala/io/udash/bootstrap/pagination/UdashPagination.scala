package io.udash.bootstrap
package pagination

import com.avsystem.commons.misc.{AbstractValueEnum, EnumCtx, ValueEnumCompanion}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import io.udash.properties.{PropertyCreator, seq}
import org.scalajs.dom.Element
import org.scalajs.dom.Event
import scalatags.JsDom.all._

final class UdashPagination[PageType : PropertyCreator, ElemType <: ReadableProperty[PageType]] private(
  pages: seq.ReadableSeqProperty[PageType, ElemType],
  selectedPageIdx: Property[Int],
  paginationSize: ReadableProperty[Option[BootstrapStyles.Size]],
  showArrows: ReadableProperty[Boolean],
  highlightActive: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  itemFactory: (ElemType, UdashPagination.ButtonType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier
) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  // keep track of pages sequence changes and update selected page
  propertyListeners += pages.listenStructure { patch =>
    if (patch.idx <= selectedPageIdx.get && patch.idx + patch.removed.size > selectedPageIdx.get) {
      selectedPageIdx.set(math.min(patch.idx, pages.size - 1))
    } else if (patch.idx <= selectedPageIdx.get && patch.idx + patch.removed.size <= selectedPageIdx.get) {
      selectedPageIdx.set(selectedPageIdx.get - patch.removed.size + patch.added.size)
    }
  }

  val selectedPage: ReadableProperty[PageType] = {
    selectedPageIdx.combine(pages)((idx, pages) => pages(idx))
  }

  /** Safely set selected page to the provided index.
    * It will select first/last index if the provided value is out of bounds. */
  def changePage(pageIdx: Int): Unit = {
    selectedPageIdx.set(math.min(pages.get.size - 1, math.max(0, pageIdx)))
  }

  /** Safely selects the next page. */
  def next(): Unit = changePage(selectedPageIdx.get + 1)

  /** Safely selects the previous page. */
  def previous(): Unit = changePage(selectedPageIdx.get - 1)

  override val render: Element = {
    import scalatags.JsDom.tags2

    tags2.nav(
      ul(
        id := componentId, BootstrapStyles.Pagination.pagination,
        nestedInterceptor((BootstrapStyles.Pagination.size _).reactiveOptionApply(paginationSize))
      )(
        nestedInterceptor(
          arrow((idx, _) => idx <= 0, previous _, UdashPagination.ButtonType.PreviousPage)
        ),
        nestedInterceptor(
          repeatWithIndex(pages) { (page, idx, nested) =>
            li(
              BootstrapStyles.Pagination.item,
              nested(BootstrapStyles.active.styleIf(
                selectedPageIdx.combine(idx)(_ == _).combine(highlightActive)(_ && _)
              ))
            )(
              span(BootstrapStyles.Pagination.link)(
                itemFactory(page, UdashPagination.ButtonType.StandardPage, idx, nested)
              )
            )(onclick :+= ((_: Event) => { changePage(idx.get); false })).render
          }
        ),
        nestedInterceptor(
          arrow((idx, size) => idx >= size - 1, next _, UdashPagination.ButtonType.NextPage)
        )
      )
    ).render
  }

  protected def arrow(highlightCond: (Int, Int) => Boolean, onClick: () => Any, buttonType: UdashPagination.ButtonType): Binding = {
    import scalatags.JsDom.all._

    produceWithNested(showArrows) {
      case (true, nested) =>
        val elements = pages.elemProperties
        li(
          nested(BootstrapStyles.disabled.styleIf(
            selectedPageIdx.combine(pages)((selected, pages) => highlightCond(selected, pages.size))
          ))
        )(
          nested(produceWithNested(selectedPageIdx) { (idx, nested) =>
            span(BootstrapStyles.Pagination.link)(
              itemFactory(elements(math.min(elements.size - 1, idx + 1)), buttonType, (-1).toProperty, nested)
            ).render
          })
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

  /** Creates label based on actual page idx. */
  def defaultPageFactory[ElemType]: (ElemType, UdashPagination.ButtonType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier = {
    case (_, UdashPagination.ButtonType.PreviousPage, _, _) =>
      span(aria.label := "Previous")(span(aria.hidden := true)("«"))
    case (_, UdashPagination.ButtonType.NextPage, _, _) =>
      span(aria.label := "Next")(span(aria.hidden := true)("»"))
    case (_, _, idx, nested) => // default: UdashPagination.ButtonType.StandardPage
      span(nested(bind(idx.transform(_ + 1))))
  }

  /**
    * Creates pagination component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/pagination/">Bootstrap Docs</a>.
    *
    * @param pages           Sequence of available pages.
    * @param selectedPageIdx A property containing selected page index.
    * @param paginationSize  A pagination component size.
    * @param showArrows      If property value is true, shows next/prev page arrows.
    * @param highlightActive If property value is true, highlights selected page.
    * @param componentId     An id of the root DOM node.
    * @param itemFactory     Creates button for each element in `pages`.
    *                        The factory gets an element property, type and index as arguments.
    *                        Use the provided interceptor to properly clean up bindings inside the content.
    * @tparam PageType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashPagination` component, call `render` to create a DOM element.
    */
  def apply[PageType: PropertyCreator, ElemType <: ReadableProperty[PageType]](
    pages: seq.ReadableSeqProperty[PageType, ElemType],
    selectedPageIdx: Property[Int],
    paginationSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    showArrows: ReadableProperty[Boolean] = UdashBootstrap.True,
    highlightActive: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: (ElemType, ButtonType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier = defaultPageFactory
  ): UdashPagination[PageType, ElemType] = {
    new UdashPagination(pages, selectedPageIdx, paginationSize, showArrows, highlightActive, componentId)(itemFactory)
  }
}