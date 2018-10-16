package io.udash.bootstrap
package button

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.component.ComponentId
import io.udash.properties.seq
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashButtonToolbar[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  val items: seq.ReadableSeqProperty[ItemType, ElemType],
  override val componentId: ComponentId
)(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[Element]) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element =
    div(role := "toolbar", BootstrapStyles.Button.toolbar, id := componentId)(
      repeatWithNested(items)(itemFactory)
    ).render
}

object UdashButtonToolbar {
  /**
    * Creates a static buttons toolbar.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/button-group/#button-toolbar">Bootstrap Docs</a>.
    *
    * @param componentId An id of the root DOM node.
    * @param groups      Rendered button groups belonging to the toolbar.
    * @return A `UdashButtonToolbar` component, call `render` to create a DOM element representing this toolbar.
    */
  def apply(componentId: ComponentId = ComponentId.newId())(groups: Element*): UdashButtonToolbar[Element, Property[Element]] = {
    reactive[Element, Property[Element]](SeqProperty[Element](groups), componentId)((item, _) => item.get)
  }


  /**
    * Creates a dynamic buttons toolbar.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/button-group/#button-toolbar">Bootstrap Docs</a>.
    *
    * @param items       Data items which will be represented as the button groups in this toolbar.
    * @param componentId An id of the root DOM node.
    * @param itemFactory Creates a buttons group based on an item from the `items` sequence.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashButtonToolbar` component, call `render` to create a DOM element representing this toolbar.
    */
  def reactive[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId()
  )(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[Element]): UdashButtonToolbar[ItemType, ElemType] = {
    new UdashButtonToolbar[ItemType, ElemType](items, componentId)(itemFactory)
  }
}