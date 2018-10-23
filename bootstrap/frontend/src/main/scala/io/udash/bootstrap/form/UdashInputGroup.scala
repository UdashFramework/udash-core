package io.udash.bootstrap
package form

import org.scalajs.dom.Element
import scalatags.JsDom.all._
import io.udash._

final class UdashInputGroup private(groupSize: InputGroupSize, override val componentId: ComponentId)(content: Modifier*)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element =
    div(BootstrapStyles.Form.inputGroup, groupSize)(
      content
    ).render
}

object UdashInputGroup {
  import io.udash.css.CssView._

  /**
    * Creates input group. More: <a href="http://getbootstrap.com/components/#input-groups">Bootstrap Docs</a>.
    *
    * @param groupSize   Size of the inputs in group.
    * @param componentId Id of the root DOM node.
    * @param content     Group content.
    * @return `UdashInputGroup` component, call render to create DOM element.
    */
  def apply(groupSize: InputGroupSize = InputGroupSize.Default,
            componentId: ComponentId = ComponentId.newId())(content: Modifier*): UdashInputGroup =
    new UdashInputGroup(groupSize, componentId)(content)

  /** Creates addon element for input group. */
  def addon(content: Modifier*): Modifier =
    span(BootstrapStyles.Form.inputGroupAddon)(content)

  /** Wraps buttons for input group. */
  def buttons(content: Modifier*): Modifier =
    div(BootstrapStyles.Form.inputGroupBtn)(content)

  /** Wraps input for input group. */
  def input(el: Element): Element = {
    BootstrapStyles.Form.formControl.addTo(el)
    el
  }
}