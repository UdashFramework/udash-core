package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashJumbotron private(override val componentId: ComponentId)(mds: Modifier*) extends UdashBootstrapComponent {
  override lazy val render: dom.Element =
    div(id := componentId, BootstrapStyles.jumbotron)(mds).render
}

object UdashJumbotron {
  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param content Jumbotron content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def apply(content: Property[_], componentId: ComponentId = UdashBootstrap.newId()): UdashJumbotron =
    new UdashJumbotron(componentId)(bind(content))

  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param content Jumbotron content.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashJumbotron =
    new UdashJumbotron(UdashBootstrap.newId())(content)

  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Jumbotron content.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def id(componentId: ComponentId, content: Modifier*): UdashJumbotron =
    new UdashJumbotron(componentId)(content)
}
