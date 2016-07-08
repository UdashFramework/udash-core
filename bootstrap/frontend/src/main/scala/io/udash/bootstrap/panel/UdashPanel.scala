package io.udash.bootstrap
package panel

import org.scalajs.dom
import scalatags.JsDom.all._

class UdashPanel private(panelStyle: PanelStyle)(content: Modifier*) extends UdashBootstrapComponent {
  override val componentId = UdashBootstrap.newId()
  override lazy val render: dom.Element =
    div(id := componentId, BootstrapStyles.Panel.panel, panelStyle)(
      content
    ).render
}

object UdashPanel {
  /**
    * Creates panel component with provided content.
    * More: <a href="http://getbootstrap.com/components/#pagination">Bootstrap Docs</a>.
    *
    * @param panelStyle Panel component style.
    * @param content    Panel content.
    * @return `UdashPanel` component, call render to create DOM element.
    */
  def apply(panelStyle: PanelStyle = PanelStyle.Default)(content: Modifier*): UdashPanel =
    new UdashPanel(panelStyle)(content)

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
