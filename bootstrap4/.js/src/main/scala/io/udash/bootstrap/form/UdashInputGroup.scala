package io.udash.bootstrap
package form

import io.udash._
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashInputGroup private(
  groupSize: ReadableProperty[Option[BootstrapStyles.Size]],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element =
    div(
      BootstrapStyles.InputGroup.inputGroup,
      nestedInterceptor((BootstrapStyles.InputGroup.size _).reactiveOptionApply(groupSize))
    )(content).render
}

object UdashInputGroup {
  import io.udash.css.CssView._

  /**
    * Creates an inputs group.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/input-group/">Bootstrap Docs</a>.
    *
    * @param groupSize   A size of the inputs in group. One of standard bootstrap values: `BootstrapStyles.Size`.
    * @param componentId An id of the root DOM node.
    * @param content     The group content. The elements usually need to be wrapped with methods like:
    *                    `UdashInputGroup.input`, `UdashInputGroup.select`, `UdashInputGroup.prepend`, `UdashInputGroup.append`, etc.
    * @return A `UdashInputGroup` component, call `render` to create a DOM element.
    */
  def apply(
    groupSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashInputGroup = {
    new UdashInputGroup(groupSize, componentId)(content)
  }

  /** Adds `form-control` style to provided element. It's required to properly display input as part of group. */
  def input(el: Element): Element =
    el.styles(BootstrapStyles.Form.control)

  /** Adds `custom-select` style to provided element. It's required to properly display select as part of group. */
  def select(el: Element): Element =
    el.styles(BootstrapStyles.Form.control, BootstrapStyles.InputGroup.customSelect)

  /** Adds `custom-file` style to provided element. It's required to properly display file input as part of group. */
  def file(el: Element): Element =
    el.styles(BootstrapStyles.Form.control, BootstrapStyles.InputGroup.customFile)

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

  /** Alias for `prependText`. It's a little surprising that you need to mark checkbox as text. */
  @inline def prependCheckbox(content: Modifier*): Modifier =
    prependText(content)

  /** Alias for `appendText`. It's a little surprising that you need to mark checkbox as text. */
  @inline def appendCheckbox(content: Modifier*): Modifier =
    appendText(content)

  /** Alias for `prependText`. It's a little surprising that you need to mark radio as text. */
  @inline def prependRadio(content: Modifier*): Modifier =
    prependText(content)

  /** Alias for `appendText`. It's a little surprising that you need to mark radio as text. */
  @inline def appendRadio(content: Modifier*): Modifier =
    appendText(content)

  /** Alias for `prepend`. */
  @inline def prependButton(content: Modifier*): Modifier =
    prepend(content)

  /** Alias for `append`. */
  @inline def appendButton(content: Modifier*): Modifier =
    append(content)

}