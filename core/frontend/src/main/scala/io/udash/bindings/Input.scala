package io.udash.bindings

import io.udash.bindings.modifiers.TextInputsModifier
import io.udash.properties.single.Property
import org.scalajs.dom.{html, _}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Abstraction for HTML input tags.
  */
private[bindings] abstract class Input(inputType: String) {
  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def apply(property: Property[String], debounce: Option[Duration], xs: Modifier*): JsDom.TypedTag[html.Input] = {
    val bind = new TextInputsModifier(property, debounce) {
      override def elementValue(t: Element): String =
        t.asInstanceOf[html.Input].value

      override def setElementValue(t: Element, v: String): Unit =
        t.asInstanceOf[html.Input].value = if (v != null) v else ""

      override def setElementKeyUp(t: Element, callback: (KeyboardEvent) => Any): Unit =
        t.asInstanceOf[html.Input].onkeyup = callback

      override def setElementOnChange(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.Input].onchange = callback

      override def setElementOnInput(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.Input].oninput = callback

      override def setElementOnPaste(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.Input].onpaste = callback
    }

    input(tpe := inputType, bind, xs)
  }

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated(message = "You should use `debounced` method or explicitly pass `None` as debouncing parameter.", since = "0.3.0")
  def apply(property: Property[String], xs: Modifier*): JsDom.TypedTag[html.Input] =
    apply(property, None, xs:_*)

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def debounced(property: Property[String], xs: Modifier*): JsDom.TypedTag[html.Input] =
    apply(property, Some(20 millis), xs:_*)
}

/** Simple text input. */
object TextInput extends Input("text")

/** Password text input. */
object PasswordInput extends Input("password")

/** Number input. (HTML5) */
object NumberInput extends Input("number")
