package io.udash.bootstrap
package button

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class UdashButtonToolbar[ItemType, ElemType <: Property[ItemType]] private
                        (val items:io.udash.properties.SeqProperty[ItemType, ElemType])
                        (itemFactory: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  override lazy val render: dom.Element = {
    div(role := "toolbar", BootstrapStyles.Button.btnToolbar)(
      repeat(items)(itemFactory)
    ).render
  }

  /** Component root DOM element ID. */
  override val componentId: ComponentId = UdashBootstrap.newId()
}

object UdashButtonToolbar {
  /**
    * Creates static button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param groups Rendered button groups belonging to the toolbar.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def apply(groups: dom.Element*)(implicit ec: ExecutionContext): UdashButtonToolbar[dom.Element, Property[dom.Element]] =
    reactive[dom.Element, Property[dom.Element]](SeqProperty[dom.Element](groups), _.get)


  /**
    * Creates dynamic button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups-toolbar">Bootstrap Docs</a>.
    *
    * @param items Data items which will be represented as button groups in this toolbar.
    * @param itemFactory Creates button group based on an item from `items` sequence.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashButtonToolbar` component, call render to create DOM element representing this toolbar.
    */
  def reactive[ItemType, ElemType <: Property[ItemType]]
              (items: io.udash.properties.SeqProperty[ItemType, ElemType],
               itemFactory: (ElemType) => dom.Element): UdashButtonToolbar[ItemType, ElemType] =
    new UdashButtonToolbar[ItemType, ElemType](items)(itemFactory)
}