package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.guide.demos.rpc.NotificationsClient
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._
import io.udash.web.commons.views.Component

trait NotificationsDemoModel {
  def registered: Boolean
  def lastMessage: String
}

class NotificationsDemoComponent extends Component {
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = NotificationsDemoViewPresenter()

  object NotificationsDemoViewPresenter {
    def apply(): Modifier = {
      val model = ModelProperty[NotificationsDemoModel]
      model.subProp(_.registered).set(false)
      model.subProp(_.lastMessage).set("-")

      val presenter = new NotificationsDemoPresenter(model)
      new NotificationsDemoView(model, presenter).render
    }
  }

  class NotificationsDemoPresenter(model: ModelProperty[NotificationsDemoModel]) {
    private val demoListener = (msg: String) => model.subProp(_.lastMessage).set(msg)

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      model.subProp(_.registered).get match {
        case false =>
          NotificationsClient.registerListener(demoListener) onComplete {
            case Success(_) =>
              model.subProp(_.registered).set(true)
              btn.disabled.set(false)
            case Failure(_) =>
              model.subProp(_.registered).set(false)
              btn.disabled.set(false)
          }
        case true =>
          NotificationsClient.unregisterListener(demoListener) onComplete {
            case Success(_) =>
              model.subProp(_.registered).set(false)
              btn.disabled.set(false)
            case Failure(_) =>
              model.subProp(_.registered).set(true)
              btn.disabled.set(false)
          }
      }
    }
  }

  class NotificationsDemoView(model: ModelProperty[NotificationsDemoModel], presenter: NotificationsDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    val registerButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("notifications-demo")
    )(produce(model.subProp(_.registered))(
      p => span(if (!p) "Register for notifications" else "Unregister").render
    ))

    registerButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.addon(
          span(id := "notifications-demo-response")(
            "Last message: ",
            bind(model.subProp(_.lastMessage))
          )
        ),
        UdashInputGroup.buttons(registerButton.render)
      ).render
    )
  }
}
