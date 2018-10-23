package io.udash.selenium.views.demos.rpc

import com.avsystem.commons._
import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.selenium.rpc.NotificationsClient
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

class NotificationsDemoComponent extends CssView {

  def getTemplate: Modifier = NotificationsDemoViewFactory()

  object NotificationsDemoViewFactory {
    def apply(): Modifier = {
      val model = ModelProperty.blank[NotificationsDemoModel]
      val presenter = new NotificationsDemoPresenter(model)
      new NotificationsDemoView(model, presenter).render
    }
  }

  class NotificationsDemoPresenter(model: ModelProperty[NotificationsDemoModel]) {
    private val demoListener = (msg: String) => model.subProp(_.lastMessage).set(msg)

    def onButtonClick(): Future[Unit] = {
      model.subProp(_.registered).get match {
        case false =>
          NotificationsClient.registerListener(demoListener).setup {
            _.onComplete {
              case Success(_) =>
                model.subProp(_.registered).set(true)
              case Failure(_) =>
                model.subProp(_.registered).set(false)
            }
          }
        case true =>
          NotificationsClient.unregisterListener(demoListener).setup {
            _.onComplete {
              case Success(_) =>
                model.subProp(_.registered).set(false)
              case Failure(_) =>
                model.subProp(_.registered).set(true)
            }
          }
      }
    }
  }

  class NotificationsDemoView(model: ModelProperty[NotificationsDemoModel], presenter: NotificationsDemoPresenter) {
    import JsDom.all._

    private val disableRegisterBtn = Property(false)
    private val registerButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = disableRegisterBtn,
      componentId = ComponentId("notifications-demo")
    )(nested => nested(
      produce(model.subProp(_.registered)) { p =>
        span(if (!p) "Register for notifications" else "Unregister").render
      }
    ))

    registerButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        disableRegisterBtn.set(true)
        presenter.onButtonClick().onComplete(_ => disableRegisterBtn.set(false))
    }

    def render: Modifier = span(
      UdashInputGroup()(
        UdashInputGroup.prependText(
          span(id := "notifications-demo-response")(
            "Last message: ",
            bind(model.subProp(_.lastMessage))
          )
        ),
        UdashInputGroup.append(
          registerButton.render
        )
      ).render
    )
  }
}
