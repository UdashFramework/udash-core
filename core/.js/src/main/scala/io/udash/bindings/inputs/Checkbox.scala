package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.Event
import org.scalajs.dom.html.{Input => JSInput}
import scalatags.JsDom.all._

/**
  * Plain checkbox bidirectionally bound to Property.
  *
  * For SeqProperty take a look at [[io.udash.bindings.inputs.CheckButtons]]
  */
object Checkbox {
  /**
    * @param selected Property to bind.
    * @param inputModifiers Additional Modifiers, don't use modifiers on type, checked and onchange attributes.
    * @return HTML input (checkbox) tag with bound Property and applied modifiers.
    */
  def apply(selected: Property[Boolean])(inputModifiers: Modifier*): InputBinding[JSInput] = {
    new InputBinding[JSInput] {
      private val in = input(inputModifiers, tpe := "checkbox").render

      propertyListeners += selected.listen(in.checked = _, initUpdate = true)
      in.onchange = (_: Event) => selected.set(in.checked)

      override def render: JSInput = in
    }
  }
}
