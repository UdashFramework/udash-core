package io.udash.bootstrap
package utils

import org.scalajs.dom.Element

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent {
  /** Component root DOM element ID. */
  val componentId: ComponentId

  /** Creates component DOM hierarchy. */
  val render: Element
}
