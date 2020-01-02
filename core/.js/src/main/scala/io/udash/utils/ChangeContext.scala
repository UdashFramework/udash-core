package io.udash
package utils

import com.avsystem.commons._
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.raw._
import org.scalajs.dom.{MutationObserver, MutationObserverInit}

object ChangeContext {

  private val bindings: MHashMap[Node, MBuffer[Binding]] = MHashMap.empty

  private def cleanup(removedNode: Node): Unit = {
    val nodeBindings = bindings.remove(removedNode).toList.flatten
    if (nodeBindings.nonEmpty) {
      println("R " + nodeBindings -> removedNode.nodeName + " " + bindings.valuesIterator.map(_.size).sum)
      nodeBindings.foreach(_.kill())
    }
  }

  private val observer = new MutationObserver((records, _) => {
    records.foreach { v =>
      for (i <- 0 until v.removedNodes.length) {
        val node = v.removedNodes.item(i)
        cleanup(node)
      }
    }
  })
  private var active = false

  def bind(node: Node, binding: Binding): Unit = {
    if (active) {
      bindings.getOrElseUpdate(node, CrossCollections.createArray) += binding
      println("A " + binding.getClass.getSimpleName + " " + node.asInstanceOf[Element].outerHTML + " " + bindings.valuesIterator.map(_.size).sum)
    }
  }

  def init(scope: Node): Unit = {
    stop()
    active = true
    observer.observe(scope, MutationObserverInit(childList = true, subtree = true))
  }

  def stop(): Unit = {
    active = false
    bindings.clear()
    observer.disconnect()
  }
}