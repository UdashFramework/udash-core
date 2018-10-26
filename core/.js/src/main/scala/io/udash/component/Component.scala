package io.udash.component

import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.Element

/** Base trait for Udash based components. */
trait Component extends Binding {
  /** Component root DOM element ID. */
  val componentId: ComponentId

  /** Creates a component DOM hierarchy. */
  def render: Element

  override def applyTo(t: Element): Unit =
    t.appendChild(render)
}