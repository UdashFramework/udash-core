package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

class PingPongCallDemoComponent extends Component {
  import Context._

  override def getTemplate: Modifier = PingPongCallDemoViewFactory()

  object PingPongCallDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[Int](0)
      val presenter = new PingPongCallDemoPresenter(clientId)
      new PingPongCallDemoView(clientId, presenter).render
    }
  }

  class PingPongCallDemoPresenter(model: Property[Int]) {
    def onButtonClick(disabled: Property[Boolean]) = {
      disabled.set(true)
      Context.serverRpc.demos.pingDemo.fPing(model.get) onComplete {
        case Success(response) =>
          model.set(response + 1)
          disabled.set(false)
        case Failure(_) =>
          model.set(-1)
      }
    }
  }

  class PingPongCallDemoView(model: Property[Int], presenter: PingPongCallDemoPresenter) {
    import JsDom.all._

    val pingDisabled = Property(false)
    val pingButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      disabled = pingDisabled,
      componentId = ComponentId("ping-pong-call-demo")
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
