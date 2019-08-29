package io.udash.bootstrap
package navs

import io.udash._
import io.udash.bootstrap.utils.UdashBootstrapComponent
import io.udash.css.CssStyle
import io.udash.properties.seq
import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._

final class UdashNav[ItemType, ElemType <: ReadableProperty[ItemType]] private
                    (navStyle: CssStyle, stacked: Boolean, justified: Boolean, override val componentId: ComponentId)
                    (val panels: seq.ReadableSeqProperty[ItemType, ElemType])
                    (elemFactory: (ElemType) => Node,
                     isActive: (ElemType) => ReadableProperty[Boolean],
                     isDisabled: (ElemType) => ReadableProperty[Boolean],
                     isDropdown: (ElemType) => ReadableProperty[Boolean])
  extends UdashBootstrapComponent {
  import io.udash.css.CssView._

  override val render: Element =
    ul(
      id := componentId,
      BootstrapStyles.Navigation.nav, navStyle,
      BootstrapStyles.Navigation.navJustified.styleIf(justified),
      BootstrapStyles.Navigation.navStacked.styleIf(stacked)
    )(
      repeat(panels)(panel => {
        li(role := "presentation")(
          BootstrapStyles.active.styleIf(isActive(panel)),
          BootstrapStyles.disabled.styleIf(isDisabled(panel)),
          BootstrapStyles.Dropdown.dropdown.styleIf(isDropdown(panel))
        )(elemFactory(panel)).render
      })
    ).render
}

object UdashNav {

  /**
    * Creates default navigation. More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param stacked     If true, navigation elements will be rendered vertically.
    * @param justified   If true, navigation elements will be justified.
    * @param panels      Sequence of elements to be converted into navigation.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (stacked: Boolean = false, justified: Boolean = false, componentId: ComponentId = ComponentId.newId())
           (panels: seq.ReadableSeqProperty[ItemType, ElemType])
           (elemFactory: (ElemType) => Node,
            isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    pills(stacked, justified, componentId)(panels)(elemFactory, isActive, isDisabled, isDropdown)

  /**
    * Creates pills navigation. More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param stacked     If true, navigation elements will be rendered vertically.
    * @param justified   If true, navigation elements will be justified.
    * @param panels      Sequence of elements to be converted into navigation.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def pills[ItemType, ElemType <: ReadableProperty[ItemType]]
           (stacked: Boolean = false, justified: Boolean = false, componentId: ComponentId = ComponentId.newId())
           (panels: seq.ReadableSeqProperty[ItemType, ElemType])
           (elemFactory: (ElemType) => Node,
            isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    new UdashNav(BootstrapStyles.Navigation.navPills, stacked, justified, componentId)(panels)(elemFactory, isActive, isDisabled, isDropdown)

  /**
    * Creates tabs navigation. More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param stacked     If true, navigation elements will be rendered vertically.
    * @param justified   If true, navigation elements will be justified.
    * @param panels      Sequence of elements to be converted into navigation.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def tabs[ItemType, ElemType <: ReadableProperty[ItemType]]
          (stacked: Boolean = false, justified: Boolean = false, componentId: ComponentId = ComponentId.newId())
          (panels: seq.ReadableSeqProperty[ItemType, ElemType])
          (elemFactory: (ElemType) => Node,
           isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
           isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
           isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    new UdashNav(BootstrapStyles.Navigation.navTabs, stacked, justified, componentId)(panels)(elemFactory, isActive, isDisabled, isDropdown)

  /**
    * Creates navbar navigation. It's prepared to put into navbar element. <br/>
    * More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param panels      Sequence of elements to be converted into navigation.
    * @param componentId Id of the root DOM node.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if element is active.
    * @param isDisabled  Creates property indicating if element is disabled.
    * @param isDropdown  Creates property indicating if element has a dropdown menu.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def navbar[ItemType, ElemType <: ReadableProperty[ItemType]]
            (panels: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
            (elemFactory: (ElemType) => Node,
             isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
             isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
             isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    new UdashNav(BootstrapStyles.Navigation.navbarNav, false, false, componentId)(panels)(elemFactory, isActive, isDisabled, isDropdown)
}