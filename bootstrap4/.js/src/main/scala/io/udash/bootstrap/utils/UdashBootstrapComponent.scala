package io.udash.bootstrap.utils

import io.udash.bindings.modifiers.Binding
import io.udash.component.Component
import io.udash.css.CssView
import io.udash.wrappers.jquery.*
import org.scalajs.dom.Element

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent extends Component with CssView {
  override val render: Element

  protected class JQueryOnBinding(selector: JQuery, event: EventName, callback: JQueryCallback) extends Binding {
    selector.on(event, callback)

    override def kill(): Unit = {
      super.kill()
      selector.off(event, callback)
    }

    override def applyTo(t: Element): Unit = ()
  }
}
