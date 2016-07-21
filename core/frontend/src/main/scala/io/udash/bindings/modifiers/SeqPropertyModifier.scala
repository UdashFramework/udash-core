package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqPropertyModifier[T, E <: ReadableProperty[T]](property: ReadableSeqProperty[T, E],
                                                                         builder: E => Element) extends Modifier[dom.Element] with Bindings {
  private def indexOf(nodes: NodeList, node: Node): Int = {
    var i = 0
    while (i < nodes.length && nodes(i) != node) i += 1
    i
  }

  override def applyTo(root: Element): Unit = {
    var firstElement: Node = null
    var firstElementIsPlaceholder = false

    CallbackSequencer.finalCallback(() => {
      property.listenStructure((patch: Patch[E]) => if (patch.added.nonEmpty || patch.removed.nonEmpty) {
        val firstIndex = indexOf(root.childNodes, firstElement)

        // Add new elements
        val newElements = patch.added.map(builder.apply)
        val insertBefore = root.childNodes(patch.idx + firstIndex)
        if (insertBefore == null) newElements.foreach(el => root.appendChild(el))
        else newElements.foreach(el => root.insertBefore(el, insertBefore))

        if (firstElementIsPlaceholder) {
          // Replace placeholder with first element of sequence
          root.removeChild(insertBefore)
          firstElement = newElements.head
          firstElementIsPlaceholder = false
        } else {
          // First element of sequence changed
          if (patch.added.nonEmpty && patch.idx == 0) firstElement = newElements.head

          val toRemove = for (i <- patch.removed.indices) yield i + patch.idx
          // Remove elements form second to the last
          toRemove.slice(1, toRemove.size)
            .map(idx => root.childNodes(idx + firstIndex + patch.added.size))
            .foreach(root.removeChild)
          if (patch.clearsProperty) {
            // Replace old head of sequence with placeholder
            val newFirstElement = emptyStringNode()
            root.replaceChild(newFirstElement, firstElement)
            firstElement = newFirstElement
            firstElementIsPlaceholder = true
          } else {
            // Remove first element from patch.removed sequence
            if (patch.removed.nonEmpty) root.removeChild(root.childNodes(firstIndex + patch.added.size + patch.idx))

            // Update firstElement
            if (patch.added.isEmpty && patch.idx == 0) firstElement = root.childNodes(firstIndex + patch.added.size)
          }
        }
      })
    })

    property.elemProperties.foreach(element => {
      val el = builder.apply(element)
      if (firstElement == null) firstElement = el
      root.appendChild(el)
    })

    if (firstElement == null) {
      val el = emptyStringNode()
      firstElement = el
      root.appendChild(el)
      firstElementIsPlaceholder = true
    }
  }
}


