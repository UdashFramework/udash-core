package io.udash.bootstrap.list

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.{BootstrapStyles, ComponentId, UdashBootstrap, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom
import org.scalajs.dom.Element

final class UdashListGroup[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  flush: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(itemFactory: (ElemType, Binding.NestedInterceptor) => dom.Element) extends UdashBootstrapComponent {
  // TODO replace all `dom.Element` with `Element`

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
    * Creates list group component, synchronised with provided [[SeqProperty]].
    * More: <a href="http://getbootstrap.com/javascript/#list-group">Bootstrap Docs</a>.
    *
    * @param items SeqProperty containing list element data.
    * @param flush If true, applies `list-group-flush` style.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM representation for provided item.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    flush: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(itemFactory: (ElemType, Binding.NestedInterceptor) => dom.Element): UdashListGroup[ItemType, ElemType] =
    new UdashListGroup(items, flush, componentId)(itemFactory)
}