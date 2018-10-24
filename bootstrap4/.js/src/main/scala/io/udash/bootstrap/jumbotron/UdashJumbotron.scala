package io.udash.bootstrap.jumbotron

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashJumbotron private(
  fluid: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Binding.NestedInterceptor => Modifier) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element = {
    div(
      id := componentId,
      BootstrapStyles.Jumbotron.jumbotron,
      nestedInterceptor(BootstrapStyles.Jumbotron.fluid.styleIf(fluid))
    )(content(nestedInterceptor)).render
  }
}

object UdashJumbotron {
  /**
    * Creates a jumbotron component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/jumbotron/">Bootstrap Docs</a>.
    *
    * @param fluid       If true, applies `jumbotron-fluid` style.
    * @param componentId An id of the root DOM node.
    * @param content     A jumbotron content.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashJumbotron` component, call `render` to create a DOM element.
    */
  def apply(
    fluid: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(content: Binding.NestedInterceptor => Modifier): UdashJumbotron = {
    new UdashJumbotron(fluid, componentId)(content)
  }
}
