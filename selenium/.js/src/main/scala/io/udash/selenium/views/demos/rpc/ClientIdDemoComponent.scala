package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.selenium.Launcher
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

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
    def onButtonClick(): Unit = {
      Launcher.serverRpc.call().demos().clientIdDemo().clientId() onComplete {
        case Success(cid) => model.set(cid)
        case Failure(ex) => model.set(ex.toString)
      }
    }
  }

  class ClientIdDemoView(model: Property[String], presenter: ClientIdDemoPresenter) {
    import JsDom.all._

    private val disableLoadBtn = Property(false)
    private val loadIdButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = disableLoadBtn,
      componentId = ComponentId("client-id-demo")
    )(_ => "Load client id")

    loadIdButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        disableLoadBtn.set(true)
        presenter.onButtonClick()
    }

    def render: Modifier = span(
      UdashInputGroup()(
        UdashInputGroup.prependText(
          span(BootstrapStyles.Spacing.margin(BootstrapStyles.Side.Right))("Your client id: "),
          produce(model)(cid => span(id := "client-id-demo-response", cid).render)
        ),
        UdashInputGroup.append(
          loadIdButton.render
        )
      ).render
    )
  }
}
