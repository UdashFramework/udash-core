package io.udash.bootstrap
package collapse

import io.udash._
import io.udash.bootstrap.panel.{PanelStyle, UdashPanel}
import io.udash.bootstrap.utils.UdashBootstrapComponent
import io.udash.properties.seq
import org.scalajs.dom._

import scala.collection.mutable

final class UdashAccordion[ItemType, ElemType <: ReadableProperty[ItemType]] private
                          (panels: seq.ReadableSeqProperty[ItemType, ElemType], override val componentId: ComponentId)
                          (panelStyleSelector: ItemType => PanelStyle,
                           heading: (ElemType) => Element,
                           body: (ElemType) => Element)
  extends UdashBootstrapComponent {

  import BootstrapTags._
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  private val collapses = mutable.Map.empty[ElemType, UdashCollapse]

  /** Returns [[io.udash.bootstrap.collapse.UdashCollapse]] component created for selected item. */
  def collapseOf(panel: ElemType): Option[UdashCollapse] =
    collapses.get(panel)

  override val render: Element =
    div(BootstrapStyles.Panel.panelGroup, id := componentId, role := "tablist", aria.multiselectable := true)(
      repeat(panels)(item => {
        val headingId = ComponentId.newId()
        val collapse = UdashCollapse()(
          BootstrapStyles.Panel.panelCollapse, role := "tabpanel", aria.labelledby := headingId,
          body(item)
        )
        val collapseId = collapse.componentId
        collapses(item) = collapse
        val panel = UdashPanel(panelStyleSelector(item.get))(
          div(BootstrapStyles.Panel.panelHeading, role := "tab", id := headingId)(
            h4(BootstrapStyles.Panel.panelTitle)(
              a(role := "button", dataToggle := "collapse", dataParent := s"#$componentId", href := s"#$collapseId")(
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

object UdashAccordion {
  /**
    * Creates dynamic accordion component. `items` sequence changes will be synchronised with rendered button group.
    * More: <a href="http://getbootstrap.com/javascript/#collapse-example-accordion">Bootstrap Docs</a>.
    *
    * @param panels            Data items which will be represented as panels in accordion.
    * @param componentId       Id of the root DOM node.
    * @param heading           Creates panel header.
    * @param body              Creates panel body.
    * @param panelTypeSelector Panel style.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashAccordion` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (panels: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
           (heading: (ElemType) => Element, body: (ElemType) => Element,
            panelTypeSelector: ItemType => PanelStyle = (_: ItemType) => PanelStyle.Default): UdashAccordion[ItemType, ElemType] =
    new UdashAccordion(panels, componentId)(panelTypeSelector, heading, body)
}
