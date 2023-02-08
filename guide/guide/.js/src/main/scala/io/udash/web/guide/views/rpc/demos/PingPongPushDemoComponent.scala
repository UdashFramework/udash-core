package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rpc.PingClient
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom
import scalatags.JsDom.all._


class PingPongPushDemoComponent extends Component {

  override def getTemplate: Modifier = PingPongPushDemoViewFactory()

  object PingPongPushDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[Int](0)
      val presenter = new PingPongPushDemoPresenter(clientId)
      new PingPongPushDemoView(clientId, presenter).render
    }
  }

  class PingPongPushDemoPresenter(model: Property[Int]) {
    private var registered = false

    def onButtonClick(disabled: Property[Boolean]) = {
      disabled.set(true)
      registerCallback(disabled)
      Context.serverRpc.demos.pingDemo.ping(model.get)
    }

    private def registerCallback(disabled: Property[Boolean]) = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        model.set(id + 1)
        disabled.set(false)
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: Property[Int], presenter: PingPongPushDemoPresenter) {

    import JsDom.all._

    val pingDisabled = Property(false)
    val pingButton = UdashButton(
      disabled = pingDisabled,
      componentId = ComponentId("ping-pong-push-demo")
    )(nested => Seq[Modifier]("Ping(", nested(bind(model)), ")"))

    pingButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.onButtonClick(pingDisabled)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      pingButton.render
    )
  }
}
