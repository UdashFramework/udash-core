package io.udash.bootstrap
package button

import io.udash._
import io.udash.bootstrap.utils.UdashBootstrapComponent
import io.udash.properties.seq
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashButtonToolbar[ItemType, ElemType <: ReadableProperty[ItemType]] private
                              (val items:seq.ReadableSeqProperty[ItemType, ElemType],
                               override val componentId: ComponentId)
                              (itemFactory: (ElemType) => Seq[Element])
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element =
    div(role := "toolbar", BootstrapStyles.Button.btnToolbar)(
      repeat(items)(itemFactory)
    ).render
}

object UdashButtonToolbar {
  /**
    * Creates static button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param groups Rendered button groups belonging to the toolbar.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def apply(groups: Element*): UdashButtonToolbar[Element, Property[Element]] =
    reactive[Element, Property[Element]](SeqProperty[Element](groups), _.get)

  /**
    * Creates static button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param groups Rendered button groups belonging to the toolbar.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def apply(componentId: ComponentId, groups: Element*): UdashButtonToolbar[Element, Property[Element]] =
    reactive[Element, Property[Element]](SeqProperty[Element](groups), _.get, componentId)


  /**
    * Creates dynamic button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param items Data items which will be represented as button groups in this toolbar.
    * @param itemFactory Creates button group based on an item from `items` sequence.
    * @param componentId Id of the root DOM node.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def reactive[ItemType, ElemType <: ReadableProperty[ItemType]]
              (items: seq.ReadableSeqProperty[ItemType, ElemType],
               itemFactory: (ElemType) => Seq[Element],
               componentId: ComponentId = ComponentId.newId()): UdashButtonToolbar[ItemType, ElemType] =
    new UdashButtonToolbar[ItemType, ElemType](items, componentId)(itemFactory)
}