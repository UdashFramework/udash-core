package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.{Element, Event, KeyboardEvent}
import scalatags.JsDom.all._

import scala.concurrent.duration.{Duration, DurationInt}

/** Simple HTML text area with bound Property. */
object TextArea {
  /**
   * @param value                       Property to bind.
   * @param debounce                    Property update timeout after input changes.
   * @param onInputElementEventReceived Callback that's executed when `Input` element receives one of following events:
   *                                    `Input`, `Change`, `KeyUp`, `Paste` and element value is different than property value.
   * @param textareaModifiers           Additional modifiers. Don't use modifiers on value, onchange and onkeyup attributes
   *                                    as they are used internally to sync property value with element value.
   * @return HTML textarea with bound Property, applied modifiers and nested options.
   */
  def apply(
    value: Property[String] = Property(""),
    debounce: Duration = 20 millis,
    onInputElementEventReceived: String => Unit = _ => ()
  )(
    textareaModifiers: Modifier*
  ): InputBinding[TextArea] =
    new InputBinding[TextArea] {
      private val element = textarea(
        textareaModifiers, nestedInterceptor(new TextAreaModifier(value, debounce, onInputElementEventReceived))
      ).render

      override def render: TextArea = element
    }

  private class TextAreaModifier(property: Property[String], debounce: Duration, onPropertyUpdated: String => Unit = _ => ())
    extends TextInputsModifier(property, debounce, onPropertyUpdated) {

    override def elementValue(t: Element): String =
      t.asInstanceOf[TextArea].value

    override def setElementValue(t: Element, v: String): Unit =
      t.asInstanceOf[TextArea].value = v

    override def setElementKeyUp(t: Element, callback: KeyboardEvent => Unit): Unit =
      t.asInstanceOf[TextArea].onkeyup = callback

    override def setElementOnChange(t: Element, callback: Event => Unit): Unit =
      t.asInstanceOf[TextArea].onchange = callback

    override def setElementOnInput(t: Element, callback: Event => Unit): Unit =
      t.asInstanceOf[TextArea].oninput = callback

    override def setElementOnPaste(t: Element, callback: Event => Unit): Unit =
      t.asInstanceOf[TextArea].onpaste = callback
  }
}
