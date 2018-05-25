package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.{Element, Event, KeyboardEvent}
import org.scalajs.dom.html.{Input => JSInput}
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.duration.{Duration, DurationInt}

/** Abstraction for HTML input tags.*/
private[bindings] abstract class Input(inputType: String) {
  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML input with bound Property, applied modifiers and nested options.
    */
  def apply(property: Property[String], debounce: Duration = 20 millis)(xs: Modifier*): InputBinding[JSInput] =
    new InputBinding[JSInput] {
      private val element = input(
        nestedInterceptor(new InputModifier(property, Some(debounce))),
        tpe := inputType, xs
      ).render

      override def render: JSInput = element
    }

  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated("Use `apply` returning `InputBinding` instead.", "0.7.0")
  def apply(property: Property[String], debounce: Option[Duration], xs: Modifier*): JsDom.TypedTag[JSInput] = {
    input(tpe := inputType, new InputModifier(property, debounce), xs)
  }

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated("Use `apply` returning `InputBinding` instead.", "0.7.0")
  def debounced(property: Property[String], xs: Modifier*): JsDom.TypedTag[JSInput] =
    apply(property, Some(20 millis), xs:_*)

  private class InputModifier(property: Property[String], debounce: Option[Duration])
    extends TextInputsModifier(property, debounce)  {

    override def elementValue(t: Element): String =
      t.asInstanceOf[JSInput].value

    override def setElementValue(t: Element, v: String): Unit =
      t.asInstanceOf[JSInput].value = if (v != null) v else ""

    override def setElementKeyUp(t: Element, callback: KeyboardEvent => Any): Unit =
      t.asInstanceOf[JSInput].onkeyup = callback

    override def setElementOnChange(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[JSInput].onchange = callback

    override def setElementOnInput(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[JSInput].oninput = callback

    override def setElementOnPaste(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[JSInput].onpaste = callback
  }
}

/** Simple text input. */
object TextInput extends Input("text")

/** Password text input. */
object PasswordInput extends Input("password")

/** Number input. (HTML5) */
object NumberInput extends Input("number")
