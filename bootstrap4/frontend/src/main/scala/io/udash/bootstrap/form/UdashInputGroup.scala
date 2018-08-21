package io.udash.bootstrap
package form

import io.udash._
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import org.scalajs.dom
import scalatags.JsDom.all._

final class UdashInputGroup private(
  groupSize: ReadableProperty[Option[BootstrapStyles.Size]],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: dom.Element =
    div(
      BootstrapStyles.InputGroup.inputGroup,
      nestedInterceptor((BootstrapStyles.InputGroup.size _).reactiveOptionApply(groupSize))
    )(content).render
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
  def apply(
    groupSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashInputGroup = {
    new UdashInputGroup(groupSize, componentId)(content)
  }

  /** Adds `form-control` style to provided element. It's required to properly display input as part of group. */
  def input(el: dom.Element): dom.Element =
    el.styles(BootstrapStyles.Form.control)

  /** Adds `custom-select` style to provided element. It's required to properly display select as part of group. */
  def select(el: dom.Element): dom.Element =
    el.styles(BootstrapStyles.InputGroup.customSelect)

  /** Adds `custom-file` style to provided element. It's required to properly display file input as part of group. */
  def file(el: dom.Element): dom.Element =
    el.styles(BootstrapStyles.InputGroup.customFile)

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

}