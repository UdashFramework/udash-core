package io.udash.bindings.modifiers

import io.udash.properties.Property
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scalatags.JsDom

/** Template of binding for text inputs. */
private[bindings] abstract class TextInputsModifier(property: Property[String]) extends JsDom.Modifier {
  def elementValue(t: Element): String
  def setElementValue(t: Element, v: String): Unit
  def setElementKeyUp(t: Element, callback: KeyboardEvent => Any): Unit
  def setElementOnChange(t: Element, callback: Event => Any): Unit

  override def applyTo(t: Element): Unit = {
    if (property.get != null) setElementValue(t, property.get)

    property.listen(value => {
      if (elementValue(t) != value) setElementValue(t, value)
    })

    val callback = (_: Event) => {
      val value: String = elementValue(t)
      if (property.get != value) property.set(value)
    }
    setElementKeyUp(t, callback)
    setElementOnChange(t, callback)
  }
}
