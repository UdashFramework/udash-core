package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqPropertyModifier[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                                         builder: E => Element) extends Modifier[dom.Element] with Bindings {
  override def applyTo(t: Element): Unit = {
    val root = jQ(t)
    var firstElement: JQuery = null
    var firstElementIsPlaceholder = false

    CallbackSequencer.finalCallback(() => {
      property.listenStructure((patch: Patch[E]) => if (patch.added.nonEmpty || patch.removed.nonEmpty) {
        val firstIndex = root.contents().index(firstElement)

        // Add new elements
        val newElements = patch.added.map(builder.apply)
        val insertBefore = root.contents().at(patch.idx + firstIndex)
        if (insertBefore.length == 0) newElements.foreach(el => root.append(el))
        else newElements.foreach(el => jQ(el).insertBefore(insertBefore))

        if (firstElementIsPlaceholder) {
          // Replace placeholder with first element of sequence
          insertBefore.remove()
          firstElement = jQ(newElements.head)
          firstElementIsPlaceholder = false
        } else {
          // First element of sequence changed
          if (patch.added.nonEmpty && patch.idx == 0) firstElement = jQ(newElements.head)

          val toRemove = for (i <- patch.removed.indices) yield i + patch.idx
          // Remove elements form second to the last
          toRemove.slice(1, toRemove.size).map(idx => jQ(root.contents().at(idx + firstIndex + patch.added.size))).foreach(_.remove())
          if (patch.clearsProperty) {
            // Replace old head of sequence with placeholder
            val newFirstElement = jQ(emptyStringNode())
            firstElement.replaceWith(newFirstElement)
            firstElement = newFirstElement
            firstElementIsPlaceholder = true
          } else {
            // Remove first element from patch.removed sequence
            if (patch.removed.nonEmpty) root.contents().at(firstIndex + patch.added.size + patch.idx).remove()

            // Update firstElement
            if (patch.added.isEmpty && patch.idx == 0) firstElement = root.contents().at(firstIndex + patch.added.size)
          }
        }
      })
    })

    property.elemProperties.foreach(element => {
      val el = builder.apply(element)
      if (firstElement == null) firstElement = jQ(el)
      root.append(el)
    })

    if (firstElement == null) {
      val el = emptyStringNode()
      firstElement = jQ(el)
      root.append(el)
      firstElementIsPlaceholder = true
    }
  }
}


