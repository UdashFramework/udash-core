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
  private val producedElementsCount = MArrayBuffer[Int]()
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

  protected def indexOf(nodes: NodeList, node: Node): Int = {
    var i = 0
    while (i < nodes.length && nodes(i) != node) i += 1
    i
  }

  protected def handlePatch(root: Node)(patch: Patch[E]): Unit =
    if (patch.added.nonEmpty || patch.removed.nonEmpty) {
      // Clean up nested bindings
      patch.removed.foreach(clearPropertyAwareNestedInterceptor)

      //index of the first element produced by the binding
      val firstIndex = indexOf(root.childNodes, firstElement)

      //number of nodes produced by properties before patch index
      val elementsBefore = producedElementsCount.iterator.slice(0, patch.idx).sum

      //total number of produced nodes
      val allElements = elementsBefore + producedElementsCount.iterator.drop(patch.idx).sum

      // Add new elements
      val newElements = patch.added.map(build)
      val newElementsFlatten: Seq[Node] = newElements.flatten
      root.childNodes(elementsBefore + firstIndex).opt match {
        case Opt(insertBefore) => insert(root)(insertBefore, newElementsFlatten)
        case Opt.Empty => replace(root)(Seq.empty, newElementsFlatten)
      }

      if (firstElementIsPlaceholder) {
        //first element was a placeholder => there's nothing to remove in the patch
        if (newElementsFlatten.nonEmpty) {
          // there is a new element - remove placeholder
          replace(root)(Seq(firstElement), Seq.empty)
          firstElementIsPlaceholder = false
        }
      } else if (patch.removed.nonEmpty) {
        def childToRemoveIdx(elIdx: Int): Int = elIdx + firstIndex + newElementsFlatten.size + elementsBefore

        val nodesToRemove = (0 until producedElementsCount.slice(patch.idx, patch.idx + patch.removed.size).sum)
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
      val els = build(element)
      producedElementsCount.append(els.size)
      if (firstElement == null) firstElement = els.head
      replace(root)(Seq.empty, els)
    }
  }
}


