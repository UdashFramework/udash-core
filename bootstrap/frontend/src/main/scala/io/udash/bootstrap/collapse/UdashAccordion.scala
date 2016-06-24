package io.udash.bootstrap
package collapse

import io.udash._
import io.udash.properties.SeqProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.collection.mutable

class UdashAccordion[ItemType, ElemType <: Property[ItemType]] private
                    (panels: properties.SeqProperty[ItemType, ElemType])
                    (panelTypeSelector: ItemType => PanelType,
                     heading: (ElemType) => dom.Element,
                     body: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  import BootstrapTags._
  import scalatags.JsDom.all._

  val accordionId = UdashBootstrap.newId()
  private val collapses = mutable.Map.empty[ElemType, UdashCollapse]
  def collapseOf(panel: ElemType): Option[UdashCollapse] =
    collapses.get(panel)

  lazy val render: Element = {
    import scalacss.ScalatagsCss._

    div(BootstrapStyles.Panel.panelGroup, id := accordionId, role := "tablist", aria.multiselectable := true)(
      repeat(panels)(panel => {
        val headingId = UdashBootstrap.newId()
        val collapse = UdashCollapse()(
          BootstrapStyles.Panel.panelCollapse, role := "tabpanel", aria.labelledby := headingId,
          body(panel)
        )
        val collapseId = collapse.collapseId
        collapses(panel) = collapse
        div(
          BootstrapStyles.Panel.panel,
          panelTypeSelector(panel.get)
        )(
          div(BootstrapStyles.Panel.panelHeading, role := "tab", id := headingId)(
            h4(BootstrapStyles.Panel.panelTitle)(
              a(role := "button", dataToggle := "collapse", dataParent := s"#$accordionId", href := s"#$collapseId")(
                heading(panel)
              )
            )
          ),
          collapse.render
        ).render
      })
    ).render
  }
}

object UdashAccordion {

  def apply[ItemType, ElemType <: Property[ItemType]](panels: SeqProperty[ItemType, ElemType])
           (heading: (ElemType) => Element, body: (ElemType) => Element,
            panelTypeSelector: ItemType => PanelType = (_: ItemType) => PanelType.Default): UdashAccordion[ItemType, ElemType] =
    new UdashAccordion(panels)(panelTypeSelector, heading, body)
}
