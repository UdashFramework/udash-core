package io.udash.bindings.modifiers

import io.udash.properties.single.Property
import org.scalajs.dom._

import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scalatags.JsDom

/** Template of binding for text inputs. */
private[bindings] abstract class TextInputsModifier(property: Property[String], debounce: Option[Duration]) extends JsDom.Modifier {
  def elementValue(t: Element): String
  def setElementValue(t: Element, v: String): Unit
  def setElementKeyUp(t: Element, callback: KeyboardEvent => Any): Unit
  def setElementOnChange(t: Element, callback: Event => Any): Unit
  def setElementOnInput(t: Element, callback: (Event) => Any): Unit
  def setElementOnPaste(t: Element, callback: (Event) => Any): Unit

  override def applyTo(t: Element): Unit = {
    if (property.get != null) setElementValue(t, property.get)

    property.listen(value => {
      if (elementValue(t) != value) setElementValue(t, value)
    })

    var propertyUpdateHandler: Int = 0
    val callback = if (debounce.nonEmpty) {
      (_: Event) => {
        val value: String = elementValue(t)
        if (propertyUpdateHandler != 0) window.clearTimeout(propertyUpdateHandler)
        propertyUpdateHandler = window.setTimeout(() => if (property.get != value) property.set(value), debounce.get.toMillis)
      }
    } else {
      (_: Event) => {
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
