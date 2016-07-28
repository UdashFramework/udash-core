package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] trait ValueModifier[T] extends Modifier[dom.Element] with Bindings {
  def property: ReadableProperty[T]
  def builder: (T => Element)
  def checkNull: Boolean
  def listen(callback: T => Unit): Registration

  override def applyTo(t: dom.Element): Unit = {
    var element: Element = null

    def rebuild() = {
      val oldEl = element
      element = if (checkNull && property.get == null) emptyStringNode() else builder(property.get)
      if (oldEl == null) t.appendChild(element)
      else t.replaceChild(element, oldEl)
    }

    listen(_ => rebuild())
    rebuild()
  }
}














