package io.udash.bootstrap
package nav

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom
import scalatags.JsDom.all._

final class UdashNav[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  panels: seq.ReadableSeqProperty[ItemType, ElemType],
  align: ReadableProperty[BootstrapStyles.Align],
  vertical: ReadableProperty[Boolean],
  fill: ReadableProperty[Boolean],
  justified: ReadableProperty[Boolean],
  tabs: ReadableProperty[Boolean],
  pills: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  elemFactory: (ElemType, Binding.NestedInterceptor) => Modifier,
  isActive: ElemType => ReadableProperty[Boolean],
  isDisabled: ElemType => ReadableProperty[Boolean],
  isDropdown: ElemType => ReadableProperty[Boolean]
) extends UdashBootstrapComponent {
  import io.udash.css.CssView._

  override val render: dom.Element =
    ul(
      id := componentId,
      BootstrapStyles.Navigation.nav,
      nestedInterceptor(BootstrapStyles.Navigation.justifyCenter.styleIf(align.transform(_ == BootstrapStyles.Align.Center))),
      nestedInterceptor(BootstrapStyles.Navigation.justifyRight.styleIf(align.transform(_ == BootstrapStyles.Align.Right))),
      nestedInterceptor(BootstrapStyles.Flex.column().styleIf(vertical)),
      nestedInterceptor(BootstrapStyles.Navigation.tabs.styleIf(tabs)),
      nestedInterceptor(BootstrapStyles.Navigation.pills.styleIf(pills)),
      nestedInterceptor(BootstrapStyles.Navigation.fill.styleIf(fill)),
      nestedInterceptor(BootstrapStyles.Navigation.justified.styleIf(justified)),
    )(
      nestedInterceptor(
        repeatWithNested(panels) { case (panel, nested) =>
          li(role := "presentation")(
            nested(BootstrapStyles.active.styleIf(isActive(panel))),
            nested(BootstrapStyles.disabled.styleIf(isDisabled(panel))),
            nested(BootstrapStyles.Dropdown.dropdown.styleIf(isDropdown(panel)))
          )(elemFactory(panel, nested)).render
        }
      )
    ).render
}

object UdashNav {
  /** Default navigation model. */
  class NavItem(val name: String, val link: String)

  /** Default breadcrumb model factory. */
  val defaultItemFactory: (ReadableProperty[NavItem], Binding.NestedInterceptor) => Modifier = {
    (item, nested) => nested(produce(item) { item =>
      a(href := item.link)(item.name).render
    })
  }

  /**
    * Creates navigation. More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param panels      Sequence of elements to be converted into navigation.
    * @param align       Alignment of elements in navigation component.
    * @param vertical    If true, shows component as column.
    * @param fill        If true, adjusts items width to fill the whole component space.
    * @param justified   If true, adjusts items width (all elements with equal width) to fill the whole component space.
    * @param tabs        If true, applies `nav-tabs` style.
    * @param pills       If true, applies `nav-pills` style.
    * @param componentId Id of root DOM node.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    panels: seq.ReadableSeqProperty[ItemType, ElemType],
    align: ReadableProperty[BootstrapStyles.Align] = BootstrapStyles.Align.Left.toProperty,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    fill: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    tabs: ReadableProperty[Boolean] = UdashBootstrap.False,
    pills: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    elemFactory: (ElemType, Binding.NestedInterceptor) => Modifier,
    isActive: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDisabled: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDropdown: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False
  ): UdashNav[ItemType, ElemType] = {
    new UdashNav(
      panels, align, vertical, fill, justified, tabs, pills, componentId
    )(elemFactory, isActive, isDisabled, isDropdown)
  }

  /**
    * Creates default navigation. More: <a href="http://getbootstrap.com/components/#nav">Bootstrap Docs</a>.
    *
    * @param panels      Sequence of elements to be converted into navigation.
    * @param align       Alignment of elements in navigation component.
    * @param vertical    If true, shows component as column.
    * @param fill        If true, adjusts items width to fill the whole component space.
    * @param justified   If true, adjusts items width (all elements with equal width) to fill the whole component space.
    * @param tabs        If true, applies `nav-tabs` style.
    * @param pills       If true, applies `nav-pills` style.
    * @param componentId Id of root DOM node.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNav` component, call render to create DOM element.
    */
  def default[ElemType <: ReadableProperty[NavItem]](
    panels: seq.ReadableSeqProperty[NavItem, ElemType],
    align: ReadableProperty[BootstrapStyles.Align] = BootstrapStyles.Align.Left.toProperty,
    vertical: ReadableProperty[Boolean] = UdashBootstrap.False,
    fill: ReadableProperty[Boolean] = UdashBootstrap.False,
    justified: ReadableProperty[Boolean] = UdashBootstrap.False,
    tabs: ReadableProperty[Boolean] = UdashBootstrap.False,
    pills: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    elemFactory: (ElemType, Binding.NestedInterceptor) => Modifier = defaultItemFactory,
    isActive: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDisabled: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDropdown: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False
  ): UdashNav[NavItem, ElemType] = {
    new UdashNav(
      panels, align, vertical, fill, justified, tabs, pills, componentId
    )(elemFactory, isActive, isDisabled, isDropdown)
  }
}