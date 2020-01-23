package io.udash.bootstrap
package badge

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

class UdashBadge private[badge](
  badgeStyle: ReadableProperty[BootstrapStyles.Color],
  pillStyle: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Binding.NestedInterceptor => Modifier)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  protected def baseTag: TypedTag[Element] = span

  override val render: Element = {
    baseTag(
      componentId, BootstrapStyles.Badge.badge,
      nestedInterceptor((BootstrapStyles.Badge.color _).reactiveApply(badgeStyle)),
      nestedInterceptor(BootstrapStyles.Badge.pill.styleIf(pillStyle))
    )(content(nestedInterceptor)).render
  }
}

private[badge] class UdashBadgeLink(
  link: ReadableProperty[String],
  badgeStyle: ReadableProperty[BootstrapStyles.Color],
  pillStyle: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Binding.NestedInterceptor => Modifier)
  extends UdashBadge(badgeStyle, pillStyle, componentId)(content) {

  protected override def baseTag: TypedTag[Element] =
    a(nestedInterceptor(href.bind(link)))
}

object UdashBadge {
  /**
    * Creates a badge component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/badge/">Bootstrap Docs</a>.
    *
    * @param badgeStyle  A color of the badge.
    * @param pillStyle   If true, the `badge-pill` style will be applied.
    * @param componentId An id of the root DOM node.
    * @param content     A badge content. Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashBadge` component, call `render` to create a DOM element.
    */
  def apply(
    badgeStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    pillStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.generate()
  )(content: Binding.NestedInterceptor => Modifier): UdashBadge = {
    new UdashBadge(badgeStyle, pillStyle, componentId)(content)
  }

  /**
    * Creates a badge link component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/badge/">Bootstrap Docs</a>.
    *
    * @param link        A content of the `href` parameter in the returned component.
    * @param badgeStyle  A color of the badge.
    * @param pillStyle   If true, the `badge-pill` style will be applied.
    * @param componentId An id of the root DOM node.
    * @param content     A badge content. Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashBadge` component, call `render` to create a DOM element.
    */
  def link(
    link: ReadableProperty[String],
    badgeStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    pillStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.generate()
  )(content: Binding.NestedInterceptor => Modifier): UdashBadge = {
    new UdashBadgeLink(link, badgeStyle, pillStyle, componentId)(content)
  }
}
