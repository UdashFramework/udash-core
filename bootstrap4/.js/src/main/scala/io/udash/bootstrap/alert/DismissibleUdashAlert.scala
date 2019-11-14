package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.{BootstrapStyles, BootstrapTags}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class DismissibleUdashAlert private[alert](
  alertStyle: ReadableProperty[BootstrapStyles.Color], override val componentId: ComponentId
)(content: Binding.NestedInterceptor => Modifier) extends UdashAlertBase(alertStyle, componentId) {
  import io.udash.css.CssView._

  private val _dismissed = Property[Boolean](false)

  def dismissed: ReadableProperty[Boolean] =
    _dismissed.readable

  private val button = UdashButton(buttonStyle = BootstrapStyles.Color.Link.toProperty) { _ => Seq[Modifier](
    id := componentId.withSuffix("close"),
    `type` := "button", BootstrapStyles.close,
    BootstrapTags.dataDismiss := "alert", aria.label := "close",
    span(aria.hidden := "true")("×")
  )}

  button.listen { case UdashButton.ButtonClickEvent(_, _) =>
    _dismissed.set(true)
  }

  private val buttonRendered = button.render

  override val render: Element = template(
    BootstrapStyles.Alert.dismissible,
    content(nestedInterceptor), buttonRendered
  ).render

  def dismiss(): Unit =
    buttonRendered.click()
}

object DismissibleUdashAlert extends UdashAlertBaseCompanion[DismissibleUdashAlert] {
  protected def create(alertStyle: ReadableProperty[BootstrapStyles.Color], componentId: ComponentId)(
    content: Binding.NestedInterceptor => Modifier
  ): DismissibleUdashAlert = {
    new DismissibleUdashAlert(alertStyle, componentId)(content)
  }
}