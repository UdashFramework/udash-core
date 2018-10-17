package io.udash.bootstrap
package nav

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.component.ComponentId
import io.udash.properties.seq
import org.scalajs.dom.Element
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
  elemFactory: (ElemType, Binding.NestedInterceptor) => Element,
  isActive: ElemType => ReadableProperty[Boolean],
  isDisabled: ElemType => ReadableProperty[Boolean],
  isDropdown: ElemType => ReadableProperty[Boolean]
) extends UdashBootstrapComponent {
  import io.udash.css.CssView._

  override val render: Element =
    ul(
      id := componentId,
      BootstrapStyles.Navigation.nav,
      nestedInterceptor(BootstrapStyles.Navigation.justifyCenter.styleIf(align.transform(_ == BootstrapStyles.Align.Center))),
      nestedInterceptor(BootstrapStyles.Navigation.justifyRight.styleIf(align.transform(_ == BootstrapStyles.Align.Right))),
      nestedInterceptor(BootstrapStyles.Flex.column().styleIf(vertical)),
      nestedInterceptor(BootstrapStyles.Navigation.tabs.styleIf(tabs)),
      nestedInterceptor(BootstrapStyles.Navigation.pills.styleIf(pills)),
      nestedInterceptor(BootstrapStyles.Navigation.fill.styleIf(fill)),
      nestedInterceptor(BootstrapStyles.Navigation.justified.styleIf(justified))
    )(
      nestedInterceptor(
        repeatWithNested(panels) { case (panel, nested) =>
          li(role := "presentation")(
            BootstrapStyles.Navigation.item,
            nested(BootstrapStyles.Dropdown.dropdown.styleIf(isDropdown(panel)))
          )({
            val el = elemFactory(panel, nested)
            nested(BootstrapStyles.active.styleIf(isActive(panel))).applyTo(el)
            nested(BootstrapStyles.disabled.styleIf(isDisabled(panel))).applyTo(el)
            el
          }).render
        }
      )
    ).render
}

object UdashNav {
  import io.udash.css.CssView._

  /** Default navigation model. */
  class NavItem(val name: String, val link: Url)

  /** Default breadcrumb model factory. */
  val defaultItemFactory: (ReadableProperty[NavItem], Binding.NestedInterceptor) => Element = {
    (item, nested) => a(
      nested(href.bind(item.transform(_.link.value))),
      nested(bind(item.transform(_.name))),
      BootstrapStyles.Navigation.link
    ).render
  }

  /**
    * Creates a navigation component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/navs/">Bootstrap Docs</a>.
    *
    * @param panels      Sequence of elements to be converted into navigation.
    * @param align       Alignment of elements in navigation component.
    * @param vertical    If true, shows component as column.
    * @param fill        If true, adjusts items width to fill the whole component space.
    * @param justified   If true, adjusts items width (all elements with equal width) to fill the whole component space.
    * @param tabs        If true, applies `nav-tabs` style.
    * @param pills       If true, applies `nav-pills` style.
    * @param componentId An id of the root DOM node.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    *                    Usually you should add the `BootstrapStyles.Navigation.link` style to your links.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashNav` component, call `render` to create a DOM element.
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
    elemFactory: (ElemType, Binding.NestedInterceptor) => Element,
    isActive: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDisabled: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDropdown: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False
  ): UdashNav[ItemType, ElemType] = {
    new UdashNav(
      panels, align, vertical, fill, justified, tabs, pills, componentId
    )(elemFactory, isActive, isDisabled, isDropdown)
  }

  /**
    * Creates a navigation component based on the default `NavItem`.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/navs/">Bootstrap Docs</a>.
    *
    * @param panels      Sequence of elements to be converted into navigation.
    * @param align       Alignment of elements in navigation component.
    * @param vertical    If true, shows component as column.
    * @param fill        If true, adjusts items width to fill the whole component space.
    * @param justified   If true, adjusts items width (all elements with equal width) to fill the whole component space.
    * @param tabs        If true, applies `nav-tabs` style.
    * @param pills       If true, applies `nav-pills` style.
    * @param componentId An id of the root DOM node.
    * @param elemFactory Creates DOM hierarchy representing an element in the navigation.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @param isActive    Creates property indicating if an element is active.
    * @param isDisabled  Creates property indicating if an element is disabled.
    * @param isDropdown  Creates property indicating if an element has a dropdown menu.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashNav` component, call `render` to create a DOM element.
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
    elemFactory: (ElemType, Binding.NestedInterceptor) => Element = defaultItemFactory,
    isActive: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDisabled: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False,
    isDropdown: ElemType => ReadableProperty[Boolean] = (_: ElemType) => UdashBootstrap.False
  ): UdashNav[NavItem, ElemType] = {
    new UdashNav(
      panels, align, vertical, fill, justified, tabs, pills, componentId
    )(elemFactory, isActive, isDisabled, isDropdown)
  }
}