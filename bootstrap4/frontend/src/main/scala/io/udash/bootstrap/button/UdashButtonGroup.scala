package io.udash.bootstrap
package button

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom
import scalatags.JsDom.all._

final class UdashButtonGroup[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  size: ReadableProperty[Option[BootstrapStyles.Size]],
  vertical: ReadableProperty[Boolean],
  justified: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[dom.Element]) extends UdashBootstrapComponent {
  import io.udash.css.CssView._

  private def buttonFullWidth: Binding =
    BootstrapStyles.Sizing.width100.styleIf(justified)

  private val classes: List[Modifier] = {
    (BootstrapStyles.Button.group: Modifier) ::
      nestedInterceptor(BootstrapStyles.Button.groupVertical.styleIf(vertical)) ::
      nestedInterceptor(BootstrapStyles.Display.flex().styleIf(justified)) ::
      nestedInterceptor((BootstrapStyles.Button.groupSize _).reactiveOptionApply(size)) :: Nil
  }

  override val render: dom.Element =
    div(id := componentId, role := "group", classes)(
      nestedInterceptor(
        repeatWithNested(items) { case (item, nested) =>
          val elements = itemFactory(item, nested)
          elements.foreach(el => nested(buttonFullWidth).applyTo(el))
          elements
        }
      )
    ).render
}

object UdashButtonGroup {
  /**
    * Creates a static button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param size        Buttons group size.
    * @param vertical    If true, buttons will be rendered vertically.
    * @param justified   If true, buttons will be justified.
    * @param componentId Id of the root DOM node.
    * @param buttons     Rendered buttons (or groups) belonging to this group.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def apply(
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(buttons: dom.Element*): UdashButtonGroup[dom.Element, Property[dom.Element]] = {
    reactive[dom.Element, Property[dom.Element]](
      SeqProperty[dom.Element](buttons),
      size, vertical, justified, componentId
    )((p, _) => p.get)
  }


  /**
    * Creates dynamic button group. `items` sequence changes will be synchronized with rendered button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param items       Data items which will be represented as buttons in this group.
    * @param size        Buttons group size.
    * @param vertical    If true, buttons will be rendered vertically.
    * @param justified   If true, buttons will be justified.
    * @param itemFactory Creates a button based on an item from the `items` sequence.
    * @tparam ItemType Single element type in the `items` sequence.
    * @tparam ElemType Type of the property containing every element in the `items` sequence.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def reactive[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(itemFactory: (ElemType, Binding.NestedInterceptor) => Seq[dom.Element]): UdashButtonGroup[ItemType, ElemType] = {
    new UdashButtonGroup[ItemType, ElemType](
      items, size, vertical, justified, componentId
    )(itemFactory)
  }

  /**
    * Creates dynamic toggle buttons group. Add/remove element from `items` sequence
    * and it will be synchronised with presented group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param selectedItems Elements represented by active buttons.
    * @param options       Data items which will be represented as buttons in this group.
    * @param size          button group size
    * @param vertical      If true, buttons will be rendered vertically
    * @param justified     If true, buttons will be justified
    * @param btnFactory    It should create UdashButton instance based on provided item and active property.
    *                      Don't forget to pass the second argument to created button as `active` arguemnt.
    *                      The default implementation uses `toString` in order to create a button's content.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def checkboxes[ItemType, ElemType <: ReadableProperty[ItemType]](
    selectedItems: SeqProperty[ItemType],
    options: seq.ReadableSeqProperty[ItemType, ElemType],
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    btnFactory: (ElemType, ReadableProperty[Boolean], Binding.NestedInterceptor) => UdashButton =
      (item: ElemType, active: ReadableProperty[Boolean], nested: Binding.NestedInterceptor) => {
        UdashButton(active = active)(nested => Seq(nested(bind(item)))).setup(nested)
      }
  ): UdashButtonGroup[ItemType, ElemType] = {
    new UdashButtonGroup[ItemType, ElemType](
      options, size, vertical, justified, componentId
    )((item, nested) => {
      val active: ReadableProperty[Boolean] = selectedItems.transform(_.contains(item.get))
      val btn: UdashButton = btnFactory(item, active, nested)
      btn.listen {
        case UdashButton.ButtonClickEvent(_, _) =>
          if (active.get) selectedItems.remove(item.get)
          else selectedItems.append(item.get)
      }
      nested(btn)
      btn.render
    })
  }

  /**
    * Creates dynamic radio buttons group. Add/remove element from `items` sequence
    * and it will be synchronised with presented button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param selectedItem  Element represented by active button.
    * @param options       Data items which will be represented as buttons in this group.
    * @param size          button group size
    * @param vertical      If true, buttons will be rendered vertically
    * @param justified     If true, buttons will be justified
    * @param btnFactory    It should create UdashButton instance based on provided item and active property.
    *                      Don't forget to pass the second argument to created button as `active` arguemnt.
    *                      The default implementation uses `toString` in order to create a button's content.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def radio[ItemType, ElemType <: ReadableProperty[ItemType]](
    selectedItem: Property[ItemType],
    options: seq.ReadableSeqProperty[ItemType, ElemType],
    size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    btnFactory: (ElemType, ReadableProperty[Boolean], Binding.NestedInterceptor) => UdashButton =
      (item: ElemType, active: ReadableProperty[Boolean], nested: Binding.NestedInterceptor) =>
        UdashButton(active = active)(nested => Seq(nested(bind(item)))).setup(nested)
  ): UdashButtonGroup[ItemType, ElemType] = {
    new UdashButtonGroup[ItemType, ElemType](
      options, size, vertical, justified, componentId
    )((item, nested) => {
      val active: ReadableProperty[Boolean] = selectedItem.transform(_ == item.get)
      val btn: UdashButton = btnFactory(item, active, nested)
      btn.listen {
        case UdashButton.ButtonClickEvent(_, _) =>
          selectedItem.set(item.get)
      }
      nested(btn)
      btn.render
    })
  }
}