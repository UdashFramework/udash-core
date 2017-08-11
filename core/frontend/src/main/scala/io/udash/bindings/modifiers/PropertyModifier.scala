package io.udash.bindings.modifiers

import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom

private[bindings]
class PropertyModifier[T](override val property: ReadableProperty[T],
                          override val builder: ((T, Binding => Binding) => Seq[dom.Element]),
                          override val checkNull: Boolean)
  extends ValueModifier[T] {

  def this(property: ReadableProperty[T], builder: (T => Seq[dom.Element]), checkNull: Boolean) = {
    this(property, (data: T, _: Binding => Binding) => builder(data), checkNull)
  }

  def listen(callback: T => Unit): Registration =
    property.listen(callback)

}










