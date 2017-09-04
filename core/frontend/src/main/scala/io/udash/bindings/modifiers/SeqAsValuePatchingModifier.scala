package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

private[bindings]
class SeqAsValuePatchingModifier[T, E <: ReadableProperty[T]]
                                (property: ReadableSeqProperty[T, E],
                                 initBuilder: (Seq[E], Binding => Binding) => Seq[Element],
                                 elementsUpdater: (Patch[E], Seq[Element], Binding => Binding) => Any)
  extends Binding {

  def this(property: ReadableSeqProperty[T, E], initBuilder: Seq[E] => Seq[Element], elementsUpdater: (Patch[E], Seq[Element]) => Any) = {
    this(property, (s, _) => initBuilder(s), (s, el, _) => elementsUpdater(s, el))
  }

  override def applyTo(t: Element): Unit = {
    val elements: Seq[Element] = initBuilder(property.elemProperties, nestedInterceptor)
    elements.foreach(t.appendChild)

    CallbackSequencer.finalCallback { () =>
      propertyListeners += property.listenStructure(patch => elementsUpdater(patch, elements, nestedInterceptor))
    }
  }
}






