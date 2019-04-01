package io.udash.web.guide.views.rest.demos

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rest.RestExampleClass
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

class SimpleRestDemoComponent extends Component {
  import Context._

  override def getTemplate: Modifier = SimpleRestDemoViewFactory()

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
    def sendStringRequest(btn: UdashButton, disabled: Property[Boolean]) = {
      disabled.set(true)
      Context.restServer.simple().string() onComplete {
        case Success(response) =>
          model.subProp(_.string).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendIntRequest(btn: UdashButton, disabled: Property[Boolean]) = {
      disabled.set(true)
      Context.restServer.simple().int() onComplete {
        case Success(response) =>
          model.subProp(_.int).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendClassRequest(btn: UdashButton, disabled: Property[Boolean]) = {
      disabled.set(true)
      Context.restServer.simple().cls() onComplete {
        case Success(response) =>
          model.subProp(_.cls).set(Some(response))
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }
  }

  class SimpleRestDemoView(model: ModelProperty[ExampleModel], presenter: SimpleRestDemoPresenter) {
    import JsDom.all._

    val loadStringButtonDisabled = Property(false)
    val loadStringButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      disabled = loadStringButtonDisabled,
      componentId = ComponentId("simple-rest-demo-string-btn")
    )(_ => "Get string")
    val loadIntButtonDisabled = Property(false)
    val loadIntButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      disabled = loadIntButtonDisabled,
      componentId = ComponentId("simple-rest-demo-int-btn")
    )(_ => "Get integer")
    val loadClassButtonDisabled = Property(false)
    val loadClassButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      disabled = loadClassButtonDisabled,
      componentId = ComponentId("simple-rest-demo-class-btn")
    )(_ => "Get class")

    loadStringButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendStringRequest(btn, loadStringButtonDisabled)
    }
    loadIntButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendIntRequest(btn, loadIntButtonDisabled)
    }
    loadClassButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) => presenter.sendClassRequest(btn, loadClassButtonDisabled)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap, id := "simple-rest-demo")(
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
