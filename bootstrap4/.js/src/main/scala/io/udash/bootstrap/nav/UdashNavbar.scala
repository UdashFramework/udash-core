package io.udash.bootstrap
package nav

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.nav.UdashNavbar.Position
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.JsDom.tags2

final class UdashNavbar[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint],
  darkStyle: ReadableProperty[Boolean],
  backgroundStyle: ReadableProperty[BootstrapStyles.Color],
  position: ReadableProperty[UdashNavbar.Position],
  override val componentId: ComponentId
)(
  navigationFactory: Binding.NestedInterceptor => Modifier,
  brand: Modifier
)
  extends UdashBootstrapComponent {

  import io.udash.bootstrap.utils.BootstrapTags._
  import io.udash.css.CssView._

  private val collapseId = ComponentId.generate()

  override val render: Element =
    tags2.nav(
      componentId, BootstrapStyles.NavigationBar.navbar,
      nestedInterceptor(BootstrapStyles.Position.fixedTop.styleIf(position.transform(_ == Position.FixedTop))),
      nestedInterceptor(BootstrapStyles.Position.fixedBottom.styleIf(position.transform(_ == Position.FixedBottom))),
      nestedInterceptor(BootstrapStyles.Position.stickyTop.styleIf(position.transform(_ == Position.StickyTop))),
      nestedInterceptor(((dark: Boolean) =>
        if (dark) BootstrapStyles.NavigationBar.dark
        else BootstrapStyles.NavigationBar.light
      ).reactiveApply(darkStyle)),
      nestedInterceptor((BootstrapStyles.Background.color _).reactiveApply(backgroundStyle)),
      nestedInterceptor((BootstrapStyles.NavigationBar.expand _).reactiveApply(expandBreakpoint))
    )(
      div(BootstrapStyles.NavigationBar.brand)(brand),
      button(
        tpe := "button", dataToggle := "collapse", dataTarget := s"#$collapseId",
        aria.expanded := false, aria.label := "Toggle navigation",
        BootstrapStyles.NavigationBar.toggler
      )(span(BootstrapStyles.NavigationBar.togglerIcon)),
      div(
        collapseId, BootstrapStyles.Collapse.collapse,
        BootstrapStyles.NavigationBar.collapse
      )(
        navigationFactory(nestedInterceptor)
      )
    ).render
}

object UdashNavbar {
  final class Position(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Position extends AbstractValueEnumCompanion[Position] {
    final val Auto, FixedTop, FixedBottom, StickyTop: Value = new Position
  }

  /**
    * Creates a default, responsive navigation bar.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/navbar/">Bootstrap Docs</a>.
    *
    * @param expandBreakpoint  Screen size breakpoint to switch between collapsed and expanded menu.
    * @param darkStyle         If true, enables dark navigation bar theme.
    * @param backgroundStyle   Selects navigation bar background style.
    * @param position          Sets bar position.
    * @param componentId       An id of the root DOM node.
    * @param navigationFactory Navigation component factory - if you want to clean up the created navigation on
    *                          the navigation bar cleanup pass it to the provided interceptor.
    * @param brand             A brand DOM element.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashNavbar` component, call `render` to create a DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint] = BootstrapStyles.ResponsiveBreakpoint.Large.toProperty,
    darkStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    backgroundStyle: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Light.toProperty,
    position: ReadableProperty[UdashNavbar.Position] = Position.Auto.toProperty,
    componentId: ComponentId = ComponentId.generate()
  )(
    navigationFactory: Binding.NestedInterceptor => UdashNav[ItemType, ElemType],
    brand: Modifier = ()
  ): UdashNavbar[ItemType, ElemType] = {
    import io.udash.css.CssView._
    new UdashNavbar(expandBreakpoint, darkStyle, backgroundStyle, position, componentId)(
      interceptor => navigationFactory(interceptor).render.styles(BootstrapStyles.NavigationBar.nav),
      brand
    )
  }

  /**
    * Creates a default, responsive navigation bar.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/navbar/">Bootstrap Docs</a>.
    *
    * @param expandBreakpoint  Screen size breakpoint to switch between collapsed and expanded menu.
    * @param darkStyle         If true, enables dark navigation bar theme.
    * @param backgroundStyle   Selects navigation bar background style.
    * @param position          Sets bar position.
    * @param componentId       An id of the root DOM node.
    * @param navigationFactory Navigation content factory - if you want to clean up the created navigation on
    *                          the navigation bar cleanup pass it to the provided interceptor.
    *                          The returned modifier is applied to the collapsible navigation container.
    *                          Usually the modifier should be an `Element` or a sequence of `Element`s.
    * @param brand             A brand DOM element.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashNavbar` component, call `render` to create a DOM element.
    */
  def customContent[ItemType, ElemType <: ReadableProperty[ItemType]](
    expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint] = BootstrapStyles.ResponsiveBreakpoint.Large.toProperty,
    darkStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    backgroundStyle: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Light.toProperty,
    position: ReadableProperty[UdashNavbar.Position] = Position.Auto.toProperty,
    componentId: ComponentId = ComponentId.generate()
  )(
    navigationFactory: Binding.NestedInterceptor => Modifier,
    brand: Modifier = ()
  ): UdashNavbar[ItemType, ElemType] = {
    new UdashNavbar(expandBreakpoint, darkStyle, backgroundStyle, position, componentId)(navigationFactory, brand)
  }
}