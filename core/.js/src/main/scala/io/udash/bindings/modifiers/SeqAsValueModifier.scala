package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom.Node

private[bindings]
class SeqAsValueModifier[T](override val property: ReadableSeqProperty[T, _ <: ReadableProperty[T]],
                            override val builder: (BSeq[T], Binding.NestedInterceptor) => BSeq[Node],
                            override val customElementsReplace: DOMManipulator.ReplaceMethod)
  extends ValueModifier[BSeq[T]] {

  def this(property: ReadableSeqProperty[T, _ <: ReadableProperty[T]], builder: BSeq[T] => BSeq[Node],
           customElementsReplace: DOMManipulator.ReplaceMethod) = {
    this(property, (data: BSeq[T], _: Binding.NestedInterceptor) => builder(data), customElementsReplace)
  }

  override def listen(callback: BSeq[T] => Unit): Registration =
    property.listen(callback)

  override def checkNull: Boolean = false // SeqProperty can not return null from `get` method

}








