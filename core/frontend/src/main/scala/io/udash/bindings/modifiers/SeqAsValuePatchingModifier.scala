package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqAsValuePatchingModifier[T, E <: ReadableProperty[T]]
  (property: ReadableSeqProperty[T, E],
   initBuilder: Seq[E] => Element,
   elementsUpdater: (Patch[E], Element) => Any) extends Modifier[dom.Element] {

  override def applyTo(t: dom.Element): Unit = {
    val element = initBuilder.apply(property.elemProperties)
    t.appendChild(element)

    CallbackSequencer.finalCallback(() => {
      property.listenStructure(patch => elementsUpdater(patch, element))
    })
  }
}






