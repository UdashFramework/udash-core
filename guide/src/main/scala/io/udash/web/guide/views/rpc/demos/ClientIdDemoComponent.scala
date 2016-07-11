package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom

trait ClientIdDemoModel {
  def clientId: String
}

class ClientIdDemoComponent extends Component {
  import Context._
  override def getTemplate: Element = ClientIdDemoViewPresenter()

  object ClientIdDemoViewPresenter {
    def apply(): Element = {
      val clientId = ModelProperty[ClientIdDemoModel]
      clientId.subProp(_.clientId).set("???")

      val presenter = new ClientIdDemoPresenter(clientId)
      new ClientIdDemoView(clientId, presenter).render
    }
  }

  class ClientIdDemoPresenter(model: ModelProperty[ClientIdDemoModel]) {
    def onButtonClick(target: JQuery) = {
      target.attr("disabled", "true")
      Context.serverRpc.demos().clientIdDemo().clientId() onComplete {
        case Success(cid) => println(cid); model.subProp(_.clientId).set(cid)
        case Failure(ex) => println(ex); model.subProp(_.clientId).set(ex.toString)
      }
    }
  }

  class ClientIdDemoView(model: ModelProperty[ClientIdDemoModel], presenter: ClientIdDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    def render: Element = span(GuideStyles.frame)(
      button(id := "client-id-demo", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(onclick :+= ((ev: MouseEvent) => {
        presenter.onButtonClick(jQ(ev.target))
        true
      }))("Load client id"),
      p("Your client id: ", produce(model)(cid => span(id := "client-id-demo-response", cid.clientId).render))
    ).render
  }
}
