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
  def builder: (T => Seq[dom.Element])
  def checkNull: Boolean
  def listen(callback: T => Unit): Registration

  override def applyTo(t: dom.Element): Unit = {
    var elements: Seq[Element] = null

    def rebuild() = {
      val oldEls = elements
      val propertyValue: T = property.get

      elements = if (checkNull && propertyValue == null) emptyStringNode() else builder(propertyValue)
      if (elements.isEmpty) elements = emptyStringNode()

      t.replaceChildren(oldEls, elements)
    }

    listen(_ => rebuild())
    rebuild()
  }
}














