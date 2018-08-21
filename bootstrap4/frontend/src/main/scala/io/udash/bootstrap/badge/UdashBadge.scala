package io.udash.bootstrap.badge

import io.udash._
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

class UdashBadge private[badge](
  badgeStyle: ReadableProperty[BootstrapStyles.Color],
  pillStyle: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBootstrapComponent {
  import io.udash.css.CssView._

  protected def baseTag: TypedTag[Element] = span

  override val render: Element = {
    baseTag(
      id := componentId, BootstrapStyles.Badge.badge,
      nestedInterceptor((BootstrapStyles.Badge.color _).reactiveApply(badgeStyle)),
      nestedInterceptor(BootstrapStyles.Badge.pill.styleIf(pillStyle))
    )(content).render
  }
}

private[badge] class UdashBadgeLink(
  link: ReadableProperty[String],
  badgeStyle: ReadableProperty[BootstrapStyles.Color],
  pillStyle: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBadge(badgeStyle, pillStyle, componentId)(content) {
  protected override def baseTag: TypedTag[Element] =
    a(nestedInterceptor(href.bind(link)))
}

object UdashBadge {
  /**
    * Creates badge component.
    * More: <a href="http://getbootstrap.com/javascript/#badges">Bootstrap Docs</a>.
    *
    * @param badgeStyle Color of the badge.
    * @param pillStyle If true, the `badge-pill` style will be applied.
    * @param componentId Id of the root DOM node.
    * @param content Badge content.
    * @return `UdashBadge` component, call render to create a DOM element.
    */
  def apply(
    badgeStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    pillStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashBadge = {
    new UdashBadge(badgeStyle, pillStyle, componentId)(content)
  }

  /**
    * Creates badge link component.
    * More: <a href="http://getbootstrap.com/javascript/#badges">Bootstrap Docs</a>.
    *
    * @param link Content of `href` parameter in returned component.
    * @param badgeStyle Color of the badge.
    * @param pillStyle If true, the `badge-pill` style will be applied.
    * @param componentId Id of the root DOM node.
    * @param content Badge content.
    * @return `UdashBadge` component, call render to create a DOM element.
    */
  def link(
    link: ReadableProperty[String],
    badgeStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    pillStyle: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashBadge = {
    new UdashBadgeLink(link, badgeStyle, pillStyle, componentId)(content)
  }
}
