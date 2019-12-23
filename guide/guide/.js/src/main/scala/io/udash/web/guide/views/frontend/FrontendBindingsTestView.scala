package io.udash
package web.guide.views.frontend

import io.udash.bindings.modifiers.Binding
import io.udash.css.CssView
import io.udash.utils.ChangeContext
import io.udash.web.guide.FrontendBindingsTestState
import org.scalajs.dom.Element

case object FrontendBindingsTestViewFactory extends StaticViewFactory[FrontendBindingsTestState.type](() => new FrontendBindingsTestView)

final class FrontendBindingsTestView extends View with CssView {

  println("view init")

  private val observer = ChangeContext.init()

  import scalatags.JsDom.all._

  def inContext[El <: Element](binding: Binding): scalatags.generic.Modifier[El] = { t: El =>
    ChangeContext.bind(t, binding)
    binding.applyTo(t)
  }

  private val prop = Property(1)

  private val r = div(
    h2("Bindings test"),
    p(
      "Test test"
    ),
    button(onclick := (() => addBind()))("Add bind")
  ).render

  private def addBind(): Unit = {
    val child = div(inContext(bind(prop))).render
    r.appendChild(child)
    child.onclick = _ => r.removeChild(child)
  }

  override def getTemplate: Modifier = {
    println("view get template")
    r
  }

  override def onClose(): Unit = {
    observer.disconnect()
    println("view closed")
  }
}