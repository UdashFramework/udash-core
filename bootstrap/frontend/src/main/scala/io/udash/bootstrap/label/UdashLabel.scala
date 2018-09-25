package io.udash.bootstrap
package label

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashLabel private(style: LabelStyle, override val componentId: ComponentId)(mds: Modifier*)
  extends UdashBootstrapComponent {

  override val render: Element =
    span(id := componentId, style)(mds: _*).render
}

object UdashLabel {
  /**
    * Creates label component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def apply(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Default, componentId)(bind(content))

  /**
    * Creates label component with default style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def apply(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Default, componentId)(content)

  /**
    * Creates label component with primary style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def primary(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Primary, componentId)(bind(content))

  /**
    * Creates label component with primary style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def primary(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Primary, componentId)(content)

  /**
    * Creates label component with success style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def success(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Success, componentId)(bind(content))

  /**
    * Creates label component with success style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def success(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Success, componentId)(content)

  /**
    * Creates label component with info style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def info(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Info, componentId)(bind(content))

  /**
    * Creates label component with info style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def info(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Info, componentId)(content)

  /**
    * Creates label component with warning style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def warning(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Warning, componentId)(bind(content))

  /**
    * Creates label component with warning style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def warning(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Warning, componentId)(content)

  /**
    * Creates label component with danger style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param content Label content - automatically synchronised with provided property content.
    * @param componentId Id of the root DOM node.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def danger(content: ReadableProperty[_], componentId: ComponentId = UdashBootstrap.newId()): UdashLabel =
    new UdashLabel(LabelStyle.Danger, componentId)(bind(content))

  /**
    * Creates label component with danger style.
    * More: <a href="http://getbootstrap.com/javascript/#labels">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content Label content.
    * @return `UdashLabel` component, call render to create DOM element.
    */
  def danger(componentId: ComponentId, content: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Danger, componentId)(content)
}