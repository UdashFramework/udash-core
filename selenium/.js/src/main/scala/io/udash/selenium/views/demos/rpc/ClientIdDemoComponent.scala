package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import io.udash.selenium.Launcher
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class ClientIdDemoComponent extends CssView {
  def getTemplate: Modifier = ClientIdDemoViewFactory()

  object ClientIdDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[String]("???")

      val presenter = new ClientIdDemoPresenter(clientId)
      new ClientIdDemoView(clientId, presenter).render
    }
  }

  class ClientIdDemoPresenter(model: Property[String]) {
    def onButtonClick() = {
      Launcher.serverRpc.demos().clientIdDemo().clientId() onComplete {
        case Success(cid) => println(cid); model.set(cid)
        case Failure(ex) => println(ex); model.set(ex.toString)
      }
    }
  }

  class ClientIdDemoView(model: Property[String], presenter: ClientIdDemoPresenter) {
    import JsDom.all._

    val loadIdButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("client-id-demo")
    )("Load client id")

    loadIdButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        btn.disabled.set(true)
        presenter.onButtonClick()
    }

    def render: Modifier = span(
      UdashInputGroup()(
        UdashInputGroup.addon(
          "Your client id: ",
          produce(model)(cid => span(id := "client-id-demo-response", cid).render)
        ),
        UdashInputGroup.buttons(loadIdButton.render)
      ).render
    )
  }
}
