package io.udash.bootstrap
package button

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.{HasModelPropertyCreator, ModelPropertyCreator, seq}
import org.scalajs.dom

import scalatags.JsDom.all._

final class UdashButtonGroup[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  val items: seq.ReadableSeqProperty[ItemType, ElemType],
  size: ButtonSize, vertical: Boolean, justified: Boolean, toggle: Boolean,
  override val componentId: ComponentId
)(itemFactory: ElemType => Seq[dom.Element]) extends UdashBootstrapComponent {
  import io.udash.bootstrap.BootstrapTags._
  import io.udash.css.CssView._

  private val classes: List[Modifier] = BootstrapStyles.Button.group ::
    BootstrapStyles.Button.groupVertical.styleIf(vertical) ::
    BootstrapStyles.Display.flex().styleIf(justified) ::
    size :: Nil

  override val render: dom.Element =
    div(id := componentId, role := "group", classes, if (toggle) dataToggle := "buttons" else ())(
      repeat(items)(
        // "To use justified button groups with <button> elements, you must wrap each button in a button group.
        // Most browsers don't properly apply our CSS for justification to <button> elements,
        // but since we support button dropdowns, we can work around that." ~ http://getbootstrap.com/components/#btn-groups
        if (justified) item => div(BootstrapStyles.Sizing.width100)(itemFactory(item)).render
        else itemFactory
      )
    ).render
}

object UdashButtonGroup {
  /** Default checkbox element model. */
  trait CheckboxModel {
    def text: String
    def checked: Boolean
  }
  object CheckboxModel extends HasModelPropertyCreator[CheckboxModel]

  /** Default implementation of [[io.udash.bootstrap.button.UdashButtonGroup.CheckboxModel]]. */
  case class DefaultCheckboxModel(text: String, checked: Boolean) extends CheckboxModel
  object DefaultCheckboxModel extends HasModelPropertyCreator[DefaultCheckboxModel]

  /** Button factory for [[io.udash.bootstrap.button.UdashButtonGroup.CheckboxModel]]. It creates group of toggle buttons. */
  val defaultCheckboxFactory = (el: CastableProperty[CheckboxModel]) => {
    val model = el.asModel
    Seq(UdashButton.toggle(active = model.subProp(_.checked))(model.subProp(_.text).get).render)
  }

  /**
    * Creates a static button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param size        button group size
    * @param vertical    If true, buttons will be rendered vertically
    * @param justified   If true, buttons will be justified
    * @param componentId Id of the root DOM node.
    * @param buttons     Rendered buttons belonging to the group
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def apply(size: ButtonSize = ButtonSize.Default, vertical: Boolean = false, justified: Boolean = false,
            componentId: ComponentId = UdashBootstrap.newId())
           (buttons: dom.Element*): UdashButtonGroup[dom.Element, Property[dom.Element]] =
    reactive[dom.Element, Property[dom.Element]](SeqProperty[dom.Element](buttons), size, vertical, justified, componentId)(_.get)


  /**
    * Creates dynamic button group. `items` sequence changes will be synchronized with rendered button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param items       Data items which will be represented as buttons in this group.
    * @param size        button group size
    * @param vertical    If true, buttons will be rendered vertically
    * @param justified   If true, buttons will be justified
    * @param itemFactory Creates a button based on an item from the `items` sequence.
    * @tparam ItemType Single element type in the `items` sequence.
    * @tparam ElemType Type of the property containing every element in the `items` sequence.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def reactive[ItemType, ElemType <: ReadableProperty[ItemType]]
              (items: seq.ReadableSeqProperty[ItemType, ElemType],
               size: ButtonSize = ButtonSize.Default, vertical: Boolean = false, justified: Boolean = false,
               componentId: ComponentId = UdashBootstrap.newId())
              (itemFactory: (ElemType) => Seq[dom.Element]): UdashButtonGroup[ItemType, ElemType] =
    new UdashButtonGroup[ItemType, ElemType](items, size, vertical, justified, false, componentId)(itemFactory)

  /**
    * Creates dynamic toggle buttons group. Add/remove element from `items` sequence
    * and it will be synchronised with presented button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param items     Data items which will be represented as buttons in this group.
    * @param size      button group size
    * @param vertical  If true, buttons will be rendered vertically
    * @param justified If true, buttons will be justified
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def checkboxes(items: seq.ReadableSeqProperty[CheckboxModel, CastableProperty[CheckboxModel]],
                 size: ButtonSize = ButtonSize.Default, vertical: Boolean = false, justified: Boolean = false,
                 componentId: ComponentId = UdashBootstrap.newId()): UdashButtonGroup[CheckboxModel, CastableProperty[CheckboxModel]] =
    new UdashButtonGroup[CheckboxModel, CastableProperty[CheckboxModel]](items, size, vertical, justified, false, componentId)(defaultCheckboxFactory)

  /**
    * Creates dynamic radio buttons group. Add/remove element from `items` sequence
    * and it will be synchronised with presented button group.
    * More: <a href="http://getbootstrap.com/components/#btn-groups">Bootstrap Docs</a>.
    *
    * @param items     Data items which will be represented as buttons in this group.
    * @param size      button group size
    * @param vertical  If true, buttons will be rendered vertically
    * @param justified If true, buttons will be justified
    * @tparam ItemType Single element type in the `items` sequence.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashButtonGroup` component, call render to create DOM element representing this group.
    */
  def radio[ItemType <: CheckboxModel : ModelPropertyCreator, ElemType <: CastableProperty[ItemType]]
           (items: seq.ReadableSeqProperty[ItemType, ElemType],
            size: ButtonSize = ButtonSize.Default, vertical: Boolean = false, justified: Boolean = false,
            componentId: ComponentId = UdashBootstrap.newId())
           : UdashButtonGroup[ItemType, ElemType] = {
    val radioId = UdashBootstrap.newId()
    val selected = Property[String]("")
    new UdashButtonGroup[ItemType, ElemType](items, size, vertical, justified, true, componentId)(el => {
      val model = el.asModel
      UdashButton(active = model.subProp(_.checked))(model.subProp(_.text).get).radio(radioId, selected)
    })
  }
}