package io.udash.bindings.modifiers

import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

private[bindings]
class AttrModifier[T](property: ReadableProperty[T], updater: ((T, Element) => Any)) extends Binding {
  override def applyTo(element: Element): Unit = {
    def rebuild() = updater(property.get, element)

    propertyListeners += property.listen(_ => rebuild())
    rebuild()
  }
}
