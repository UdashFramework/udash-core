package io.udash.bootstrap
package button

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.properties.seq
import org.scalajs.dom
import scalatags.JsDom.all._

final class UdashButtonToolbar[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  val items: seq.ReadableSeqProperty[ItemType, ElemType],
  override val componentId: ComponentId
)(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[dom.Element]) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: dom.Element =
    div(role := "toolbar", BootstrapStyles.Button.toolbar, id := componentId)(
      repeatWithNested(items)(itemFactory)
    ).render
}

object UdashButtonToolbar {
  /**
    * Creates static button toolbar.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param groups Rendered button groups belonging to the toolbar.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def apply(componentId: ComponentId = ComponentId.newId())(groups: dom.Element*): UdashButtonToolbar[dom.Element, Property[dom.Element]] = {
    reactive[dom.Element, Property[dom.Element]](SeqProperty[dom.Element](groups), componentId)((item, _) => item.get)
  }


  /**
    * Creates dynamic buttons toolbar.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param items Data items which will be represented as button groups in this toolbar.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates button group based on an item from `items` sequence.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def reactive[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId()
  )(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[dom.Element]): UdashButtonToolbar[ItemType, ElemType] = {
    new UdashButtonToolbar[ItemType, ElemType](items, componentId)(itemFactory)
  }
}