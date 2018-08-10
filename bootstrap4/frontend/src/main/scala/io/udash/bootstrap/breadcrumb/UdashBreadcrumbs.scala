package io.udash.bootstrap.breadcrumb

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
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
            li(nested(BootstrapStyles.active.styleIf(page.transform(isActive))))(
              itemFactory(page, nested)
            ).render
          }
        )
      )
    ).render
  }
}

object UdashBreadcrumbs {
  import scalatags.JsDom.all._

  /** Default breadcrumb model. */
  class Breadcrumb(val name: String, val link: String)

  /** Default breadcrumb model factory. */
  val defaultPageFactory: (ReadableProperty[Breadcrumb], Binding.NestedInterceptor) => Modifier = {
    (page, nested) => nested(produce(page) { page =>
      a(href := page.link)(page.name).render
    })
  }

  /**
    * Creates breadcrumbs component, synchronised with provided SeqProperty.
    * More: <a href="http://getbootstrap.com/javascript/#breadcrumbs">Bootstrap Docs</a>.
    *
    * @param pages       SeqProperty containing breadcrumbs data.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM representation for provided item.
    * @param isActive  Decides whether an element has addition `active` style class.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    pages: seq.ReadableSeqProperty[ItemType, ElemType],
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: (ElemType, Binding.NestedInterceptor) => Modifier,
    isActive: ItemType => Boolean = (_: ItemType) => false
  ): UdashBreadcrumbs[ItemType, ElemType] = {
    new UdashBreadcrumbs(pages, componentId)(itemFactory, isActive)
  }

  /**
    * Creates breadcrumbs component, synchronised with provided SeqProperty based on the default model with name and link.
    * More: <a href="http://getbootstrap.com/javascript/#breadcrumbs">Bootstrap Docs</a>.
    *
    * @param pages       SeqProperty containing breadcrumbs data.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM representation for provided item.
    * @param isActive  Decides whether an element has addition `active` style class.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def default(
    pages: ReadableSeqProperty[Breadcrumb],
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: (ReadableProperty[Breadcrumb], Binding.NestedInterceptor) => Modifier = defaultPageFactory,
    isActive: Breadcrumb => Boolean = (_: Breadcrumb) => false
  ): UdashBreadcrumbs[Breadcrumb, ReadableProperty[Breadcrumb]] = {
    new UdashBreadcrumbs(pages, componentId)(itemFactory, isActive)
  }

  /**
    * Creates breadcrumbs component, synchronised with provided SeqProperty based on a simple string elements.
    * More: <a href="http://getbootstrap.com/javascript/#breadcrumbs">Bootstrap Docs</a>.
    *
    * @param pages       SeqProperty containing breadcrumbs data.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM representation for provided item.
    * @param isActive  Decides whether an element has addition `active` style class.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def text(
    pages: ReadableSeqProperty[String],
    componentId: ComponentId = ComponentId.newId()
  )(
    itemFactory: (ReadableProperty[String], Binding.NestedInterceptor) => Modifier = (p, nested) => nested(bind(p)),
    isActive: String => Boolean = (_: String) => false
  ): UdashBreadcrumbs[String, ReadableProperty[String]] = {
    new UdashBreadcrumbs(pages, componentId)(itemFactory, isActive)
  }
}
