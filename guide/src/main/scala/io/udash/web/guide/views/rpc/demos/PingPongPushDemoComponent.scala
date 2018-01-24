package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rpc.PingClient
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom
import scalatags.JsDom.all._

trait PingPongPushDemoModel {
  def pingId: Int
}
object PingPongPushDemoModel extends HasModelPropertyCreator[PingPongPushDemoModel]

class PingPongPushDemoComponent extends Component {

  override def getTemplate: Modifier = PingPongPushDemoViewFactory()

  object PingPongPushDemoViewFactory {
    def apply(): Modifier = {
      val clientId = ModelProperty.empty[PingPongPushDemoModel]
      clientId.subProp(_.pingId).set(0)

      val presenter = new PingPongPushDemoPresenter(clientId)
      new PingPongPushDemoView(clientId, presenter).render
    }
  }

  class PingPongPushDemoPresenter(model: ModelProperty[PingPongPushDemoModel]) {
    private var registered = false

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      registerCallback(btn)
      Context.serverRpc.demos().pingDemo().ping(model.subProp(_.pingId).get)
    }

    private def registerCallback(btn: UdashButton) = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        model.subProp(_.pingId).set(id + 1)
        btn.disabled.set(false)
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: ModelProperty[PingPongPushDemoModel], presenter: PingPongPushDemoPresenter) {
    import JsDom.all._

    val pingButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("ping-pong-push-demo")
    )("Ping(", bind(model.subProp(_.pingId)), ")")

    pingButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      pingButton.render
    )
  }
}
