package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

trait PingPongCallDemoModel {
  def pingId: Int
}

class PingPongCallDemoComponent extends Component {
  import Context._

  override def getTemplate: Modifier = PingPongCallDemoViewPresenter()

  object PingPongCallDemoViewPresenter {
    def apply(): Modifier = {
      val clientId = ModelProperty[PingPongCallDemoModel]
      clientId.subProp(_.pingId).set(0)

      val presenter = new PingPongCallDemoPresenter(clientId)
      new PingPongCallDemoView(clientId, presenter).render
    }
  }

  class PingPongCallDemoPresenter(model: ModelProperty[PingPongCallDemoModel]) {
    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      Context.serverRpc.demos().pingDemo().fPing(model.subProp(_.pingId).get) onComplete {
        case Success(response) =>
          model.subProp(_.pingId).set(response + 1)
          btn.disabled.set(false)
        case Failure(ex) =>
          model.subProp(_.pingId).set(-1)
      }
    }
  }

  class PingPongCallDemoView(model: ModelProperty[PingPongCallDemoModel], presenter: PingPongCallDemoPresenter) {
    import JsDom.all._

    val pingButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("ping-pong-call-demo")
    )("Ping(", bind(model.subProp(_.pingId)), ")")

    pingButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      pingButton.render
    )
  }
}
