package io.udash.bindings

import io.udash.bindings.modifiers.TextInputsModifier
import io.udash.properties.Property
import org.scalajs.dom.{html, _}

import scala.concurrent.ExecutionContext
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Simple HTML text area with bound Property.
  */
object TextArea {
  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and onkeyup attributes.
    * @return HTML textarea with bound Property, applied modifiers and nested options.
    */
  def apply(property: Property[String], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.TextArea] = {
    val bind = new TextInputsModifier(property) {
      override def elementValue(t: Element): String = t.asInstanceOf[html.TextArea].value
      override def setElementValue(t: Element, v: String): Unit = t.asInstanceOf[html.TextArea].value = v
      override def setElementKeyUp(t: Element, callback: (KeyboardEvent) => Any): Unit = t.asInstanceOf[html.TextArea].onkeyup = callback
      override def setElementOnChange(t: Element, callback: (Event) => Any): Unit = t.asInstanceOf[html.TextArea].onchange = callback
    }

    textarea(bind, xs)
  }
}
