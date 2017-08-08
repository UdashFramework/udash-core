package io.udash.bootstrap
package panel

import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

final class UdashPanel private(panelStyle: PanelStyle, override val componentId: ComponentId)(content: Modifier*)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: dom.Element =
    div(id := componentId, BootstrapStyles.Panel.panel, panelStyle)(
      content
    ).render
}

object UdashPanel {
  import io.udash.css.CssView._

  /**
    * Creates panel component with provided content.
    * More: <a href="http://getbootstrap.com/components/#pagination">Bootstrap Docs</a>.
    *
    * @param panelStyle Panel component style.
    * @param componentId Id of the root DOM node.
    * @param content    Panel content.
    * @return `UdashPanel` component, call render to create DOM element.
    */
  def apply(panelStyle: PanelStyle = PanelStyle.Default, componentId: ComponentId = UdashBootstrap.newId())(content: Modifier*): UdashPanel =
    new UdashPanel(panelStyle, componentId)(content)

  /** Creates panel header with provided content. You can use it as panel component content. */
  def heading(content: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelHeading)(content)

  /** Creates panel body with provided content. You can use it as panel component content. */
  def body(content: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelBody)(content)

  /** Creates panel footer with provided content. You can use it as panel component content. */
  def footer(content: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelFooter)(content)
}
