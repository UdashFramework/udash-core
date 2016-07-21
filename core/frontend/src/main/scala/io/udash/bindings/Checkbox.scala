package io.udash.bindings

import io.udash.properties.single.Property
import org.scalajs.dom.{html, _}

import scala.concurrent.ExecutionContext
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
  def apply(property: Property[Boolean], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.Input] = {
    val bind = new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[html.Input]
        element.checked = property.get
        property.listen(value => element.checked = value)
        element.onchange = (_: Event) => {
          property.set(element.checked)
        }
      }
    }

    input(tpe := "checkbox", bind, xs)
  }
}
