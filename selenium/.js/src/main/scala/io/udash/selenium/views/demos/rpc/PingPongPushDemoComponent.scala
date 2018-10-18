package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.PingClient
import scalatags.JsDom
import scalatags.JsDom.all._

class PingPongPushDemoComponent extends CssView {

  def getTemplate: Modifier = PingPongPushDemoViewFactory()

  object PingPongPushDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[Int](0)
      val presenter = new PingPongPushDemoPresenter(clientId)
      new PingPongPushDemoView(clientId, presenter).render
    }
  }

  class PingPongPushDemoPresenter(model: Property[Int]) {
    private var registered = false

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      registerCallback(btn)
      Launcher.serverRpc.demos().pingDemo().ping(model.get)
    }

    private def registerCallback(btn: UdashButton) = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        model.set(id + 1)
        btn.disabled.set(false)
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: Property[Int], presenter: PingPongPushDemoPresenter) {
    import JsDom.all._

    val pingButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("ping-pong-push-demo")
    )("Ping(", bind(model), ")")

    pingButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(pingButton.render)
  }
}
