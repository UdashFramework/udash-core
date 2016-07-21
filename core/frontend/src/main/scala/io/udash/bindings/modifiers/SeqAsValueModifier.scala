package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom._

private[bindings] class SeqAsValueModifier[T](override val property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                                              override val builder: Seq[T] => Element) extends ValueModifier[Seq[T]] {
  override def listen(callback: Seq[T] => Unit): Registration = property.listen(callback)
  override def checkNull: Boolean = false // SeqProperty can not return null from `get` method
}








