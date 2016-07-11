package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom

trait PingPongCallDemoModel {
  def pingId: Int
}

class PingPongCallDemoComponent extends Component {
  import Context._

  override def getTemplate: Element = PingPongCallDemoViewPresenter()

  object PingPongCallDemoViewPresenter {
    def apply(): Element = {
      val clientId = ModelProperty[PingPongCallDemoModel]
      clientId.subProp(_.pingId).set(0)

      val presenter = new PingPongCallDemoPresenter(clientId)
      new PingPongCallDemoView(clientId, presenter).render
    }
  }

  class PingPongCallDemoPresenter(model: ModelProperty[PingPongCallDemoModel]) {
    def onButtonClick(target: JQuery) = {
      target.attr("disabled", "true")
      Context.serverRpc.demos().pingDemo().fPing(model.subProp(_.pingId).get) onComplete {
        case Success(response) =>
          model.subProp(_.pingId).set(response + 1)
          target.removeAttr("disabled")
        case Failure(ex) =>
          model.subProp(_.pingId).set(-1)
      }
    }
  }

  class PingPongCallDemoView(model: ModelProperty[PingPongCallDemoModel], presenter: PingPongCallDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    def render: Element = span(GuideStyles.frame)(
      button(id := "ping-pong-call-demo", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(onclick :+= ((ev: MouseEvent) => {
        presenter.onButtonClick(jQ(ev.target))
        true
      }))(produce(model.subProp(_.pingId))(p => JsDom.StringFrag(s"Ping($p)").render.asInstanceOf[dom.Element]))
    ).render
  }
}
