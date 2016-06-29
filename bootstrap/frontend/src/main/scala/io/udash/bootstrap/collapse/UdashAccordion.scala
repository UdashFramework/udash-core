package io.udash.bootstrap
package collapse

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.panel.{PanelStyle, PanelStyle$, UdashPanel}
import io.udash.properties.SeqProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.collection.mutable

class UdashAccordion[ItemType, ElemType <: Property[ItemType]] private
                    (panels: properties.SeqProperty[ItemType, ElemType])
                    (panelStyleSelector: ItemType => PanelStyle,
                     heading: (ElemType) => dom.Element,
                     body: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  import BootstrapTags._

  import scalatags.JsDom.all._

  val accordionId: ComponentId = UdashBootstrap.newId()
  private val collapses = mutable.Map.empty[ElemType, UdashCollapse]
  def collapseOf(panel: ElemType): Option[UdashCollapse] =
    collapses.get(panel)

  lazy val render: Element = {

    div(BootstrapStyles.Panel.panelGroup, id := accordionId.id, role := "tablist", aria.multiselectable := true)(
      repeat(panels)(item => {
        val headingId = UdashBootstrap.newId()
        val collapse = UdashCollapse()(
          BootstrapStyles.Panel.panelCollapse, role := "tabpanel", aria.labelledby := headingId.id,
          body(item)
        )
        val collapseId = collapse.collapseId
        collapses(item) = collapse
        val panel = UdashPanel(panelStyleSelector(item.get))(
          div(BootstrapStyles.Panel.panelHeading, role := "tab", id := headingId.id)(
            h4(BootstrapStyles.Panel.panelTitle)(
              a(role := "button", dataToggle := "collapse", dataParent := s"#$accordionId", href := s"#$collapseId")(
                heading(item)
              )
            )
          ),
          collapse.render
        )
        panel.render
      })
    ).render
  }
}

object UdashAccordion {

  def apply[ItemType, ElemType <: Property[ItemType]](panels: SeqProperty[ItemType, ElemType])
           (heading: (ElemType) => Element, body: (ElemType) => Element,
            panelTypeSelector: ItemType => PanelStyle = (_: ItemType) => PanelStyle.Default): UdashAccordion[ItemType, ElemType] =
    new UdashAccordion(panels)(panelTypeSelector, heading, body)
}
