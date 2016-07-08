package io.udash.bootstrap
package label

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashLabel private(style: LabelStyle)(mds: Modifier*) extends UdashBootstrapComponent {
  override val componentId: ComponentId = UdashBootstrap.newId()

  override lazy val render: dom.Element =
    span(id := componentId, style)(mds: _*).render
}

object UdashLabel {
  /**
    * Creates label component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def apply(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Default)(bind(content))

  /**
    * Creates label component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Default)(content)

  /**
    * Creates label component with primary style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def primary(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Primary)(bind(content))

  /**
    * Creates label component with primary style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def primary(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Primary)(content)

  /**
    * Creates label component with success style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def success(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Success)(bind(content))

  /**
    * Creates label component with success style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def success(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Success)(content)

  /**
    * Creates label component with info style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def info(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Info)(bind(content))

  /**
    * Creates label component with info style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def info(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Info)(content)

  /**
    * Creates label component with warning style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def warning(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Warning)(bind(content))

  /**
    * Creates label component with warning style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def warning(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Warning)(content)

  /**
    * Creates label component with danger style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def danger(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Danger)(bind(content))

  /**
    * Creates label component with danger style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def danger(content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Danger)(content)
}