package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.BootstrapStyles
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashAlert private[alert](
  alertStyle: ReadableProperty[BootstrapStyles.Color], override val componentId: ComponentId
)(content: Binding.NestedInterceptor => Modifier) extends UdashAlertBase(alertStyle, componentId) {
  override val render: Element =
    template(content(nestedInterceptor)).render
}

object UdashAlert extends UdashAlertBaseCompanion[UdashAlert] {
  protected def create(alertStyle: ReadableProperty[BootstrapStyles.Color], componentId: ComponentId)(
    content: Binding.NestedInterceptor => Modifier
  ): UdashAlert = {
    new UdashAlert(alertStyle, componentId)(content)
  }
}
