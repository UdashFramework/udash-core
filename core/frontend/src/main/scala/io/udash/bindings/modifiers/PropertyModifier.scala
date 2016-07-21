package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom._

private[bindings] class PropertyModifier[T](override val property: ReadableProperty[T],
                                            override val builder: (T => Element),
                                            override val checkNull: Boolean) extends ValueModifier[T] {
  def listen(callback: T => Unit): Registration = property.listen(callback)
}










