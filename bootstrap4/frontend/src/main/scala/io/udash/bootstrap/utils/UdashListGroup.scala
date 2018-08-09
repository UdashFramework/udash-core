package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.ComponentId
import io.udash.properties.seq
import org.scalajs.dom
import org.scalajs.dom.Element

final class UdashListGroup[ItemType, ElemType <: ReadableProperty[ItemType]] private
                          (items: seq.ReadableSeqProperty[ItemType, ElemType], override val componentId: ComponentId)
                          (body: (ElemType) => dom.Element)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  import scalatags.JsDom.all._

  override val render: Element =
    ul(id := componentId, BootstrapStyles.ListGroup.listGroup)(
      repeat(items)(item => {
        val el = body(item)
        BootstrapStyles.ListGroup.item.addTo(el)
        el
      })
    ).render
}

object UdashListGroup {
  /**
    * Creates list group component, synchronised with provided [[SeqProperty]].
    * More: <a href="http://getbootstrap.com/javascript/#list-group">Bootstrap Docs</a>.
    *
    * @param items SeqProperty containing list element data.
    * @param componentId Id of the root DOM node.
    * @param body Creates DOM representation for provided item.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (items: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
           (body: (ElemType) => Element): UdashListGroup[ItemType, ElemType] =
    new UdashListGroup(items, componentId)(body)
}