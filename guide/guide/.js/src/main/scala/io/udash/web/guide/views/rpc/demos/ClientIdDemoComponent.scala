package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Success}

class ClientIdDemoComponent extends Component {
  import Context._
  override def getTemplate: Modifier = ClientIdDemoViewFactory()

  object ClientIdDemoViewFactory {
    def apply(): Modifier = {
      val clientId = Property[String]("???")

      val presenter = new ClientIdDemoPresenter(clientId)
      new ClientIdDemoView(clientId, presenter).render
    }
  }

  class ClientIdDemoPresenter(model: Property[String]) {
    def onButtonClick() = {
      Context.serverRpc.demos.clientIdDemo.clientId() onComplete {
        case Success(cid) => println(cid); model.set(cid)
        case Failure(ex) => println(ex); model.set(ex.toString)
      }
    }
  }

  class ClientIdDemoView(model: Property[String], presenter: ClientIdDemoPresenter) {
    import JsDom.all._

    val loadIdButtonDisabled = Property(false)
    val loadIdButton = UdashButton(
      disabled = loadIdButtonDisabled,
      componentId = ComponentId("client-id-demo")
    )(_ => "Load client id")

    loadIdButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        loadIdButtonDisabled.set(true)
        presenter.onButtonClick()
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.prependText(
          "Your client id: ",
          produce(model)(cid => span(id := "client-id-demo-response", cid).render)
        ),
        loadIdButton.render
      ).render
    )
  }
}
