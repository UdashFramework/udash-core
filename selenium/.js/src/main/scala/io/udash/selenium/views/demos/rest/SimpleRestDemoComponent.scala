package io.udash.selenium.views.demos.rest

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton, UdashButtonGroup}
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.rest.RestExampleClass
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class SimpleRestDemoComponent extends CssView {

  def getTemplate: Modifier = SimpleRestDemoViewFactory()

  class ExampleModel(
    val string: String,
    val int: Int,
    val cls: Option[RestExampleClass]
  )
  object ExampleModel extends HasModelPropertyCreator[ExampleModel]

  object SimpleRestDemoViewFactory {
    def apply(): Modifier = {
      val responsesModel = ModelProperty(new ExampleModel("-", 0, None))
      val presenter = new SimpleRestDemoPresenter(responsesModel)
      new SimpleRestDemoView(responsesModel, presenter).render
    }
  }

  class SimpleRestDemoPresenter(model: ModelProperty[ExampleModel]) {
    def sendStringRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Launcher.restServer.simple().string() onComplete {
        case Success(response) =>
          model.subProp(_.string).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendIntRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Launcher.restServer.simple().int() onComplete {
        case Success(response) =>
          model.subProp(_.int).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendClassRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Launcher.restServer.simple().cls() onComplete {
        case Success(response) =>
          model.subProp(_.cls).set(Some(response))
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }
  }

  class SimpleRestDemoView(model: ModelProperty[ExampleModel], presenter: SimpleRestDemoPresenter) {
    import JsDom.all._

    val loadStringButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("simple-rest-demo-string-btn")
    )("Get string")
    val loadIntButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("simple-rest-demo-int-btn")
    )("Get integer")
    val loadClassButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("simple-rest-demo-class-btn")
    )("Get class")

    loadStringButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendStringRequest(btn)
    }
    loadIntButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendIntRequest(btn)
    }
    loadClassButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendClassRequest(btn)
    }

    def render: Modifier = span(id := "simple-rest-demo")(
      UdashButtonGroup()(
        loadStringButton.render,
        loadIntButton.render,
        loadClassButton.render
      ).render,
      h3("Results:"),
      div("String: ", bind(model.subProp(_.string))),
      div("Int: ", bind(model.subProp(_.int))),
      div("Class: ", bind(model.subProp(_.cls)))
    )
  }
}
