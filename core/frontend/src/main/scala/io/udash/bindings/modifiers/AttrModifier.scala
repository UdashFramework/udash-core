package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class AttrModifier[T](property: ReadableProperty[T], updater: ((T, Element) => Any)) extends Modifier[dom.Element] with Bindings {
  override def applyTo(element: Element): Unit = {
    def rebuild() = updater(property.get, element)

    property.listen(_ => rebuild())
    rebuild()
  }
}
