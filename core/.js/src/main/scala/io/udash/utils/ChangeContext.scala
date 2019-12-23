package io.udash
package utils

import org.scalajs.dom.raw._
import org.scalajs.dom.{MutationObserver, MutationObserverInit}

import scala.scalajs.js

object ChangeContext {

  final val NodeAdded = "NodeAdded"
  final val NodeRemoved = "NodeRemoved"

  def init(): Unit = {
    val mutationObserver: MutationObserver = new MutationObserver((records, _) => {
      records.foreach { v =>
        for (i <- 0 until v.removedNodes.length) {
          val node = v.removedNodes.item(i)
          if (!node.isInstanceOf[Text])
            dispatchOnAllNodes(NodeRemoved, node)
        }
        for (i <- 0 until v.addedNodes.length) {
          val node = v.addedNodes.item(i)
          if (!node.isInstanceOf[Text])
            dispatchOnAllNodes(NodeAdded, node)
        }
      }
    })

    mutationObserver.observe(org.scalajs.dom.document.body, MutationObserverInit(childList = true, subtree = true))
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