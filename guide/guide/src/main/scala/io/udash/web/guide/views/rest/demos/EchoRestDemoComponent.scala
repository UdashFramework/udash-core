package io.udash.web.guide.views.rest.demos

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

class EchoRestDemoComponent extends Component {
  import Context._

  override def getTemplate: Modifier = SimpleRestDemoViewFactory()

  object SimpleRestDemoViewFactory {
    def apply(): Modifier = {
      val responseProperty = Property.blank[String]
      val presenter = new SimpleRestDemoPresenter(responseProperty)
      new SimpleRestDemoView(responseProperty, presenter).render
    }
  }

  class SimpleRestDemoPresenter(response: Property[String]) {
    def sendWithQueryRequest(content: String) =
      Context.restServer.echo().withQuery(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithHeaderRequest(content: String) =
      Context.restServer.echo().withHeader(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithURLRequest(content: String) =
      Context.restServer.echo().withUrlPart(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithBodyRequest(content: String) =
      Context.restServer.echo().withBody(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }
  }

  class SimpleRestDemoView(response: Property[String], presenter: SimpleRestDemoPresenter) {
    import JsDom.all._

    val content = Property("a b !@#$%^&*()_+")

    val queryButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-query-btn")
    )(_ => "Query")
    val headerButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-header-btn")
    )(_ => "Header")
    val urlButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-url-btn")
    )(_ => "URL")
    val bodyButton = UdashButton(
      buttonStyle = Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-body-btn")
    )(_ => "Body")

    queryButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.sendWithQueryRequest(content.get)
    }
    headerButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.sendWithHeaderRequest(content.get)
    }
    urlButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.sendWithURLRequest(content.get)
    }
    bodyButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.sendWithBodyRequest(content.get)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap, id := "echo-rest-demo")(
      UdashInputGroup()(
        UdashInputGroup.input(
          TextInput(content)(id := "echo-rest-demo-input").render
        ),
        UdashInputGroup.appendButton(queryButton.render),
        UdashInputGroup.appendButton(headerButton.render),
        UdashInputGroup.appendButton(urlButton.render),
        UdashInputGroup.appendButton(bodyButton.render)
      ).render,
      div(id := "echo-rest-demo-response")(
        h3("Response: "),
        bind(response)
      )
    )
  }
}
