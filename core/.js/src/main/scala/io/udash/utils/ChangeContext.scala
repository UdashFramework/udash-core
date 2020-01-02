package io.udash
package utils

import com.avsystem.commons._
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.raw._
import org.scalajs.dom.{MutationObserver, MutationObserverInit}

object ChangeContext {

  private val bindings: MHashMap[Node, MBuffer[Binding]] = MHashMap.empty

  //todo tail
  private def cleanup(removedNodes: NodeList): Unit = {
    for (i <- 0 until removedNodes.length) {
      val node = removedNodes.item(i)
      val nodeBindings = bindings.remove(node).toList.flatten
      if (nodeBindings.nonEmpty) {
        println("R " + nodeBindings -> node.nodeName + " " + bindings.valuesIterator.map(_.size).sum)
        nodeBindings.foreach(_.kill())
      }

      cleanup(node.childNodes)
    }
  }

  private val observer = new MutationObserver((records, _) => records.foreach(v => cleanup(v.removedNodes)))
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