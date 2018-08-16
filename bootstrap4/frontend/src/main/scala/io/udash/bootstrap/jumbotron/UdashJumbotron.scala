package io.udash.bootstrap.jumbotron

import io.udash._
import io.udash.bootstrap.{BootstrapStyles, ComponentId, UdashBootstrap, UdashBootstrapComponent}
import org.scalajs.dom
import scalatags.JsDom.all._

final class UdashJumbotron private(
  fluid: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: dom.Element =
    div(
      id := componentId,
      BootstrapStyles.Jumbotron.jumbotron,
      nestedInterceptor(BootstrapStyles.Jumbotron.fluid.styleIf(fluid))
    )(content).render
}

object UdashJumbotron {
  /**
    * Creates jumbotron component.
    * More: <a href="http://getbootstrap.com/javascript/#jumbotron">Bootstrap Docs</a>.
    *
    * @param fluid       If true, applies `jumbotron-fluid` style.
    * @param componentId Id of the root DOM node.
    * @param content     Jumbotron content.
    * @return `UdashJumbotron` component, call render to create DOM element.
    */
  def apply(
    fluid: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashJumbotron =
    new UdashJumbotron(fluid, componentId)(content)
}
