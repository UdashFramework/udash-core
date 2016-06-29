package io.udash.bootstrap
package form

import org.scalajs.dom

import scalatags.JsDom.all._

class UdashInputGroup private(groupSize: InputGroupSize)(content: Modifier*) extends UdashBootstrapComponent {
  override val componentId = UdashBootstrap.newId()
  override lazy val render =
    div(BootstrapStyles.Form.inputGroup, groupSize)(
      content
    ).render
}

object UdashInputGroup {
  /**
    * Creates input group. More: <a href="http://getbootstrap.com/components/#input-groups">Bootstrap Docs</a>.
    *
    * @param groupSize Size of the inputs in group.
    * @param content   Group content.
    * @return `UdashInputGroup` component, call render to create DOM element.
    */
  def apply(groupSize: InputGroupSize = InputGroupSize.Default)(content: Modifier*): UdashInputGroup =
    new UdashInputGroup(groupSize)(content)

  /** Creates addon element for input group. */
  def addon(content: Modifier*): Modifier =
    span(BootstrapStyles.Form.inputGroupAddon)(content)

  /** Wraps buttons for input group. */
  def buttons(content: Modifier*): Modifier =
    div(BootstrapStyles.Form.inputGroupBtn)(content)

  /** Wraps input for input group. */
  def input(el: dom.Element): dom.Element = {
    BootstrapStyles.Form.formControl.addTo(el)
    el
  }
}