package io.udash.bootstrap.utils

import io.udash.bindings.modifiers.Binding
import org.scalajs.dom
import org.scalajs.dom.Element

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent extends Binding {
  /** Component root DOM element ID. */
  val componentId: ComponentId

  /** Creates component DOM hierarchy. */
  val render: dom.Element

  override def applyTo(t: Element): Unit =
    t.appendChild(render)
}
