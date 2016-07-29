package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqAsValuePatchingModifier[T, E <: ReadableProperty[T]]
  (property: ReadableSeqProperty[T, E],
   initBuilder: Seq[E] => Seq[Element],
   elementsUpdater: (Patch[E], Seq[Element]) => Any) extends Modifier[dom.Element] {

  override def applyTo(t: dom.Element): Unit = {
    val elements: Seq[Element] = initBuilder.apply(property.elemProperties)
    elements.foreach(t.appendChild)

    CallbackSequencer.finalCallback(() => {
      property.listenStructure(patch => elementsUpdater(patch, elements))
    })
  }
}






