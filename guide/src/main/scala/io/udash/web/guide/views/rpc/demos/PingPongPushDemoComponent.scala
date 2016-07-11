package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.web.commons.styles.attributes.Attributes
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

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      registerCallback(btn)
      Context.serverRpc.demos().pingDemo().ping(model.subProp(_.pingId).get)
    }

    private def registerCallback(btn: UdashButton) = if (!registered) {
      val listener: Int => Any = (id: Int) => {
        model.subProp(_.pingId).set(id + 1)
        btn.disabled.set(false)
      }
      PingClient.registerPongListener(listener)
      registered = true
    }
  }

  class PingPongPushDemoView(model: ModelProperty[PingPongPushDemoModel], presenter: PingPongPushDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    val pingButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("ping-pong-push-demo")
    )("Ping(", bind(model.subProp(_.pingId)), ")")

    pingButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.onButtonClick(btn)
    }

    def render: Element = span(GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
      pingButton.render
    ).render
  }
}
