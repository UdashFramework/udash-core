package io.udash

import io.udash.bootstrap.utils.BootstrapImplicits
import io.udash.component.Components
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom

package object bootstrap extends BootstrapImplicits with Components {
  final val BootstrapStyles = io.udash.bootstrap.utils.BootstrapStyles
  final val BootstrapTags = io.udash.bootstrap.utils.BootstrapTags

  implicit def jqueryInterface(el: dom.Element): BootstrapJs.BootstrapJQuery = BootstrapJs.jqueryInterface(el)
  implicit def bootstrapInterface(query: JQuery): BootstrapJs.BootstrapJQuery = BootstrapJs.jqueryInterface(query)
}
