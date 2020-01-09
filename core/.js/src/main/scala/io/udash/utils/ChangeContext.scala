package io.udash
package utils

import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.raw._
import org.scalajs.dom.{MutationObserver, MutationObserverInit}

import scala.scalajs.js

object ChangeContext {

  private var total = 0

  //todo tail
  private def cleanup(removedNodes: NodeList): Unit = {
    for (i <- 0 until removedNodes.length) {
      val node = removedNodes.item(i)
      val nodeBindings = bindings(node)
      if (nodeBindings.nonEmpty) {
        total -= nodeBindings.size
        println("R " + nodeBindings -> node.nodeName + " " + total)
        nodeBindings.foreach(_.kill())
      }
      js.special.delete(node, "bindings")
      cleanup(node.childNodes)
    }
  }

  private def setup(addedNodes: NodeList): Unit = {
    for (i <- 0 until addedNodes.length) {
      val node = addedNodes.item(i)
      //todo ensure node bindings are started
      setup(node.childNodes)
    }
  }

  private val observer = new MutationObserver((records, _) => records.foreach { v =>
    cleanup(v.removedNodes)
    setup(v.addedNodes)
  })
  private var active = false

  //todo nice wrapper for this
  private def bindings(node: Node): js.Array[Binding] = {
    if (node.hasOwnProperty("bindings")) {
      node.asInstanceOf[js.Dynamic].bindings.asInstanceOf[js.Array[Binding]]
    } else {
      val result = js.Array[Binding]()
      node.asInstanceOf[js.Dynamic].bindings = result
      result
    }
  }

  def bind(node: Node, binding: Binding): Unit = {
    if (active) {
      bindings(node) += binding
      total += 1
      println("A " + binding.getClass.getSimpleName + " " + binding.hashCode() + " " + node.asInstanceOf[Element].outerHTML + " " + total)
    }
  }

  def init(scope: Node): Unit = {
    stop()
    active = true
    observer.observe(scope, MutationObserverInit(childList = true, subtree = true))
  }

  def stop(): Unit = {
    active = false
    observer.disconnect()
  }
}