package io.udash.bootstrap.utils

import io.udash.component.Component
import org.scalajs.dom.Element

/** Base trait for Bootstrap components. */
trait UdashBootstrapComponent extends Component {
  override val render: Element
}
