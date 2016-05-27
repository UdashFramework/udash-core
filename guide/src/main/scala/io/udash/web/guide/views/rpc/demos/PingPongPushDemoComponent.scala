package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rpc.PingClient
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.JsDom

trait PingPongPushDemoModel {
  def pingId: Int
}

class PingPongPushDemoComponent extends Component {
  import io.udash.web.guide.Context._

  override def getTemplate: Element = PingPongPushDemoViewPresenter()

  object PingPongPushDemoViewPresenter {
    def apply(): Element = {
      val clientId = ModelProperty[PingPongPushDemoModel]
      clientId.subProp(_.pingId).set(0)

      val presenter = new PingPongPushDemoPresenter(clientId)
      new PingPongPushDemoView(clientId, presenter).render
    }
  }

  class PingPongPushDemoPresenter(model: ModelProperty[PingPongPushDemoModel]) {
    private var registered = false

    def onButtonClick(target: JQuery) = {
      target.attr("disabled", "true")
      registerCallback(target)
      Context.serverRpc.demos().pingDemo().ping(model.subProp(_.pingId).get)
    }

    private def registerCallback(target: JQuery) = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        model.subProp(_.pingId).set(id + 1)
        target.removeAttr("disabled")
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: ModelProperty[PingPongPushDemoModel], presenter: PingPongPushDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    def render: Element = span(GuideStyles.frame)(
      button(id := "ping-pong-push-demo", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(onclick :+= ((ev: MouseEvent) => {
        presenter.onButtonClick(jQ(ev.target))
        true
      }))(produce(model.subProp(_.pingId))(p => JsDom.StringFrag(s"Ping($p)").render.asInstanceOf[dom.Element]))
    ).render
  }
}
