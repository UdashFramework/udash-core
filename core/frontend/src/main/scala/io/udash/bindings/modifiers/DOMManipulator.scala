package io.udash.bindings.modifiers

import org.scalajs.dom.{Element, Node}

private[bindings]
trait DOMManipulator {
  type ReplaceMethod = (Node, Seq[Node], Seq[Node]) => Boolean
  type InsertMethod = (Node, Node, Seq[Node]) => Boolean

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
  def customElementsInsert: InsertMethod = DOMManipulator.defaultElementInsert

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
  val defaultElementReplace: DOMManipulator#ReplaceMethod = (_, _, _) => true
  val defaultElementInsert: DOMManipulator#InsertMethod = (_, _, _) => true
}
