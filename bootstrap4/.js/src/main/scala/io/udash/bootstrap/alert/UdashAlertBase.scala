package io.udash.bootstrap
package alert

import com.avsystem.commons.universalOps
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.css.CssView._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom.html.Div
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

import scala.scalajs.js

private[alert] abstract class UdashAlertBase(
  alertStyle: ReadableProperty[BootstrapStyles.Color], override val componentId: ComponentId
) extends UdashBootstrapComponent {

  protected final def template: TypedTag[Div] = {
    div(
      componentId, role := "alert",
      BootstrapStyles.Alert.alert,
      nestedInterceptor((BootstrapStyles.Alert.color _).reactiveApply(alertStyle))
    )
  }

  override def kill(): Unit = {
    super.kill()
    jQSelector().alert("dispose")
  }

  protected final def jQSelector(): UdashAlertJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashAlertJQuery]
}

private[alert] trait UdashAlertBaseCompanion[T <: UdashAlertBase] {
  protected def create(alertStyle: ReadableProperty[BootstrapStyles.Color], componentId: ComponentId)(
    content: Binding.NestedInterceptor => Modifier
  ): T

  /**
    * Creates an alert with provided style.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/alerts/">Bootstrap Docs</a>.
    *
    * @param alertStyle  A color of the alert.
    * @param componentId An id of the root DOM node.
    * @param content     A content of the alert. Use the provided interceptor to properly clean up bindings inside the content.
    * @return A `UdashAlert` component, call `render` to create a DOM element.
    */
  def apply(
    alertStyle: ReadableProperty[BootstrapStyles.Color] = UdashBootstrap.ColorSecondary,
    componentId: ComponentId = ComponentId.generate()
  )(content: Binding.NestedInterceptor => Modifier): T = {
    create(alertStyle, componentId)(content)
  }

  def link(link: Url)(content: Modifier*): Modifier =
    a(href := link, BootstrapStyles.Alert.link)(content)
}

@js.native
private trait UdashAlertJQuery extends JQuery {
  def alert(cmd: String): Unit = js.native
}