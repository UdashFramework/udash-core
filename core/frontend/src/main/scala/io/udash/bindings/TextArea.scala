package io.udash.bindings

import io.udash.bindings.modifiers.TextInputsModifier
import io.udash.properties.single.Property
import org.scalajs.dom.{html, _}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Simple HTML text area with bound Property.
  */
object TextArea {
  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def apply(property: Property[String], debounce: Option[Duration], xs: Modifier*): JsDom.TypedTag[html.TextArea] = {
    val bind = new TextInputsModifier(property, debounce) {
      override def elementValue(t: Element): String =
        t.asInstanceOf[html.TextArea].value

      override def setElementValue(t: Element, v: String): Unit =
        t.asInstanceOf[html.TextArea].value = v

      override def setElementKeyUp(t: Element, callback: (KeyboardEvent) => Any): Unit =
        t.asInstanceOf[html.TextArea].onkeyup = callback

      override def setElementOnChange(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.TextArea].onchange = callback

      override def setElementOnInput(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.TextArea].oninput = callback

      override def setElementOnPaste(t: Element, callback: (Event) => Any): Unit =
        t.asInstanceOf[html.TextArea].onpaste = callback
    }

    textarea(bind, xs)
  }

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated(message = "You should use `debounced` method or explicitly pass `None` as debouncing parameter.", since = "0.3.0")
  def apply(property: Property[String], xs: Modifier*): JsDom.TypedTag[html.TextArea] =
    apply(property, None, xs:_*)

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def debounced(property: Property[String], xs: Modifier*): JsDom.TypedTag[html.TextArea] =
    apply(property, Some(20 millis), xs:_*)
}
