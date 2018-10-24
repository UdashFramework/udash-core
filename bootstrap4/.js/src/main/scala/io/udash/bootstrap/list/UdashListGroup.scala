package io.udash.bootstrap.list

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom.Element

final class UdashListGroup[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  flush: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(itemFactory: (ElemType, Binding.NestedInterceptor) => Element) extends UdashBootstrapComponent {
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  override val render: Element =
    div(
      id := componentId, BootstrapStyles.ListGroup.listGroup,
      nestedInterceptor(BootstrapStyles.ListGroup.flush.styleIf(flush))
    )(
      nestedInterceptor(
        repeatWithNested(items) { (item, nested) =>
          itemFactory(item, nested).styles(BootstrapStyles.ListGroup.item)
        }
      )
    ).render
}

object UdashListGroup {
  /**
    * Creates a list group component, synchronised with a provided items sequence.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/list-group/">Bootstrap Docs</a>.
    *
    * @param items       Data items which will be represented as DOM elements in this group.
    * @param flush       If true, applies `list-group-flush` style.
    * @param componentId An id of the root DOM node.
    * @param itemFactory Creates DOM representation of the provided element.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashBreadcrumbs` component, call `render` to create a DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    flush: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(itemFactory: (ElemType, Binding.NestedInterceptor) => Element): UdashListGroup[ItemType, ElemType] =
    new UdashListGroup(items, flush, componentId)(itemFactory)
}