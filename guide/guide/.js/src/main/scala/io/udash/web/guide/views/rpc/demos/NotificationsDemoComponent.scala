package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.views.Component
import io.udash.web.guide.demos.rpc.NotificationsClient
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Success}

trait NotificationsDemoModel {
  def registered: Boolean
  def lastMessage: String
}
object NotificationsDemoModel extends HasModelPropertyCreator[NotificationsDemoModel] {
  implicit val blank: Blank[NotificationsDemoModel] = Blank.Simple(new NotificationsDemoModel {
    override def registered: Boolean = false
    override def lastMessage: String = "-"
  })
}

class NotificationsDemoComponent extends Component {
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = NotificationsDemoViewFactory()

  object NotificationsDemoViewFactory {
    def apply(): Modifier = {
      val model = ModelProperty.blank[NotificationsDemoModel]
      val presenter = new NotificationsDemoPresenter(model)
      new NotificationsDemoView(model, presenter).render
    }
  }

  class NotificationsDemoPresenter(model: ModelProperty[NotificationsDemoModel]) {
    private val demoListener = (msg: String) => model.subProp(_.lastMessage).set(msg)

    def onButtonClick(disabled: Property[Boolean]): Unit = {
      disabled.set(true)
      if (model.subProp(_.registered).get) {
        NotificationsClient.unregisterListener(demoListener) onComplete {
          case Success(_) =>
            model.subProp(_.registered).set(false)
            disabled.set(false)
          case Failure(_) =>
            model.subProp(_.registered).set(true)
            disabled.set(false)
        }
      } else {
        NotificationsClient.registerListener(demoListener) onComplete {
          case Success(_) =>
            model.subProp(_.registered).set(true)
            disabled.set(false)
          case Failure(_) =>
            model.subProp(_.registered).set(false)
            disabled.set(false)
        }
      }
    }
  }

  class NotificationsDemoView(model: ModelProperty[NotificationsDemoModel], presenter: NotificationsDemoPresenter) {
    import JsDom.all._

    val registerDisabled = Property(false)
    val registerButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = registerDisabled,
      componentId = ComponentId("notifications-demo")
    )(nested => nested(produce(model.subProp(_.registered))(
      p => span(if (!p) "Register for notifications" else "Unregister").render
    )))

    registerButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.onButtonClick(registerDisabled)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.prependText(
          span(id := "notifications-demo-response")(
            "Last message: ",
            bind(model.subProp(_.lastMessage))
          )
        ),
        registerButton.render
      ).render
    )
  }
}
