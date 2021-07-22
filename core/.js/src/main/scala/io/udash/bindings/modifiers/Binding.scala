package io.udash.bindings.modifiers

import com.avsystem.commons.SharedExtensions._
import io.udash.utils.Registration
import org.scalajs.dom.Element
import scalatags.generic.Modifier

import scala.scalajs.js

/** Modifier representing data binding. */
trait Binding extends Modifier[Element] {
  protected final val propertyListeners: js.Array[Registration] = js.Array()
  protected final val nestedBindings: js.Array[Binding] = js.Array()

  /** Every interceptor is expected to return the value received as argument. */
  def nestedInterceptor[T <: Binding](binding: T): T =
    binding.setup {
      nestedBindings += _
    }

  def addRegistration(registration: Registration): Unit = (propertyListeners += registration).discard

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
  type NestedInterceptor = Binding => Binding
}
