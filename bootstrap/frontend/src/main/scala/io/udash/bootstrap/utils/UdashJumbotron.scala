package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashJumbotron private(mds: Modifier*) extends UdashBootstrapComponent {
  override val componentId: ComponentId = UdashBootstrap.newId()

  override lazy val render: dom.Element =
    div(id := componentId, BootstrapStyles.jumbotron)(mds).render
}

object UdashJumbotron {
  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param content Jumbotron content - automatically synchronised with provided property content.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def apply(content: Property[_]): UdashJumbotron =
    new UdashJumbotron(bind(content))

  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param content Jumbotron content.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashJumbotron =
    new UdashJumbotron(content)
}
