package io.udash.bindings.inputs

import io.udash.properties.single.Property
import org.scalajs.dom.{Element, Event}
import org.scalajs.dom.html.{Input => JSInput}
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Plain checkbox with bidirectionally binding with Property.
  *
  * For SeqProperty look at [[io.udash.bindings.CheckButtons]]
  */
object Checkbox {
  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on type, checked and onchange attributes.
    * @return HTML input (checkbox) tag with bound Property and applied modifiers.
    */
  def apply(property: Property[Boolean], xs: Modifier*): JsDom.TypedTag[JSInput] = {
    val bind = new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[JSInput]
        element.checked = property.get
        property.listen(element.checked = _)
        element.onchange = (_: Event) => property.set(element.checked)
      }
    }

    input(tpe := "checkbox", bind, xs)
  }
}
