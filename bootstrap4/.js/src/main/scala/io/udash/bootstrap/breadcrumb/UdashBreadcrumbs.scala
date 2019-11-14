package io.udash.bootstrap
package breadcrumb

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom.Element
import scalatags.JsDom.all.Modifier

final class UdashBreadcrumbs[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  pages: seq.ReadableSeqProperty[ItemType, ElemType],
  override val componentId: ComponentId
)(
  itemFactory: (ElemType, Binding.NestedInterceptor) => Modifier,
  isActive: ItemType => Boolean
) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element = {
    import scalatags.JsDom.all._
    import scalatags.JsDom.tags2.nav

    nav(id := componentId, aria.label := "breadcrumb")(
      ol(BootstrapStyles.Navigation.breadcrumb)(
        nestedInterceptor(
          repeatWithNested(pages) { case (page, nested) =>
            li(
              BootstrapStyles.Navigation.breadcrumbItem,
              nested(BootstrapStyles.active.styleIf(page.transform(isActive)))
            )(itemFactory(page, nested)).render
          }
        )
      )
    ).render
  }
}

object UdashBreadcrumbs {
  import scalatags.JsDom.all._

  /** A default breadcrumb model. */
  class Breadcrumb(val name: String, val link: Url)

  /** A default breadcrumb component factory. */
  val defaultPageFactory: (ReadableProperty[Breadcrumb], Binding.NestedInterceptor) => Modifier = {
    (page, nested) => nested(produce(page) { page =>
      a(href := page.link)(page.name).render
    })
  }

  /**
    * Creates a breadcrumbs component, synchronised with a provided SeqProperty.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/breadcrumb/">Bootstrap Docs</a>.
    *
    * @param items       The SeqProperty containing breadcrumbs data.
    * @param componentId An id of the root DOM node.
    * @param itemFactory Creates a DOM representation for an item from the `items` property.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @param isActive    Decides whether an element has an additional `active` style class.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashBreadcrumbs` component, call `render` to create a DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    componentId: ComponentId = ComponentId.generate()
  )(
    itemFactory: (ElemType, Binding.NestedInterceptor) => Modifier,
    isActive: ItemType => Boolean = (_: ItemType) => false
  ): UdashBreadcrumbs[ItemType, ElemType] = {
    new UdashBreadcrumbs(items, componentId)(itemFactory, isActive)
  }

  /**
    * Creates breadcrumbs component, synchronised with a provided SeqProperty. Based on the default model with a name and a link.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/breadcrumb/">Bootstrap Docs</a>.
    *
    * @param items       The SeqProperty containing breadcrumbs data.
    * @param componentId An id of the root DOM node.
    * @param itemFactory Creates a DOM representation for an item from the `items` property.
    * @param isActive    Decides whether an element has an additional `active` style class.
    * @return A `UdashBreadcrumbs` component, call `render` to create a DOM element.
    */
  def default(
    items: ReadableSeqProperty[Breadcrumb],
    componentId: ComponentId = ComponentId.generate()
  )(
    itemFactory: (ReadableProperty[Breadcrumb], Binding.NestedInterceptor) => Modifier = defaultPageFactory,
    isActive: Breadcrumb => Boolean = (_: Breadcrumb) => false
  ): UdashBreadcrumbs[Breadcrumb, ReadableProperty[Breadcrumb]] = {
    new UdashBreadcrumbs(items, componentId)(itemFactory, isActive)
  }

  /**
    * Creates breadcrumbs component, synchronised with provided SeqProperty. Based on a simple string elements.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/breadcrumb/">Bootstrap Docs</a>.
    *
    * @param items       The SeqProperty containing breadcrumbs data.
    * @param componentId An id of the root DOM node.
    * @param itemFactory Creates a DOM representation for an item from the `items` property.
    * @param isActive    Decides whether an element has an additional `active` style class.
    * @return A `UdashBreadcrumbs` component, call `render` to create a DOM element.
    */
  def text(
    items: ReadableSeqProperty[String],
    componentId: ComponentId = ComponentId.generate()
  )(
    itemFactory: (ReadableProperty[String], Binding.NestedInterceptor) => Modifier = (p, nested) => nested(bind(p)),
    isActive: String => Boolean = (_: String) => false
  ): UdashBreadcrumbs[String, ReadableProperty[String]] = {
    new UdashBreadcrumbs(items, componentId)(itemFactory, isActive)
  }
}
