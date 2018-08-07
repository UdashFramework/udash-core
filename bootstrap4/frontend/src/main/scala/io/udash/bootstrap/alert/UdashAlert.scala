package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

sealed abstract class UdashAlertBase(
  alertStyle: BootstrapStyles.Color, override val componentId: ComponentId
) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  protected final def template: JsDom.TypedTag[Div] =
    div(
      id := componentId, role := "alert",
      BootstrapStyles.Alert.color(alertStyle),
      BootstrapStyles.Alert.alert
    )

}

final class UdashAlert private[alert](
  alertStyle: BootstrapStyles.Color, override val componentId: ComponentId
)(content: Modifier*) extends UdashAlertBase(alertStyle, componentId) {

  override val render: Element = template(content).render
}

final class DismissibleUdashAlert private[alert](
  alertStyle: BootstrapStyles.Color, override val componentId: ComponentId
)(content: Modifier*) extends UdashAlertBase(alertStyle, componentId) {

  import io.udash.css.CssView._

  private val _dismissed = Property[Boolean](false)
  val dismissed: ReadableProperty[Boolean] = _dismissed.readable

  private val button = UdashButton()(
    `type` := "button", BootstrapStyles.close,
    BootstrapTags.dataDismiss := "alert", aria.label := "close",
    span(aria.hidden := "true")("×")
  )
  button.listen { case UdashButton.ButtonClickEvent(_, _) => _dismissed.set(true) }

  private val buttonRendered = button.render

  override val render: Element = template(
    BootstrapStyles.Alert.alertDismissible,
    buttonRendered, content
  ).render

  def dismiss(): Unit =
    buttonRendered.click()
}

trait AlertCompanion[T <: UdashAlertBase] {
  protected def create(alertStyle: BootstrapStyles.Color, componentId: ComponentId)(content: Modifier*): T

  /** Creates an alert with `Primary` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def primary(content: Modifier*): T =
    create(BootstrapStyles.Color.Primary, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Primary` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def primary(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Primary, componentId)(content: _*)

  /** Creates an alert with `Secondary` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def secondary(content: Modifier*): T =
    create(BootstrapStyles.Color.Secondary, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Secondary` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def secondary(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Secondary, componentId)(content: _*)

  /** Creates an alert with `Success` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def success(content: Modifier*): T =
    create(BootstrapStyles.Color.Success, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Success` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def success(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Success, componentId)(content: _*)

  /** Creates an alert with `Info` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def info(content: Modifier*): T =
    create(BootstrapStyles.Color.Info, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Info` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def info(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Info, componentId)(content: _*)

  /** Creates an alert with `Warning` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def warning(content: Modifier*): T =
    create(BootstrapStyles.Color.Warning, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Warning` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def warning(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Warning, componentId)(content: _*)

  /** Creates an alert with `Danger` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def danger(content: Modifier*): T =
    create(BootstrapStyles.Color.Danger, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Danger` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def danger(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Danger, componentId)(content: _*)

  /** Creates an alert with `Light` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def light(content: Modifier*): T =
    create(BootstrapStyles.Color.Light, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Light` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def light(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Light, componentId)(content: _*)

  /** Creates an alert with `Dark` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def dark(content: Modifier*): T =
    create(BootstrapStyles.Color.Dark, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Dark` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def dark(componentId: ComponentId, content: Modifier*): T =
    create(BootstrapStyles.Color.Dark, componentId)(content: _*)
}

/** Standard alert component. */
object UdashAlert extends AlertCompanion[UdashAlert] {
  protected def create(alertStyle: BootstrapStyles.Color, componentId: ComponentId)(content: Modifier*): UdashAlert =
    new UdashAlert(alertStyle, componentId)(content: _*)
}

/** Dismissible alert component. */
object DismissibleUdashAlert extends AlertCompanion[DismissibleUdashAlert] {
  protected def create(alertStyle: BootstrapStyles.Color, componentId: ComponentId)(content: Modifier*): DismissibleUdashAlert =
    new DismissibleUdashAlert(alertStyle, componentId)(content: _*)
}
