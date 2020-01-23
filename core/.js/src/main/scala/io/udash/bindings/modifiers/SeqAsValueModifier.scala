package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom.Node

private[bindings] final class SeqAsValueModifier[T](
  override val property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
  build: (Seq[T], Binding.NestedInterceptor) => Seq[Node],
  override val customElementsReplace: DOMManipulator.ReplaceMethod
) extends ValueModifier[BSeq[T]] {

  override protected def builder: (BSeq[T], NestedInterceptor) => Seq[Node] = (data, interceptor) => build(data.toSeq, interceptor)

  def this(property: ReadableSeqProperty[T, _ <: ReadableProperty[T]], builder: Seq[T] => Seq[Node],
    customElementsReplace: DOMManipulator.ReplaceMethod) = {
    this(property, (data: Seq[T], _: Binding.NestedInterceptor) => builder(data), customElementsReplace)
  }

  override def listen(callback: BSeq[T] => Unit): Registration =
    property.listen(callback)

  override def checkNull: Boolean = false // SeqProperty can not return null from `get` method

}