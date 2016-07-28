package io.udash.bindings.modifiers

import io.udash._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom

import scalatags.JsDom

private[bindings] class SimplePropertyModifier[T](property: ReadableProperty[T], checkNull: Boolean) extends PropertyModifier[T](property, (t: T) => {
  if (t != null) JsDom.StringFrag(t.toString).render.asInstanceOf[dom.Element]
  else emptyStringNode()
}, checkNull)












