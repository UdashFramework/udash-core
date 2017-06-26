package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashPageHeader private(override val componentId: ComponentId, mds: Modifier*) extends UdashBootstrapComponent {
  import io.udash.css.CssView._
  override lazy val render: dom.Element =
    span(id := componentId, BootstrapStyles.Typography.pageHeader)(mds).render
}

object UdashPageHeader {
  /**
    * Creates page header component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#page-header">Bootstrap Docs</a>.
    *
    * @param content Component content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashPageHeader` component, call render to create DOM element.
    */
  def apply(content: Property[_], componentId: ComponentId = UdashBootstrap.newId()): UdashPageHeader =
    new UdashPageHeader(componentId, bind(content))

  /**
    * Creates page header component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#page-header">Bootstrap Docs</a>.
    *
    * @param content Component content.
    * @return `UdashPageHeader` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashPageHeader =
    new UdashPageHeader(UdashBootstrap.newId(), content)

  /**
    * Creates page header component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#page-header">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Component content.
    * @return `UdashPageHeader` component, call render to create DOM element.
    */
  def id(componentId: ComponentId, content: Modifier*): UdashPageHeader =
    new UdashPageHeader(componentId, content)
}
