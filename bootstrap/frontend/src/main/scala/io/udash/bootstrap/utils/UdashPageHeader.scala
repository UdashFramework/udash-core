package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashPageHeader private(mds: Modifier*) extends UdashBootstrapComponent {
  override val componentId: ComponentId = UdashBootstrap.newId()

  override lazy val render: dom.Element =
    span(id := componentId, BootstrapStyles.Typography.pageHeader)(mds).render
}

object UdashPageHeader {
  /**
    * Creates page header component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#page-header">Bootstrap Docs</a>.
    *
    * @param content Component content - automatically synchronised with provided property content.
    * @return `UdashPageHeader` component, call render to create DOM element.
    */
  def apply(content: Property[_]): UdashPageHeader =
    new UdashPageHeader(bind(content))

  /**
    * Creates page header component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#page-header">Bootstrap Docs</a>.
    *
    * @param content Component content.
    * @return `UdashPageHeader` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashPageHeader =
    new UdashPageHeader(content)
}
