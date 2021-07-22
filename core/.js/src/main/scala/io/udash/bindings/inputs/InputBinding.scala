package io.udash.bindings.inputs

import com.avsystem.commons.universalOps
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.Element

trait InputBinding[RenderType <: Element] extends Binding {
  def render: RenderType

  override def applyTo(t: Element): Unit =
    t.appendChild(render).discard
}
