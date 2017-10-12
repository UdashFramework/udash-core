package io.udash.bindings.modifiers

import org.scalajs.dom.{Element, Node}

private[bindings]
trait DOMManipulator {
  import DOMManipulator._

  /**
    * Provides custom child elements replace method. This method takes
    * root element, old children and new children. It should return `true`,
    * if it does not replace elements in DOM.
    */
  def customElementsReplace: ReplaceMethod

  /**
    * Provides custom child elements insert method. This method takes
    * root element, ref node and new children. It should return `true`,
    * if it does not insert elements in DOM.
    */
  def customElementsInsert: InsertMethod = defaultElementInsert

  protected def replace(root: Node)(oldElements: Seq[Node], newElements: Seq[Node]): Unit =
    if (customElementsReplace(root, oldElements, newElements)) {
      root.replaceChildren(oldElements, newElements)
    }

  protected def insert(root: Node)(before: Node, newElements: Seq[Node]): Unit =
    if (customElementsInsert(root, before, newElements)) {
      newElements.foreach(root.insertBefore(_, before))
    }
}

private[bindings]
object DOMManipulator {
  type ReplaceMethod = (Node, Seq[Node], Seq[Node]) => Boolean
  type InsertMethod = (Node, Node, Seq[Node]) => Boolean

  val defaultElementReplace: ReplaceMethod = (_, _, _) => true
  val defaultElementInsert: InsertMethod = (_, _, _) => true
}
