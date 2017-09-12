package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom._

private[bindings]
trait ValueModifier[T] extends Binding {
  import Bindings._

  protected def property: ReadableProperty[T]
  protected def builder: ((T, Binding => Binding) => Seq[Node])
  protected def checkNull: Boolean
  protected def listen(callback: T => Unit): Registration

  protected def replace(root: Node)(oldElements: Seq[Node], newElements: Seq[Node]): Unit =
    root.replaceChildren(oldElements, newElements)

  override def applyTo(t: dom.Element): Unit = {
    var elements: Seq[Node] = Seq.empty

    def rebuild(propertyValue: T): Unit = {
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














