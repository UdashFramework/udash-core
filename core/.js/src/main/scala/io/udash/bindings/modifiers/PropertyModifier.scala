package io.udash.bindings.modifiers

import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom.Node

private[bindings] class PropertyModifier[T](
  override val property: ReadableProperty[T],
  override val builder: (T, Binding.NestedInterceptor) => Seq[Node],
  override val checkNull: Boolean,
  override val customElementsReplace: DOMManipulator.ReplaceMethod
) extends ValueModifier[T] {

  def this(property: ReadableProperty[T], builder: T => Seq[Node], checkNull: Boolean) = {
    this(property, (data: T, _: Binding.NestedInterceptor) => builder(data), checkNull, DOMManipulator.DefaultElementReplace)
  }

  def listen(callback: T => Unit): Registration =
    property.listen(callback)

}