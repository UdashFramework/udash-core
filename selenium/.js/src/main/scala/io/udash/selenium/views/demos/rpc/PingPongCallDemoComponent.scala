package io.udash.selenium.views.demos.rpc

import com.avsystem.commons._
import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.selenium.Launcher
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

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
    def onButtonClick(): Future[Unit] = {
      Launcher.serverRpc.call().demos().pingDemo().fPing(model.get).setup {
        _.onComplete {
          case Success(response) =>
            model.set(response + 1)
          case Failure(_) =>
            model.set(-1)
        }
      }.toUnit
    }
  }

  class PingPongCallDemoView(model: Property[Int], presenter: PingPongCallDemoPresenter) {
    import JsDom.all._

    private val disablePingBtn = Property(false)
    private val pingButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = disablePingBtn,
      componentId = ComponentId("ping-pong-call-demo")
    )(nested => Seq[Modifier]("Ping(", nested(bind(model)), ")"))

    pingButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        disablePingBtn.set(true)
        presenter.onButtonClick().onComplete(_ => disablePingBtn.set(false))
    }

    def render: Modifier = span(pingButton.render)
  }
}
