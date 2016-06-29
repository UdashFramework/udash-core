package io.udash.bootstrap.panel

import io.udash.bootstrap.{BootstrapStyles, UdashBootstrapComponent}
import org.scalajs.dom

import scalatags.JsDom.all._

class UdashPanel private(panelStyle: PanelStyle)(mds: Modifier*) extends UdashBootstrapComponent {
  lazy val render: dom.Element =
    div(BootstrapStyles.Panel.panel, panelStyle)(
      mds
    ).render
}

object UdashPanel {
  def apply(panelStyle: PanelStyle = PanelStyle.Default)(content: Modifier*): UdashPanel =
    new UdashPanel(panelStyle)(content)

  def heading(mds: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelHeading)(mds)

  def body(mds: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelBody)(mds)

  def footer(mds: Modifier*): Modifier =
    div(BootstrapStyles.Panel.panelFooter)(mds)
}
