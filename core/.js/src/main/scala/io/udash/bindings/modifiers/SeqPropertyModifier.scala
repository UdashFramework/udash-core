package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

private[bindings] final class SeqPropertyModifier[T, E <: ReadableProperty[T]](
  override val property: ReadableSeqProperty[T, E],
  builder: (E, Binding.NestedInterceptor) => BSeq[Node],
  override val customElementsReplace: DOMManipulator.ReplaceMethod,
  override val customElementsInsert: DOMManipulator.InsertMethod
) extends SeqPropertyModifierUtils[T, E] {

  def this(property: ReadableSeqProperty[T, E], builder: E => BSeq[Node],
           customElementsReplace: DOMManipulator.ReplaceMethod,
           customElementsInsert: DOMManipulator.InsertMethod) = {
    this(property, (d, _) => builder(d), customElementsReplace, customElementsInsert)
  }

  protected def build(item: E): BSeq[Node] =
    builder(item, propertyAwareNestedInterceptor(item))
}


