package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.css.CssView
import io.udash.selenium.Launcher
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class PingPongCallDemoComponent extends CssView {

  def getTemplate: Modifier = PingPongCallDemoViewFactory()

  object PingPongCallDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[Int](0)
      val presenter = new PingPongCallDemoPresenter(clientId)
      new PingPongCallDemoView(clientId, presenter).render
    }
  }

  class PingPongCallDemoPresenter(model: Property[Int]) {
    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      Launcher.serverRpc.demos().pingDemo().fPing(model.get) onComplete {
        case Success(response) =>
          model.set(response + 1)
          btn.disabled.set(false)
        case Failure(_) =>
          model.set(-1)
      }
    }
  }

  class PingPongCallDemoView(model: Property[Int], presenter: PingPongCallDemoPresenter) {
    import JsDom.all._

    val pingButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("ping-pong-call-demo")
    )("Ping(", bind(model), ")")

    pingButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(pingButton.render)
  }
}
