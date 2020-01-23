package io.udash.bootstrap
package pagination

import com.avsystem.commons.misc.{AbstractValueEnum, EnumCtx, ValueEnumCompanion}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.i18n.{LangProperty, TranslationKey0, TranslationProvider}
import io.udash.properties.seq
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._

final class UdashPagination[PageType, ElemType <: ReadableProperty[PageType]] private(
  pages: seq.ReadableSeqProperty[PageType, ElemType],
  selectedPageIdx: Property[Int],
  paginationSize: ReadableProperty[Option[BootstrapStyles.Size]],
  showArrows: ReadableProperty[Boolean],
  highlightActive: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  itemFactory: (ElemType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier,
  arrowFactory: (ElemType, UdashPagination.ArrowType, Binding.NestedInterceptor) => Modifier,
  additionalListModifiers: Binding.NestedInterceptor => Modifier
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
        componentId, BootstrapStyles.Pagination.pagination,
        nestedInterceptor((BootstrapStyles.Pagination.size _).reactiveOptionApply(paginationSize)),
        additionalListModifiers(nestedInterceptor)
      )(
        nestedInterceptor(
          arrow((idx, _) => idx <= 0, previous _, UdashPagination.ArrowType.PreviousPage)
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
                itemFactory(page, idx, nested)
              )
            )(onclick :+= ((_: Event) => changePage(idx.get))).render
          }
        ),
        nestedInterceptor(
          arrow((idx, size) => idx >= size - 1, next _, UdashPagination.ArrowType.NextPage)
        )
      )
    ).render
  }

  protected def arrow(highlightCond: (Int, Int) => Boolean, onClick: () => Any, buttonType: UdashPagination.ArrowType): Binding = {
    import scalatags.JsDom.all._

    produceWithNested(showArrows) {
      case (true, nested) =>
        val elements = pages.elemProperties
        li(
          BootstrapStyles.Pagination.item,
          nested(BootstrapStyles.disabled.styleIf(
            selectedPageIdx.combine(pages)((selected, pages) => highlightCond(selected, pages.size))
          ))
        )(
          nested(produceWithNested(selectedPageIdx) { (idx, nested) =>
            span(BootstrapStyles.Pagination.link)(
              arrowFactory(elements(math.min(elements.size - 1, idx + 1)), buttonType, nested)
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

  final class ArrowType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ArrowType extends ValueEnumCompanion[ArrowType] {
    final val PreviousPage, NextPage: Value = new ArrowType
  }

  /** Creates label based on actual page idx. */
  def defaultPageFactory[ElemType]: (ElemType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier =
    (_, idx, nested) => span(nested(bind(idx.transform(_ + 1))))

  /**
    * Creates standard arrows.
    *
    * @param srTexts Translation keys for previous and next arrows aria.label texts.
    */
  def defaultArrowFactory[ElemType](
    srTexts: Option[(TranslationKey0, TranslationKey0, LangProperty, TranslationProvider)] = None
  ): (ElemType, UdashPagination.ArrowType, Binding.NestedInterceptor) => Modifier =
    (_, arrowType, nested) => {
      if (arrowType == UdashPagination.ArrowType.PreviousPage) {
        span(
          srTexts.map { case (previous, _, lang, provider) =>
            import io.udash.i18n._
            nested(
              translatedAttrDynamic(previous, aria.label.name)(_.apply()(provider, lang.get))(lang)
            ): Modifier
          }.getOrElse(aria.label := "Previous")
        )(span(aria.hidden := true)("«"))
      } else {
        span(
          srTexts.map { case (_, next, lang, provider) =>
            import io.udash.i18n._
            nested(
              translatedAttrDynamic(next, aria.label.name)(_.apply()(provider, lang.get))(lang)
            ): Modifier
          }.getOrElse(aria.label := "Next")
        )(span(aria.hidden := true)("»"))
      }
    }

  /**
    * Creates pagination component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/pagination/">Bootstrap Docs</a>.
    *
    * @param pages                   Sequence of available pages.
    * @param selectedPageIdx         A property containing selected page index.
    * @param paginationSize          A pagination component size.
    * @param showArrows              If property value is true, shows next/prev page arrows.
    * @param highlightActive         If property value is true, highlights selected page.
    * @param componentId             An id of the root DOM node.
    * @param itemFactory             Creates button for each element in `pages`.
    *                                The factory gets an element property and index as arguments.
    *                                Use the provided interceptor to properly clean up bindings inside the content.
    * @param arrowFactory            Creates button for the pagination arrows.
    *                                The factory gets an element property and arrow type as arguments.
    *                                Use the provided interceptor to properly clean up bindings inside the content.
    * @param additionalListModifiers Additional modifiers of the `ul` element.
    *                                You can pass `BootstrapStyles.Flex.justifyContentCenter()`
    *                                here to center the pagination component.
    * @tparam PageType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashPagination` component, call `render` to create a DOM element.
    */
  def apply[PageType, ElemType <: ReadableProperty[PageType]](
    pages: seq.ReadableSeqProperty[PageType, ElemType],
    selectedPageIdx: Property[Int],
    paginationSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    showArrows: ReadableProperty[Boolean] = UdashBootstrap.True,
    highlightActive: ReadableProperty[Boolean] = UdashBootstrap.True,
    componentId: ComponentId = ComponentId.generate()
  )(
    itemFactory: (ElemType, ReadableProperty[Int], Binding.NestedInterceptor) => Modifier = defaultPageFactory,
    arrowFactory: (ElemType, UdashPagination.ArrowType, Binding.NestedInterceptor) => Modifier = defaultArrowFactory(),
    additionalListModifiers: Binding.NestedInterceptor => Modifier = _ => ()
  ): UdashPagination[PageType, ElemType] = {
    new UdashPagination(
      pages, selectedPageIdx, paginationSize, showArrows, highlightActive, componentId
    )(itemFactory, arrowFactory, additionalListModifiers)
  }
}