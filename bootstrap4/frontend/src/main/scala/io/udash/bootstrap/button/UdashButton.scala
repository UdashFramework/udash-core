package io.udash.bootstrap
package button

import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.bootstrap.button.UdashButton.ButtonClickEvent
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.JsDom
import scalatags.JsDom.all._

final class UdashButton private(
  buttonStyle: ReadableProperty[BootstrapStyles.Color],
  size: ReadableProperty[Option[BootstrapStyles.Size]],
  outline: ReadableProperty[Boolean],
  block: ReadableProperty[Boolean],
  active: ReadableProperty[Boolean],
  disabled: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(content: Modifier*) extends UdashBootstrapComponent with Listenable[UdashButton, ButtonClickEvent] {

  import io.udash.css.CssView._

  private val classes: List[Modifier] = {
    (BootstrapStyles.Button.btn: Modifier) ::
      nestedInterceptor(
        ((data: (BootstrapStyles.Color, Boolean)) => data match {
          case (style, false) =>
            BootstrapStyles.Button.color(style)
          case (style, true) =>
            BootstrapStyles.Button.outline(style)
        }).reactiveApply(buttonStyle.combine(outline)((_, _)))
      ) ::
      nestedInterceptor((BootstrapStyles.Button.size _).reactiveOptionApply(size)) ::
      nestedInterceptor(BootstrapStyles.Button.block.styleIf(block)) ::
      nestedInterceptor(BootstrapStyles.active.styleIf(active)) ::
      nestedInterceptor(BootstrapStyles.disabled.styleIf(disabled)) ::
      nestedInterceptor(JsDom.all.disabled.attrIf(disabled)) :: Nil
  }

  override val render: dom.html.Button = {
    button(id := componentId, tpe := "button")(classes: _*)(
      onclick :+= ((me: MouseEvent) => {
        fire(ButtonClickEvent(this, me))
        false
      })
    )(content).render
  }

  private[bootstrap] def radio(radioId: ComponentId, selected: Property[String]): dom.Element = {
    val inputId = ComponentId.newId()
    val in = input(tpe := "radio", name := radioId, id := inputId)
//    selected.listen(v => active.set(v == inputId.id)) // TODO ????
    active.listen(v => if (v) selected.set(inputId.id))
    if (active.get) selected.set(inputId.id)
    label(id := componentId)(classes: _*)(
      onclick :+= ((me: MouseEvent) => {
        selected.set(inputId.id)
        fire(ButtonClickEvent(this, me))
        false
      })
    )(in)(content: _*).render
  }
}

object UdashButton {
  final case class ButtonClickEvent(source: UdashButton, mouseEvent: MouseEvent)
    extends AbstractCase with ListenableEvent[UdashButton]

  /**
    * Creates button component, more: <a href="http://getbootstrap.com/css/#buttons">Bootstrap Docs</a>.
    *
    * @param buttonStyle Button style, one of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param size        Button size, one of the standard bootstrap sizes `BootstrapStyles.Size`.
    * @param outline     If true, selects outline style for the button.
    * @param block       If true, rendered button will be a full-width block.
    * @param active      Property indicating if the button is in the `active` state.
    * @param disabled    Property indicating if the button is disabled.
    * @param componentId Id of the root DOM node.
    * @param content     Button content
    * @return `UdashButton` component, call render to create DOM element representing this button.
    */
  def apply(
    buttonStyle: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Secondary.toProperty,
    size: ReadableProperty[Option[BootstrapStyles.Size]] = (None: Option[BootstrapStyles.Size]).toProperty,
    outline: ReadableProperty[Boolean] = false.toProperty,
    block: ReadableProperty[Boolean] = false.toProperty,
    active: ReadableProperty[Boolean] = false.toProperty,
    disabled: ReadableProperty[Boolean] = false.toProperty,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashButton =
    new UdashButton(buttonStyle, size, outline, block, active, disabled, componentId)(content: _*)

  /**
    * Creates toggle button component. It will automatically toggle `active` property on click.
    * More: <a href="http://getbootstrap.com/css/#buttons">Bootstrap Docs</a>.
    *
    * @param buttonStyle Button style
    * @param size        Button size
    * @param block       If true, the rendered button will be a full-width block
    * @param active      Property indicating if the button is in `active` state.
    * @param disabled    Property indicating if the button is disabled.
    * @param componentId Id of the root DOM node.
    * @param content     Button content
    * @return `UdashButton` component, call render to create DOM element representing this button.
    */
  def toggle(
    active: Property[Boolean],
    buttonStyle: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Secondary.toProperty,
    size: ReadableProperty[Option[BootstrapStyles.Size]] = (None: Option[BootstrapStyles.Size]).toProperty,
    outline: ReadableProperty[Boolean] = false.toProperty,
    block: ReadableProperty[Boolean] = false.toProperty,
    disabled: ReadableProperty[Boolean] = false.toProperty,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): UdashButton = {
    val button = new UdashButton(buttonStyle, size, outline, block, active, disabled, componentId)(content: _*)
    button.listen { case _ => active.set(!active.get) }
    button
  }

}
