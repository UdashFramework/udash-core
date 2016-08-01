package io.udash.bindings.modifiers

import io.udash.bindings._
import io.udash.properties._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic._

private[bindings] class SeqPropertyModifier[T, E <: ReadableProperty[T]]
  (property: ReadableSeqProperty[T, E], builder: E => Seq[Element]) extends Modifier[dom.Element] with Bindings {

  private def indexOf(nodes: NodeList, node: Node): Int = {
    var i = 0
    while (i < nodes.length && nodes(i) != node) i += 1
    i
  }

  override def applyTo(root: Element): Unit = {
    var firstElement: Node = null
    var firstElementIsPlaceholder = false
    val producedElementsCount = scala.collection.mutable.ArrayBuffer[Int]()

    CallbackSequencer.finalCallback(() => {
      property.listenStructure((patch: Patch[E]) => if (patch.added.nonEmpty || patch.removed.nonEmpty) {
        val firstIndex = indexOf(root.childNodes, firstElement)
        val elementsBefore = producedElementsCount.slice(0, patch.idx).sum

        // Add new elements
        val newElements = patch.added.map(builder.apply)
        val newElementsFlatten: Seq[Element] = newElements.flatten
        val insertBefore = root.childNodes(elementsBefore + firstIndex)
        if (insertBefore == null) newElementsFlatten.foreach(root.appendChild)
        else newElementsFlatten.foreach(el => root.insertBefore(el, insertBefore))

        if (firstElementIsPlaceholder) {
          if (newElementsFlatten.nonEmpty) {
            // Replace placeholder with first element of sequence
            root.removeChild(insertBefore)
            firstElement = newElementsFlatten.head
            firstElementIsPlaceholder = false
          }
        } else {
          // First element of sequence changed
          if (newElementsFlatten.nonEmpty && patch.idx == 0) firstElement = newElementsFlatten.head

          def childToRemoveIdx(elIdx: Int): Int =
            elIdx + firstIndex + newElementsFlatten.size + elementsBefore

          // Remove elements form second to the last
          (1 until producedElementsCount.slice(patch.idx, patch.idx + patch.removed.size).sum)
            .map(idx => root.childNodes(childToRemoveIdx(idx)))
            .foreach(root.removeChild)

          if (patch.clearsProperty) {
            // Replace old head of sequence with placeholder
            val newFirstElement = emptyStringNode()
            root.replaceChild(newFirstElement, firstElement)
            firstElement = newFirstElement
            firstElementIsPlaceholder = true
          } else {
            // Remove first element from patch.removed sequence
            if (patch.removed.nonEmpty) root.removeChild(root.childNodes(childToRemoveIdx(0)))

            // Update firstElement
            if (newElementsFlatten.isEmpty && patch.idx == 0)
              firstElement = root.childNodes(firstIndex + newElementsFlatten.size)
          }
        }

        val sizeChange = patch.added.size - patch.removed.size
        if (sizeChange > 0) producedElementsCount.insert(patch.idx, Seq.fill(sizeChange)(0):_*)
        else producedElementsCount.remove(patch.idx, -sizeChange)
        newElements.zipWithIndex.foreach {
          case (res, idx) =>
            producedElementsCount(patch.idx + idx) = res.size
        }
      })
    })

    property.elemProperties.foreach(element => {
      val els = builder.apply(element)
      producedElementsCount.append(els.size)
      if (firstElement == null) firstElement = els.head
      els.foreach(root.appendChild)
    })

    if (firstElement == null) {
      val el = emptyStringNode()
      firstElement = el
      root.appendChild(el)
      firstElementIsPlaceholder = true
    }
  }
}


