package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

trait ClientIdDemoModel {
  def clientId: String
}

class ClientIdDemoComponent extends Component {
  import Context._
  override def getTemplate: Modifier = ClientIdDemoViewFactory()

  object ClientIdDemoViewFactory {
    def apply(): Modifier = {
      val clientId = ModelProperty[ClientIdDemoModel]
      clientId.subProp(_.clientId).set("???")

      val presenter = new ClientIdDemoPresenter(clientId)
      new ClientIdDemoView(clientId, presenter).render
    }
  }

  class ClientIdDemoPresenter(model: ModelProperty[ClientIdDemoModel]) {
    def onButtonClick() = {
      Context.serverRpc.demos().clientIdDemo().clientId() onComplete {
        case Success(cid) => println(cid); model.subProp(_.clientId).set(cid)
        case Failure(ex) => println(ex); model.subProp(_.clientId).set(ex.toString)
      }
    }
  }

  class ClientIdDemoView(model: ModelProperty[ClientIdDemoModel], presenter: ClientIdDemoPresenter) {
    import JsDom.all._

    val loadIdButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("client-id-demo")
    )("Load client id")

    loadIdButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        btn.disabled.set(true)
        presenter.onButtonClick()
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.addon(
          "Your client id: ",
          produce(model)(cid => span(id := "client-id-demo-response", cid.clientId).render)
        ),
        UdashInputGroup.buttons(loadIdButton.render)
      ).render
    )
  }
}
