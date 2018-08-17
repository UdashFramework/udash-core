package io.udash.bootstrap
package nav

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.nav.UdashNavbar.Position
import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2

final class UdashNavbar[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint],
  darkStyle: ReadableProperty[Boolean],
  backgroundStyle: ReadableProperty[BootstrapStyles.Color],
  position: ReadableProperty[UdashNavbar.Position],
  override val componentId: ComponentId
)(
  navigationFactory: Binding.NestedInterceptor => UdashNav[ItemType, ElemType],
  brand: Modifier
)
  extends UdashBootstrapComponent {

  import BootstrapTags._
  import io.udash.css.CssView._

  private val collapseId = ComponentId.newId()

  override val render: dom.Element =
    tags2.nav(
      id := componentId, BootstrapStyles.NavigationBar.navbar,
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
        id := collapseId, BootstrapStyles.Collapse.collapse,
        BootstrapStyles.NavigationBar.collapse
      )(
        navigationFactory(nestedInterceptor).render
          .styles(BootstrapStyles.NavigationBar.nav)
      )
    ).render
}

object UdashNavbar {
  final class Position(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Position extends AbstractValueEnumCompanion[Position] {
    final val Auto, FixedTop, FixedBottom, StickyTop: Value = new Position
  }

  /**
    * Creates default responsive navigation bar. More: <a href="http://getbootstrap.com/components/#navbar">Bootstrap Docs</a>.
    *
    * @param expandBreakpoint  Screen size breakpoint to switch between collapsed and expanded menu.
    * @param darkStyle         If true, enables dark navigation bar theme.
    * @param backgroundStyle   Selects navigation bar background style.
    * @param position          Sets bar position.
    * @param componentId       Id of the root DOM node.
    * @param navigationFactory Navigation component factory - if you want to clean up the created navigation on
    *                          the navigation bar cleanup pass it to the provided interceptor.
    * @param brand             Brand DOM element.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashNavbar` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint] = BootstrapStyles.ResponsiveBreakpoint.Large.toProperty,
    darkStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    backgroundStyle: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Light.toProperty,
    position: ReadableProperty[UdashNavbar.Position] = Position.Auto.toProperty,
    componentId: ComponentId = ComponentId.newId()
  )(
    navigationFactory: Binding.NestedInterceptor => UdashNav[ItemType, ElemType],
    brand: Modifier = ()
  ): UdashNavbar[ItemType, ElemType] = {
    new UdashNavbar(expandBreakpoint, darkStyle, backgroundStyle, position, componentId)(navigationFactory, brand)
  }
}