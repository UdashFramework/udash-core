package io.udash.selenium.views.demos.rest

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId}
import io.udash.css.CssView
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.rest.RestExampleClass
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class SimpleRestDemoComponent extends CssView {

  def getTemplate: Modifier = SimpleRestDemoViewFactory()

  class ExampleModel(
    val string: String,
    val int: Int,
    val cls: Option[RestExampleClass],

    val loadingString: Boolean,
    val loadingInt: Boolean,
    val loadingCls: Boolean
  )
  object ExampleModel extends HasModelPropertyCreator[ExampleModel]

  object SimpleRestDemoViewFactory {
    def apply(): Modifier = {
      val responsesModel = ModelProperty(new ExampleModel("-", 0, None, false, false, false))
      val presenter = new SimpleRestDemoPresenter(responsesModel)
      new SimpleRestDemoView(responsesModel, presenter).render
    }
  }

  class SimpleRestDemoPresenter(model: ModelProperty[ExampleModel]) {
    def sendStringRequest(): Unit = {
      Launcher.restServer.simple().string() onComplete {
        case Success(response) =>
          model.subProp(_.string).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendIntRequest(): Unit = {
      Launcher.restServer.simple().int() onComplete {
        case Success(response) =>
          model.subProp(_.int).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendClassRequest(): Unit = {
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

    private val loadStringButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = model.subProp(_.loadingString),
      componentId = ComponentId("simple-rest-demo-string-btn")
    )(_ => "Get string")
    private val loadIntButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = model.subProp(_.loadingInt),
      componentId = ComponentId("simple-rest-demo-int-btn")
    )(_ => "Get integer")
    private val loadClassButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      disabled = model.subProp(_.loadingCls),
      componentId = ComponentId("simple-rest-demo-class-btn")
    )(_ => "Get class")

    loadStringButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        model.subProp(_.loadingString).set(true)
        presenter.sendStringRequest()
    }
    loadIntButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        model.subProp(_.loadingInt).set(true)
        presenter.sendIntRequest()
    }
    loadClassButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        model.subProp(_.loadingCls).set(true)
        presenter.sendClassRequest()
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
