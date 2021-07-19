package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.bindings.Bindings._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

import scala.scalajs.js

private[bindings] trait SeqPropertyModifierUtils[T, E <: ReadableProperty[T]] extends Binding with DOMManipulator {

  protected val property: ReadableSeqProperty[T, E]
  protected def build(item: E): Seq[Node]

  private var firstElement: Node = _
  private var firstElementIsPlaceholder = false
  private val producedElementsCount = js.Array[Int]()
  private val nestedBindingsByProperty: MHashMap[E, js.Array[Binding]] = MHashMap.empty

  def propertyAwareNestedInterceptor(p: E)(binding: Binding): Binding = {
    super.nestedInterceptor(binding)
    binding.setup { b =>
      nestedBindingsByProperty.getOrElseUpdate(p, js.Array()).push(b)
    }
  }

  def clearPropertyAwareNestedInterceptor(p: E): Unit = {
    nestedBindingsByProperty.remove(p).foreach { bindings =>
      bindings.foreach(_.kill())
      bindings.length = 0
    }
  }

  //todo remove
  @inline private def indexOf(nodes: NodeList, node: Node): Int = {
    var i = 0
    while (i < nodes.length && !nodes(i).eq(node)) i += 1
    i
  }

  protected def handlePatch(root: Node)(patch: Patch[E]): Unit =
    if (patch.added.nonEmpty || patch.removed.nonEmpty) {
      // Clean up nested bindings
      patch.removed.foreach(clearPropertyAwareNestedInterceptor)

      //index of the first element produced by the binding
      val firstIndex = indexOf(root.childNodes, firstElement)

      //number of nodes produced by properties before patch index
      val elementsBefore = producedElementsCount.jsSlice(0, patch.idx).sum

      //total number of produced nodes
      val allElements = elementsBefore + producedElementsCount.iterator.drop(patch.idx).sum

      // Add new elements
      val newElements = patch.added.map(p => defragment(build(p)))
      val newElementsFlatten: Seq[Node] = newElements.flatten
      if (newElementsFlatten.nonEmpty) {
        if (firstElementIsPlaceholder) {
          replace(root)(Seq(firstElement), newElementsFlatten)
          firstElementIsPlaceholder = false
        } else insert(root)(root.childNodes(elementsBefore + firstIndex), newElementsFlatten)
      }

      if (patch.removed.nonEmpty) {
        def childToRemoveIdx(elIdx: Int): Int = elIdx + firstIndex + newElementsFlatten.size + elementsBefore

        val nodesToRemove = (0 until producedElementsCount.jsSlice(patch.idx, patch.idx + patch.removed.size).sum)
          .map(idx => root.childNodes(childToRemoveIdx(idx)))

        val replacement = {
          // if no new elements were added and all old ones are to be removed, add a placeholder
          firstElementIsPlaceholder = true
          emptyStringNode()
        }.optIf(patch.added.isEmpty && allElements == nodesToRemove.size)

        replace(root)(nodesToRemove, replacement.toSeq)
      }

      firstElement = root.childNodes(firstIndex)

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
      val els = defragment(build(element))
      producedElementsCount.push(els.size)
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


