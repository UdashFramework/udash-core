package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.demos.rpc.NotificationsClient
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom

trait NotificationsDemoModel {
  def registered: Boolean
  def lastMessage: String
}

class NotificationsDemoComponent extends Component {
  import io.udash.web.guide.Context._

  override def getTemplate: Element = NotificationsDemoViewPresenter()

  object NotificationsDemoViewPresenter {
    def apply(): Element = {
      val model = ModelProperty[NotificationsDemoModel]
      model.subProp(_.registered).set(false)
      model.subProp(_.lastMessage).set("-")

      val presenter = new NotificationsDemoPresenter(model)
      new NotificationsDemoView(model, presenter).render
    }
  }

  class NotificationsDemoPresenter(model: ModelProperty[NotificationsDemoModel]) {
    private val demoListener = (msg: String) => model.subProp(_.lastMessage).set(msg)

    def onButtonClick(target: JQuery) = {
      target.attr("disabled", "true")
      model.subProp(_.registered).get match {
        case false =>
          NotificationsClient.registerListener(demoListener) onComplete {
            case Success(_) =>
              model.subProp(_.registered).set(true)
              target.removeAttr("disabled")
            case Failure(_) =>
              model.subProp(_.registered).set(false)
              target.removeAttr("disabled")
          }
        case true =>
          NotificationsClient.unregisterListener(demoListener) onComplete {
            case Success(_) =>
              model.subProp(_.registered).set(false)
              target.removeAttr("disabled")
            case Failure(_) =>
              model.subProp(_.registered).set(true)
              target.removeAttr("disabled")
          }
      }
    }
  }

  class NotificationsDemoView(model: ModelProperty[NotificationsDemoModel], presenter: NotificationsDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    def render: Element = span(GuideStyles.frame)(
      button(id := "notifications-demo", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(onclick :+= ((ev: MouseEvent) => {
        presenter.onButtonClick(jQ(ev.target))
        true
      }))(produce(model.subProp(_.registered))(p => JsDom.StringFrag(if (!p) "Register for notifications" else "Unregister").render.asInstanceOf[Element])),
      p(id := "notifications-demo-response")("Last message: ", bind(model.subProp(_.lastMessage)))
    ).render
  }
}
