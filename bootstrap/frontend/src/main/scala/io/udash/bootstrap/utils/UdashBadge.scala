package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashBadge(mds: Modifier*) extends UdashBootstrapComponent {
  override val componentId: ComponentId = UdashBootstrap.newId()

  override lazy val render: dom.Element =
    span(id := componentId, BootstrapStyles.Label.badge)(mds).render
}

object UdashBadge {
  /**
    * Creates badge component.
    * More: <a href="http://getbootstrap.com/javascript/#badges">Bootstrap Docs</a>.
    *
    * @param content Badge content - automatically synchronised with provided property content.
    * @return `UdashBadge` component, call render to create DOM element.
    */
  def apply(content: Property[_]): UdashBadge =
    new UdashBadge(bind(content))

  /**
    * Creates badge component.
    * More: <a href="http://getbootstrap.com/javascript/#badges">Bootstrap Docs</a>.
    *
    * @param content Badge content.
    * @return `UdashBadge` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashBadge =
    new UdashBadge(content)
}
