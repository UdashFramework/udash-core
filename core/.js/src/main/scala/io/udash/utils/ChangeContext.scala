package io.udash
package utils

import com.avsystem.commons._
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.raw._
import org.scalajs.dom.{MutationObserver, MutationObserverInit}

import scala.scalajs.js

object ChangeContext {

  final val NodeAdded = "NodeAdded"
  final val NodeRemoved = "NodeRemoved"

  private val bindings: MHashMap[Node, MBuffer[Binding]] = MHashMap.empty
  private val observer = new MutationObserver((records, _) => {
    records.foreach { v =>
      for (i <- 0 until v.removedNodes.length) {
        val node = v.removedNodes.item(i)
        val nodeBindings = bindings.remove(node).toList.flatten
        println("R " + nodeBindings -> node.nodeName + " " + bindings.valuesIterator.map(_.size).sum)
        nodeBindings.foreach(_.kill())
      }
      for (i <- 0 until v.addedNodes.length) {
        val node = v.addedNodes.item(i)
        val nodeBindings = bindings.get(node).toList.flatten
        println("A " + nodeBindings -> node.nodeName)
      }
    }
  })
  private var active = false

  def bind(node: Node, binding: Binding): Unit = {
    if (active) bindings.getOrElseUpdate(node, CrossCollections.createArray) += binding
  }

  def init(): Unit = {
    stop()
    active = true
    observer.observe(org.scalajs.dom.document.body, MutationObserverInit(childList = true, subtree = true))
  }

  def stop(): Unit = {
    active = false
    observer.disconnect()
  }

  private def dispatchOnAllNodes(eventType: String, node: Node): Unit = {
    val event: CustomEvent = new CustomEvent(eventType, js.undefined)
    node.dispatchEvent(event)

    val children = node.asInstanceOf[Element].getElementsByTagName("*")
    for (j <- 0 until children.length) {
      children.item(j).dispatchEvent(event)
    }
  }
}