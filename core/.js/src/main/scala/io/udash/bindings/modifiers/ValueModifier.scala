package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.bindings._
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration
import org.scalajs.dom._

import scala.scalajs.js

private[bindings] trait ValueModifier[T] extends Binding with DOMManipulator {

  import Bindings._

  protected def property: ReadableProperty[T]
  protected def builder: (T, Binding.NestedInterceptor) => Seq[Node]
  protected def checkNull: Boolean
  protected def listen(callback: T => Unit): Registration

  override def applyTo(t: Element): Unit = {
    var elements: Seq[Node] = Seq.empty

    def rebuild(propertyValue: T): Unit = {
      killNestedBindings()

      val oldEls = elements
      val newEls: Seq[Node] =
        builder(propertyValue, nestedInterceptor)
          .optIf(!checkNull || propertyValue != null)
          .filter(_.nonEmpty)
          .getOrElse(emptyStringNode())

      elements = newEls.iterator.flatMap {
        case fragment: DocumentFragment =>
          //https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/slice#Array-like_objects
          js.Dynamic.global.Array.prototype.slice.call(fragment.childNodes).asInstanceOf[js.Array[Node]].iterator
        case node => Iterator(node)
      }.toSeq

      replace(t)(oldEls, elements)
    }

    propertyListeners.push(listen(rebuild))
    rebuild(property.get)
  }
}