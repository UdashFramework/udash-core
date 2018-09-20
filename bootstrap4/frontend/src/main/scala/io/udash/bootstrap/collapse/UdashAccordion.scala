package io.udash.bootstrap
package collapse

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.card.UdashCard
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import io.udash.properties.seq
import org.scalajs.dom._

import scala.collection.mutable

final class UdashAccordion[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  elements: seq.ReadableSeqProperty[ItemType, ElemType],
  override val componentId: ComponentId
)(
  heading: (ElemType, Binding.NestedInterceptor) => Seq[Element],
  body: (ElemType, Binding.NestedInterceptor) => Seq[Element]
) extends UdashBootstrapComponent {

  import io.udash.bootstrap.utils.BootstrapTags._
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  private val collapses = mutable.Map.empty[ElemType, UdashCollapse]

  /** Returns [[io.udash.bootstrap.collapse.UdashCollapse]] component created for selected item. */
  def collapseOf(panel: ElemType): Option[UdashCollapse] =
    collapses.get(panel)

  override val render: Element =
    div(BootstrapStyles.Collapse.accordion, id := componentId)(
      nestedInterceptor(
        repeatWithNested(elements) { case (item, nested) =>
          val headingId = ComponentId.newId()
          val card = UdashCard() { factory =>
            val collapse = UdashCollapse()(_ => Seq(
              aria.labelledby := headingId, dataParent := s"#$componentId",
              factory.body(nested => body(item, nested))
            ))
            collapses(item) = collapse
            val header = factory.header { nested => Seq(
              id := headingId,
              h5(BootstrapStyles.Spacing.margin(BootstrapStyles.Side.Bottom, size = "0"))(
                button(
                  BootstrapStyles.Button.btn, BootstrapStyles.Button.color(BootstrapStyles.Color.Link),
                  tpe := "button", dataToggle:= "collapse", href := s"#${collapse.componentId}",
                  heading(item, nested)
                )
              )
            )}
            nested(collapse)
            Seq[Modifier](header, collapse.render)
          }
          nested(card)
          card.render
        }
      )
    ).render
}

object UdashAccordion {
  /**
    * Creates dynamic accordion component. `items` sequence changes will be synchronised with rendered button group.
    * More: <a href="http://getbootstrap.com/javascript/#collapse-example-accordion">Bootstrap Docs</a>.
    *
    * @param elements          Data items which will be represented as panels in accordion.
    * @param componentId       Id of the root DOM node.
    * @param heading           Creates panel header.
    * @param body              Creates panel body.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashAccordion` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    elements: seq.ReadableSeqProperty[ItemType, ElemType],
    componentId: ComponentId = ComponentId.newId()
  )(
    heading: (ElemType, Binding.NestedInterceptor) => Seq[Element],
    body: (ElemType, Binding.NestedInterceptor) => Seq[Element]
  ): UdashAccordion[ItemType, ElemType] =
    new UdashAccordion(elements, componentId)(heading, body)
}
