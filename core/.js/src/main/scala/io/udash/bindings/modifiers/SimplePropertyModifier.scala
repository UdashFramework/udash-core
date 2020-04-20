package io.udash.bindings.modifiers

import io.udash._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom

private[bindings] final class SimplePropertyModifier(property: ReadableProperty[_]) extends PropertyModifier[Any](
  property,
  t => dom.document.createTextNode(t.toString),
  checkNull = true,
  DOMManipulator.DefaultElementReplace
)
