package io.udash.bootstrap
package collapse

import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.card.UdashCard
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.component.{ComponentId, Listenable, ListenableEvent}
import io.udash.properties.seq
import org.scalajs.dom._

import scala.collection.mutable

final class UdashAccordion[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  elements: seq.ReadableSeqProperty[ItemType, ElemType],
  override val componentId: ComponentId
)(
  heading: (ElemType, Binding.NestedInterceptor) => Seq[Element],
  body: (ElemType, Binding.NestedInterceptor) => Seq[Element]
) extends UdashBootstrapComponent
  with Listenable[UdashAccordion[ItemType, ElemType], UdashAccordion.AccordionEvent[ItemType, ElemType]] {

  import io.udash.bootstrap.utils.BootstrapTags._
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  private val collapses = mutable.Map.empty[ElemType, UdashCollapse]
  propertyListeners += elements.listenStructure { patch =>
    patch.removed.foreach(collapses.remove)
  }

  override def kill(): Unit = {
    super.kill()
    collapses.clear()
  }

  /** Returns [[io.udash.bootstrap.collapse.UdashCollapse]] component created for selected item. */
  def collapseOf(panel: ElemType): Option[UdashCollapse] =
    collapses.get(panel)

  override val render: Element =
    div(BootstrapStyles.Collapse.accordion, id := componentId)(
      nestedInterceptor(
        repeatWithIndex(elements) { case (item, idx, nested) =>
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
            Seq[Modifier](
              header, collapse.render,
              nested(
                new Binding {
                  override def applyTo(t: Element): Unit = {
                    propertyListeners += collapse.listen { case ev =>
                      fire(UdashAccordion.AccordionEvent(UdashAccordion.this, item.get, idx.get, ev))
                    }
                  }
                }
              )
            )
          }
          nested(card)
          card.render
        }
      )
    ).render
}

object UdashAccordion {
  final case class AccordionEvent[ItemType, ElemType <: ReadableProperty[ItemType]](
    override val source: UdashAccordion[ItemType, ElemType],
    item: ItemType,
    idx: Int,
    collapseEvent: UdashCollapse.CollapseEvent
  ) extends AbstractCase with ListenableEvent[UdashAccordion[ItemType, ElemType]]

  /**
    * Creates a dynamic accordion component. `items` sequence changes will be synchronised with the rendered elements.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/collapse/#accordion-example">Bootstrap Docs</a>.
    *
    * @param elements    Data items which will be represented as cards in the accordion.
    * @param componentId An id of the root DOM node.
    * @param heading     Creates panel header.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @param body        Creates panel body.
    *                    Use the provided interceptor to properly clean up bindings inside the content.
    * @tparam ItemType A single element's type in the `items` sequence.
    * @tparam ElemType A type of a property containing an element in the `items` sequence.
    * @return A `UdashAccordion` component, call `render` to create a DOM element.
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
