package io.udash.bootstrap
package alert

import io.udash._
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import io.udash.css.CssView._
import org.scalajs.dom.html.Div
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

private[alert] abstract class UdashAlertBase(
  alertStyle: ReadableProperty[BootstrapStyles.Color], override val componentId: ComponentId
) extends UdashBootstrapComponent {

  protected final def template: TypedTag[Div] = {
    div(
      id := componentId, role := "alert",
      BootstrapStyles.Alert.alert,
      nestedInterceptor((BootstrapStyles.Alert.color _).reactiveApply(alertStyle))
    )
  }
}

private[alert] trait UdashAlertBaseCompanion[T <: UdashAlertBase] {
  protected def create(alertStyle: ReadableProperty[BootstrapStyles.Color], componentId: ComponentId)(content: Modifier*): T

  /**
    * Creates an alert with provided style, more: <a href="http://getbootstrap.com/javascript/#alerts">Bootstrap Docs</a>.
    *
    * @param alertStyle Color of the alert.
    * @param componentId Id of the root DOM node.
    * @return `UdashAlert` component, call render to create a DOM element.
    */
  def apply(
    alertStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    componentId: ComponentId = ComponentId.newId()
  )(content: Modifier*): T = {
    create(alertStyle, componentId)(content)
  }

  def link(link: String)(content: Modifier*): Modifier =
    a(href := link, BootstrapStyles.Alert.link)(content)
}