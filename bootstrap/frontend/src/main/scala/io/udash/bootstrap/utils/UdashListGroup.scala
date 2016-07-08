package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.SeqProperty
import org.scalajs.dom
import org.scalajs.dom.Element

class UdashListGroup[ItemType, ElemType <: Property[ItemType]] private(items: properties.SeqProperty[ItemType, ElemType])
                                                                      (body: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  import scalatags.JsDom.all._

  override val componentId: ComponentId = UdashBootstrap.newId()

  override lazy val render: Element =
    ul(id := componentId, BootstrapStyles.List.listGroup)(
      repeat(items)(item => {
        val el = body(item)
        BootstrapStyles.List.listGroupItem.addTo(el)
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
  * @param body    Creates DOM representation for provided item.
    * @tparam ItemType Single element type in `items`.
  * @tparam ElemType   Type of the property containing every element in `items` sequence.
  * @return `UdashBreadcrumbs` component, call render to create DOM element.
  */
  def apply[ItemType, ElemType <: Property[ItemType]]
           (items: SeqProperty[ItemType, ElemType])
           (body: (ElemType) => Element): UdashListGroup[ItemType, ElemType] =
    new UdashListGroup(items)(body)
}