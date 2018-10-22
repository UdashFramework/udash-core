package io.udash.bindings.modifiers

import io.udash._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom

private[bindings] class SimplePropertyModifier[T](property: ReadableProperty[T], checkNull: Boolean)
  extends PropertyModifier[T](
    property,
    (t: T) => {
      if (t != null) dom.document.createTextNode(t.toString)
      else emptyStringNode()
    },
    checkNull
  )












