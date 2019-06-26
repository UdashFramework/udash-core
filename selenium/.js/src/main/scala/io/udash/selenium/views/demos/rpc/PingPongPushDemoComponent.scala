package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
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
      val disablePingBtn = Property(false)
      val presenter = new PingPongPushDemoPresenter(clientId, disablePingBtn)
      new PingPongPushDemoView(clientId, disablePingBtn, presenter).render
    }
  }

  class PingPongPushDemoPresenter(clientId: Property[Int], disablePingBtn: Property[Boolean]) {
    private var registered = false

    def onButtonClick(): Unit = {
      registerCallback()
      Launcher.serverRpc.demos().pingDemo().ping(clientId.get)
    }

    private def registerCallback(): Unit = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        clientId.set(id + 1)
        disablePingBtn.set(false)
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: Property[Int], disablePingBtn: Property[Boolean], presenter: PingPongPushDemoPresenter) {
    import JsDom.all._

    private val pingButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = disablePingBtn,
      componentId = ComponentId("ping-pong-push-demo")
    )(nested => Seq[Modifier]("Ping(", nested(bind(model)), ")"))

    pingButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        disablePingBtn.set(true)
        presenter.onButtonClick()
    }

    def render: Modifier = span(pingButton.render)
  }
}
