package io.udash.bindings.inputs

import io.udash.bindings.modifiers.Binding
import io.udash.properties.single.Property
import org.scalajs.dom._

import scala.concurrent.duration.Duration

/** Template of binding for text inputs. */
private[bindings] abstract class TextInputsModifier(property: Property[String], debounce: Option[Duration]) extends Binding {
  def elementValue(t: Element): String
  def setElementValue(t: Element, v: String): Unit
  def setElementKeyUp(t: Element, callback: KeyboardEvent => Unit): Unit
  def setElementOnChange(t: Element, callback: Event => Unit): Unit
  def setElementOnInput(t: Element, callback: Event => Unit): Unit
  def setElementOnPaste(t: Element, callback: Event => Unit): Unit

  override def applyTo(t: Element): Unit = {
    if (property.get != null) setElementValue(t, property.get)

    propertyListeners += property.listen { value =>
      if (elementValue(t) != value) setElementValue(t, value)
    }

    var propertyUpdateHandler: Int = 0
    val callback = if (debounce.nonEmpty && debounce.get.toMillis > 0) {
      _: Event => {
        if (propertyUpdateHandler != 0) window.clearTimeout(propertyUpdateHandler)
        propertyUpdateHandler = window.setTimeout(() => {
          val value: String = elementValue(t)
          if (property.get != value) property.set(value)
        }, debounce.get.toMillis.toDouble)
      }
    } else {
      _: Event => {
        val value: String = elementValue(t)
        if (property.get != value) property.set(value)
      }
    }
    setElementKeyUp(t, callback)
    setElementOnChange(t, callback)
    setElementOnInput(t, callback)
    setElementOnPaste(t, callback)
  }
}
