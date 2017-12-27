package io.udash.web.guide.views.rest.demos

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton, UdashButtonGroup}
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

  trait ExampleModel {
    def string: String
    def int: Int
    def cls: RestExampleClass
  }
  object ExampleModel extends HasModelPropertyCreator[ExampleModel]

  object SimpleRestDemoViewFactory {
    def apply(): Modifier = {
      val responsesModel = ModelProperty.empty[ExampleModel]

      val presenter = new SimpleRestDemoPresenter(responsesModel)
      new SimpleRestDemoView(responsesModel, presenter).render
    }
  }

  class SimpleRestDemoPresenter(model: ModelProperty[ExampleModel]) {
    def sendStringRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Context.restServer.simple().string() onComplete {
        case Success(response) =>
          model.subProp(_.string).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendIntRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Context.restServer.simple().int() onComplete {
        case Success(response) =>
          model.subProp(_.int).set(response)
        case Failure(ex) =>
          ex.printStackTrace()
      }
    }

    def sendClassRequest(btn: UdashButton) = {
      btn.disabled.set(true)
      Context.restServer.simple().cls() onComplete {
        case Success(response) =>
          model.subProp(_.cls).set(response)
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
