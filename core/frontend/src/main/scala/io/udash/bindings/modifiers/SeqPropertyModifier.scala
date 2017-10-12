package io.udash.bindings.modifiers

import com.avsystem.commons.SharedExtensions._
import io.udash.bindings.Bindings._
import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

import scala.scalajs.js

private[bindings]
class SeqPropertyModifier[T, E <: ReadableProperty[T]](override val property: ReadableSeqProperty[T, E],
                                                       builder: (E, Binding => Binding) => Seq[Element])
  extends SeqPropertyModifierUtils[T, E] {

  def this(property: ReadableSeqProperty[T, E], builder: E => Seq[Element]) = {
    this(property, (d, _) => builder(d))
  }

  protected def build(item: E): Seq[Element] =
    builder(item, propertyAwareNestedInterceptor(item))
}


