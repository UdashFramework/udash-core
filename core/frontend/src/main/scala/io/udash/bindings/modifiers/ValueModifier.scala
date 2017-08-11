package io.udash.bindings.modifiers

import com.avsystem.commons.SharedExtensions._
import io.udash.bindings._
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js

private[bindings]
trait ValueModifier[T] extends Binding {
  import Bindings._

  protected def property: ReadableProperty[T]
  protected def builder: ((T, Binding => Binding) => Seq[dom.Element])
  protected def checkNull: Boolean
  protected def listen(callback: T => Unit): Registration

  protected def replace(root: Element)(oldElements: Seq[Element], newElements: Seq[Element]): Unit =
    root.replaceChildren(oldElements, newElements)

  override def applyTo(t: dom.Element): Unit = {
    var elements: Seq[Element] = Seq.empty

    def rebuild(propertyValue: T) = {
      killNestedBindings()

      val oldEls = elements
      elements = {
        if (checkNull && propertyValue == null) emptyStringNode()
        else builder(propertyValue, nestedInterceptor)
      }
      if (elements.isEmpty) elements = emptyStringNode()

      replace(t)(oldEls, elements)
    }

    propertyListeners.push(listen(rebuild))
    rebuild(property.get)
  }
}














