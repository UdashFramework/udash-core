package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.bindings.Bindings._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

import scala.scalajs.js

private[bindings] trait SeqPropertyModifierUtils[T, E <: ReadableProperty[T]] extends Binding with DOMManipulator {

  protected val property: ReadableSeqProperty[T, E]
  protected def build(item: E): BSeq[Node]

  private var firstElement: Node = _
  private var firstElementIsPlaceholder = false
  private val producedElementsCount = scala.collection.mutable.ArrayBuffer[Int]()
  protected val nestedBindingsByProperty: js.Dictionary[js.Array[Binding]] = js.Dictionary.empty

  def propertyAwareNestedInterceptor(p: E)(binding: Binding): Binding = {
    super.nestedInterceptor(binding)
    binding.setup { b =>
      val id: String = p.id.toString
      if (!nestedBindingsByProperty.contains(id)) {
        nestedBindingsByProperty(id) = js.Array()
      }
      nestedBindingsByProperty(id).push(b)
    }
  }

  def clearPropertyAwareNestedInterceptor(p: E): Unit = {
    val id = p.id.toString
    if (nestedBindingsByProperty.contains(id)) {
      nestedBindingsByProperty(id).foreach(_.kill())
      nestedBindingsByProperty(id).length = 0
      nestedBindingsByProperty.remove(id)
    }
  }

  protected def indexOf(nodes: NodeList, node: Node): Int = {
    var i = 0
    while (i < nodes.length && nodes(i) != node) i += 1
    i
  }

  protected def handlePatch(root: Node)(patch: Patch[E]): Unit =
    if (patch.added.nonEmpty || patch.removed.nonEmpty) {
      // Clean up nested bindings
      patch.removed.foreach(clearPropertyAwareNestedInterceptor)

      val firstIndex = indexOf(root.childNodes, firstElement)
      val elementsBefore = producedElementsCount.slice(0, patch.idx).sum

      // Add new elements
      val newElements = patch.added.map(build)
      val newElementsFlatten: BSeq[Node] = newElements.flatten
      val insertBefore = root.childNodes(elementsBefore + firstIndex)
      if (insertBefore == null) replace(root)(Seq.empty, newElementsFlatten)
      else insert(root)(insertBefore, newElementsFlatten)

      if (firstElementIsPlaceholder) {
        if (newElementsFlatten.nonEmpty) {
          // Replace placeholder with first element of sequence
          replace(root)(Seq(insertBefore), Seq.empty)
          firstElement = newElementsFlatten.head
          firstElementIsPlaceholder = false
        }
      } else {
        // First element of sequence changed
        if (newElementsFlatten.nonEmpty && patch.idx == 0) firstElement = newElementsFlatten.head

        def childToRemoveIdx(elIdx: Int): Int =
          elIdx + firstIndex + newElementsFlatten.size + elementsBefore

        // Remove elements form second to the last
        val nodesToRemove = (1 until producedElementsCount.slice(patch.idx, patch.idx + patch.removed.size).sum)
          .map(idx => root.childNodes(childToRemoveIdx(idx)))
        replace(root)(nodesToRemove, Seq.empty)

        if (patch.clearsProperty) {
          // Replace old head of sequence with placeholder
          val newFirstElement = emptyStringNode()
          replace(root)(Seq(firstElement), Seq(newFirstElement))
          firstElement = newFirstElement
          firstElementIsPlaceholder = true
        } else {
          // Remove first element from patch.removed sequence
          if (patch.removed.nonEmpty) replace(root)(Seq(root.childNodes(childToRemoveIdx(0))), Seq.empty)

          // Update firstElement
          if (newElementsFlatten.isEmpty && patch.idx == 0)
            firstElement = root.childNodes(firstIndex + newElementsFlatten.size)
        }
      }

      val sizeChange = patch.added.size - patch.removed.size
      if (sizeChange > 0) producedElementsCount.insertAll(patch.idx, Seq.fill(sizeChange)(0))
      else producedElementsCount.remove(patch.idx, -sizeChange)
      newElements.zipWithIndex.foreach {
        case (res, idx) =>
          producedElementsCount(patch.idx + idx) = res.size
      }
    }

  override def applyTo(root: Element): Unit = {
    propertyListeners += property.listenStructure(handlePatch(root))

    property.elemProperties.foreach { element =>
      val els = build(element)
      producedElementsCount.append(els.size)
      if (firstElement == null) firstElement = els.head
      replace(root)(Seq.empty, els)
    }

    if (firstElement == null) {
      val el = emptyStringNode()
      firstElement = el
      replace(root)(Seq.empty, Seq(el))
      firstElementIsPlaceholder = true
    }
  }
}


