package io.udash.bootstrap
package button

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton.ButtonClickEvent
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.JsDom
import scalatags.JsDom.all._

class UdashButton private(buttonStyle: ButtonStyle, size: ButtonSize, block: Boolean,
                          val active: Property[Boolean], val disabled: Property[Boolean],
                          override val componentId: ComponentId)
                         (content: Modifier*) extends UdashBootstrapComponent with Listenable[UdashButton, ButtonClickEvent] {
  import io.udash.css.CssView._

  private lazy val classes: List[Modifier] = buttonStyle :: size ::
    BootstrapStyles.Button.btnBlock.styleIf(block) :: BootstrapStyles.active.styleIf(active) ::
    BootstrapStyles.disabled.styleIf(disabled) :: JsDom.all.disabled.attrIf(disabled) :: Nil

  override lazy val render: dom.html.Button =
    button(id := componentId, tpe := "button")(classes: _*)(
      onclick :+= ((_: MouseEvent) => {
        fire(ButtonClickEvent(this))
        false
      })
    )(content: _*).render

  private[bootstrap] def radio(radioId: ComponentId, selected: Property[String]): dom.Element = {
    val inputId = UdashBootstrap.newId()
    val in = input(tpe := "radio", name := radioId, id := inputId)
    selected.listen(v => active.set(v == inputId.id))
    active.listen(v => if (v) selected.set(inputId.id))
    if (active.get) selected.set(inputId.id)
    label(id := componentId)(classes: _*)(
      onclick :+= ((_: MouseEvent) => {
        selected.set(inputId.id)
        fire(ButtonClickEvent(this))
        false
      })
    )(in)(content: _*).render
  }
}

object UdashButton {
  case class ButtonClickEvent(source: UdashButton) extends ListenableEvent[UdashButton]

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  /**
    * Creates button component, more: <a href="http://getbootstrap.com/css/#buttons">Bootstrap Docs</a>.
    *
    * @param buttonStyle Button style
    * @param size        Button size
    * @param block       If true, rendered button will be a full-width block
    * @param active      Property indicating if the button is in the `active` state.
    * @param disabled    Property indicating if the button is disabled.
    * @param componentId Id of the root DOM node.
    * @param content     Button content
    * @return `UdashButton` component, call render to create DOM element representing this button.
    */
  def apply(buttonStyle: ButtonStyle = ButtonStyle.Default, size: ButtonSize = ButtonSize.Default, block: Boolean = false,
            active: Property[Boolean] = Property(false), disabled: Property[Boolean] = Property(false),
            componentId: ComponentId = UdashBootstrap.newId())(content: Modifier*): UdashButton =
    new UdashButton(buttonStyle, size, block, active, disabled, componentId)(content: _*)

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
  def toggle(buttonStyle: ButtonStyle = ButtonStyle.Default, size: ButtonSize = ButtonSize.Default, block: Boolean = false,
             active: Property[Boolean] = Property(false), disabled: Property[Boolean] = Property(false),
             componentId: ComponentId = UdashBootstrap.newId())(content: Modifier*): UdashButton = {
    val button = new UdashButton(buttonStyle, size, block, active, disabled, componentId)(content: _*)
    button.listen { case _ => active.set(!active.get) }
    button
  }

}
