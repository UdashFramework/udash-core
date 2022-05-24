package io.udash.bindings.modifiers

import io.udash.utils.Registration
import org.scalajs.dom.Element
import scalatags.generic.Modifier

import scala.scalajs.js

/** Modifier representing data binding. */
trait Binding extends Modifier[Element] {
  protected final val propertyListeners: js.Array[Registration] = js.Array()
  protected final val nestedBindings: js.Array[Binding] = js.Array()

  /** Every interceptor is expected to return the value received as argument. */
  final val nestedInterceptor: Binding.NestedInterceptor = new Binding.NestedInterceptor {
    override def apply(binding: Binding): binding.type = {
      nestedBindings.push(binding)
      binding
    }
    override def multi(bindings: Binding*): bindings.type = {
      nestedBindings.push(bindings: _*)
      bindings
    }
  }

  def addRegistration(registration: Registration): Unit = propertyListeners += registration

  /** This method clears all bindings and listeners. */
  def kill(): Unit = {
    killNestedBindings()
    propertyListeners.foreach(_.cancel())
    propertyListeners.length = 0 // JS way to clear an array
  }

  /** This method clears all nested bindings and listeners. */
  def killNestedBindings(): Unit = {
    nestedBindings.foreach(_.kill())
    nestedBindings.length = 0 // JS way to clear an array
  }
}

object Binding {

  /** Every interceptor is expected to return the value received as argument. */
  trait NestedInterceptor {
    def apply(binding: Binding): binding.type
    def multi(bindings: Binding*): bindings.type = {
      bindings.foreach(apply(_))
      bindings
    }
  }

  object NestedInterceptor {
    final val Identity: NestedInterceptor = new NestedInterceptor {
      override def apply(binding: Binding): binding.type = binding
    }
  }

}
