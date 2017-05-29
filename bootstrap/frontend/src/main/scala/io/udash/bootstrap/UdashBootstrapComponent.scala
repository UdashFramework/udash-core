package io.udash.bootstrap

import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.css.CssView
import org.scalajs.dom

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent {
  /** Component root DOM element ID. */
  val componentId: ComponentId

  /** Creates component DOM hierarchy. */
  val render: dom.Element
}
