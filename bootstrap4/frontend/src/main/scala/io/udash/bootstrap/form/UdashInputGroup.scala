package io.udash.bootstrap
package form

import io.udash.bootstrap.ComponentId
import org.scalajs.dom

import scalatags.JsDom.all._

final class UdashInputGroup private(groupSize: InputGroupSize, override val componentId: ComponentId)(content: Modifier*)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: dom.Element =
    div(BootstrapStyles.InputGroup.inputGroup, groupSize)(
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

  /** Creates an element to be prepended to the input of this input group. */
  def prepend(content: Modifier*): Modifier =
    div(BootstrapStyles.InputGroup.prepend)(content)

  /** Convenience method that wraps its content in input-group-text */
  def prependText(content: Modifier*): Modifier =
    prepend(span(BootstrapStyles.InputGroup.text)(content))

  /** Creates an element to be appended to the input of this input group. */
  def append(content: Modifier*): Modifier =
    div(BootstrapStyles.InputGroup.append)(content)

  /** Convenience method that wraps its content in input-group-text */
  def appendText(content: Modifier*): Modifier =
    append(span(BootstrapStyles.InputGroup.text)(content))

  /** Wraps input for input group. */
  def input(el: dom.Element): dom.Element = {
    BootstrapStyles.Form.control.addTo(el)
    el
  }
}