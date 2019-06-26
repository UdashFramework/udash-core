package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.activity.Call
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class LoggingDemoComponent extends CssView {
  def getTemplate: Modifier = LoggingDemoViewFactory()

  object LoggingDemoViewFactory {
    def apply(): Modifier = {
      val calls = SeqProperty.blank[Call]

      val presenter = new LoggingDemoPresenter(calls)
      new LoggingDemoView(calls, presenter).render
    }
  }

  class LoggingDemoPresenter(calls: SeqProperty[Call]) {
    def onButtonClick(): Unit = {
      Launcher.serverRpc.demos().call().calls onComplete {
        case Success(data) => calls.set(data)
        case Failure(_) => calls.clear()
      }
    }
  }

  class LoggingDemoView(model: SeqProperty[Call], presenter: LoggingDemoPresenter) {
    import JsDom.all._

    private val loadCallsButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      componentId = ComponentId("load-calls-btn")
    )(_ => "Load calls list")

    loadCallsButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.onButtonClick()
    }

    def render: Modifier = span(
      loadCallsButton.render,
      produce(model)(seq =>
        ul(id := "calls-list")(
          seq.map(call => li(call.toString))
        ).render
      )
    )
  }
}
