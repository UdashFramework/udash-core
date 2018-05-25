package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.{Element, Event, KeyboardEvent}
import org.scalajs.dom.html.TextArea
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.duration.{Duration, DurationInt}

/** Simple HTML text area with bound Property.*/
object TextArea {
  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def apply(property: Property[String], debounce: Duration = 20 millis)(xs: Modifier*): InputBinding[TextArea] =
    new InputBinding[TextArea] {
      private val element = textarea(
        nestedInterceptor(new TextAreaModifier(property, Some(debounce))), xs
      ).render

      override def render: TextArea = element
    }

  /**
    * @param property Property to bind.
    * @param debounce Property update timeout after input changes.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated("Use `apply` returning `InputBinding` instead.", "0.7.0")
  def apply(property: Property[String], debounce: Option[Duration], xs: Modifier*): JsDom.TypedTag[TextArea] = {
    textarea(new TextAreaModifier(property, debounce), xs)
  }

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  @deprecated("Use `apply` returning `InputBinding` instead.", "0.7.0")
  def debounced(property: Property[String], xs: Modifier*): JsDom.TypedTag[TextArea] =
    apply(property, Some(20 millis), xs:_*)

  private class TextAreaModifier(property: Property[String], debounce: Option[Duration])
    extends TextInputsModifier(property, debounce)  {

    override def elementValue(t: Element): String =
      t.asInstanceOf[TextArea].value

    override def setElementValue(t: Element, v: String): Unit =
      t.asInstanceOf[TextArea].value = v

    override def setElementKeyUp(t: Element, callback: KeyboardEvent => Any): Unit =
      t.asInstanceOf[TextArea].onkeyup = callback

    override def setElementOnChange(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[TextArea].onchange = callback

    override def setElementOnInput(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[TextArea].oninput = callback

    override def setElementOnPaste(t: Element, callback: Event => Any): Unit =
      t.asInstanceOf[TextArea].onpaste = callback
  }
}
