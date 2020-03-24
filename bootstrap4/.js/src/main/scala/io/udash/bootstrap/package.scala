package io.udash

import io.udash.bootstrap.utils.BootstrapImplicits
import io.udash.component.Components
import org.scalajs.dom

package object bootstrap extends BootstrapImplicits with Components {
  final val BootstrapStyles = io.udash.bootstrap.utils.BootstrapStyles
  final val BootstrapTags = io.udash.bootstrap.utils.BootstrapTags

  implicit def jqueryInterface(el: dom.Element): BootstrapJs.BootstrapJQuery = BootstrapJs.jqueryInterface(el)
}
