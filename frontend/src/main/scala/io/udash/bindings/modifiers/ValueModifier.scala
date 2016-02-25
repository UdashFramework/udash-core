package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.utils.Registration
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] trait ValueModifier[T] extends Modifier[dom.Element] with Bindings {
  def property: ReadableProperty[T]
  def builder: (T => Element)
  def checkNull: Boolean
  def listen(callback: T => Unit): Registration

  override def applyTo(t: dom.Element): Unit = {
    val root = jQ(t)
    var element: JQuery = null

    def rebuild() = {
      val oldEl = element
      element = if (checkNull && property.get == null) jQ(emptyStringNode()) else jQ(builder(property.get))
      if (oldEl == null) root.append(element)
      else oldEl.replaceWith(element)
    }

    listen(_ => rebuild())
    rebuild()
  }
}














