package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqAsValuePatchingModifier[T, E <: ReadableProperty[T]]
  (property: ReadableSeqProperty[T, E],
   initBuilder: Seq[E] => Element,
   elementsUpdater: (Patch[E], JQuery) => Any) extends Modifier[dom.Element] {

  override def applyTo(t: dom.Element): Unit = {
    val root = jQ(t)
    val element = jQ(initBuilder.apply(property.elemProperties))
    root.append(element)

    CallbackSequencer.finalCallback(() => {
      property.listenStructure(patch => elementsUpdater(patch, element))
    })
  }
}






