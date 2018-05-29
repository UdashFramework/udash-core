package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.{Element, Event}
import org.scalajs.dom.html.{Input => JSInput}
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Plain checkbox bidirectionally bound to Property.
  *
  * For SeqProperty take a look at [[io.udash.bindings.CheckButtons]]
  */
object Checkbox {
  /**
    * @param selected Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on type, checked and onchange attributes.
    * @return HTML input (checkbox) tag with bound Property and applied modifiers.
    */
  def apply(selected: Property[Boolean])(xs: Modifier*): InputBinding[JSInput] = {
    new InputBinding[JSInput] {
      private val in = input(
        xs, tpe := "checkbox",
        nestedInterceptor((checked := "checked").attrIf(selected))
      ).render

      in.onchange = (_: Event) => selected.set(in.checked)

      override def render: JSInput = in
    }
  }

  /**
    * @param property Property to bind.
    * @param xs Additional Modifiers, don't use modifiers on type, checked and onchange attributes.
    * @return HTML input (checkbox) tag with bound Property and applied modifiers.
    */
  @deprecated("Use the constructor with dynamic options set and generic element type.", "0.7.0")
  def deprecated(property: Property[Boolean], xs: Modifier*): TypedTag[JSInput] = {
    val bind = new Modifier {
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
