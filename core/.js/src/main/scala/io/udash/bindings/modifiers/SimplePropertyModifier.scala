package io.udash.bindings.modifiers

import io.udash._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom

private[bindings] class SimplePropertyModifier[T](property: ReadableProperty[T])
  extends PropertyModifier[T](
    property,
    t => dom.document.createTextNode(t.toString),
    true
  )












