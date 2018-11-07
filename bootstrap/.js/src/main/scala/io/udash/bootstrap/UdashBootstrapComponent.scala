package io.udash.bootstrap

import io.udash.ComponentId
import org.scalajs.dom.Element

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent {
  val bootstrapJsContext: BootstrapJsContext.type = BootstrapJsContext

  /** Component root DOM element ID. */
  val componentId: ComponentId

  /** Creates component DOM hierarchy. */
  val render: Element
}
