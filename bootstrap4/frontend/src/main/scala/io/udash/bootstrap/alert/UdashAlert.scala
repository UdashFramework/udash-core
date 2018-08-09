package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bootstrap.ComponentId
import org.scalajs.dom.Element

import scalatags.JsDom.all._

final class UdashAlert private[alert](
  alertStyle: ReadableProperty[BootstrapStyles.Color], override val componentId: ComponentId
)(content: Modifier*) extends UdashAlertBase(alertStyle, componentId) {
  override val render: Element =
    template(content).render
}

/** Standard alert component. */
object UdashAlert extends UdashAlertBaseCompanion[UdashAlert] {
  protected def create(alertStyle: ReadableProperty[BootstrapStyles.Color], componentId: ComponentId)(content: Modifier*): UdashAlert =
    new UdashAlert(alertStyle, componentId)(content: _*)
}
