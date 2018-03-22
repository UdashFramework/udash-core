package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.UdashButton
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

sealed abstract class UdashAlertBase(alertStyle: AlertStyle, override val componentId: ComponentId)
  extends UdashBootstrapComponent {

  protected final def template: JsDom.TypedTag[Div] =
    div(id := componentId, alertStyle, role := "alert")

}

final class UdashAlert private[alert](alertStyle: AlertStyle, override val componentId: ComponentId)(content: Modifier*)
  extends UdashAlertBase(alertStyle, componentId) {

  override val render: Element = template(content).render
}

final class DismissibleUdashAlert private[alert](alertStyle: AlertStyle, override val componentId: ComponentId)
                                                (content: Modifier*)
  extends UdashAlertBase(alertStyle, componentId) {

  import io.udash.css.CssView._

  private val _dismissed = Property[Boolean](false)
  val dismissed: ReadableProperty[Boolean] = _dismissed.transform(identity)

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
  import AlertStyle._

  protected def create(alertStyle: AlertStyle, componentId: ComponentId)(content: Modifier*): T

  /** Creates an alert with `Success` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def success(content: Modifier*): T =
    create(Success, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Success` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def success(componentId: ComponentId, content: Modifier*): T =
    create(Success, componentId)(content: _*)

  /** Creates an alert with `Info` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def info(content: Modifier*): T =
    create(Info, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Info` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def info(componentId: ComponentId, content: Modifier*): T =
    create(Info, componentId)(content: _*)

  /** Creates an alert with `Warning` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def warning(content: Modifier*): T =
    create(Warning, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Warning` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def warning(componentId: ComponentId, content: Modifier*): T =
    create(Warning, componentId)(content: _*)

  /** Creates an alert with `Danger` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def danger(content: Modifier*): T =
    create(Danger, UdashBootstrap.newId())(content: _*)

  /** Creates an alert with `Danger` style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>. */
  def danger(componentId: ComponentId, content: Modifier*): T =
    create(Danger, componentId)(content: _*)
}

/** Standard alert component. */
object UdashAlert extends AlertCompanion[UdashAlert] {
  protected def create(alertStyle: AlertStyle, componentId: ComponentId)(content: Modifier*): UdashAlert =
    new UdashAlert(alertStyle, componentId)(content: _*)
}

/** Dismissible alert component. */
object DismissibleUdashAlert extends AlertCompanion[DismissibleUdashAlert] {
  protected def create(alertStyle: AlertStyle, componentId: ComponentId)(content: Modifier*): DismissibleUdashAlert =
    new DismissibleUdashAlert(alertStyle, componentId)(content: _*)
}
