package io.udash.bindings.modifiers

import com.avsystem.commons.SharedExtensions._
import io.udash.utils.Registration
import org.scalajs.dom

import scala.scalajs.js
import scalatags.generic.Modifier

/** Modifier representing data binding. */
trait Binding extends Modifier[dom.Element] {
  protected var propertyListeners: js.Array[Registration] = js.Array()
  protected var nestedBindings: js.Array[Binding] = js.Array()

  protected def nestedInterceptor(binding: Binding): Binding =
    binding.setup { nestedBindings += _ }

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
