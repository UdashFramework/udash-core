package io.udash.selenium.views.demos.rest

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import io.udash.selenium.Launcher
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class EchoRestDemoComponent extends CssView {

  def getTemplate: Modifier = SimpleRestDemoViewFactory()

  object SimpleRestDemoViewFactory {
    def apply(): Modifier = {
      val responseProperty = Property.blank[String]
      val presenter = new SimpleRestDemoPresenter(responseProperty)
      new SimpleRestDemoView(responseProperty, presenter).render
    }
  }

  class SimpleRestDemoPresenter(response: Property[String]) {
    def sendWithQueryRequest(content: String): Unit =
      Launcher.restServer.echo().withQuery(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithHeaderRequest(content: String): Unit =
      Launcher.restServer.echo().withHeader(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithURLRequest(content: String): Unit =
      Launcher.restServer.echo().withUrlPart(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }

    def sendWithBodyRequest(content: String): Unit =
      Launcher.restServer.echo().withBody(content) onComplete {
        case Success(v) =>
          response.set(v)
        case Failure(ex) =>
          response.set(s"Error: $ex!")
      }
  }

  class SimpleRestDemoView(response: Property[String], presenter: SimpleRestDemoPresenter) {
    import JsDom.all._

    val content = Property("a b !@#$%^&*()_+")

    private val queryButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-query-btn")
    )(_ => "Query")
    private val headerButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-header-btn")
    )(_ => "Header")
    private val urlButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-url-btn")
    )(_ => "URL")
    private val bodyButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
      componentId = ComponentId("echo-rest-demo-body-btn")
    )(_ => "Body")

    queryButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.sendWithQueryRequest(content.get)
    }
    headerButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.sendWithHeaderRequest(content.get)
    }
    urlButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.sendWithURLRequest(content.get)
    }
    bodyButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        presenter.sendWithBodyRequest(content.get)
    }

    def render: Modifier = span(id := "echo-rest-demo")(
      UdashInputGroup()(
        UdashInputGroup.input(
          TextInput(content)(id := "echo-rest-demo-input").render
        ),
        UdashInputGroup.append(
          queryButton.render,
          headerButton.render,
          urlButton.render,
          bodyButton.render
        )
      ).render,
      div(id := "echo-rest-demo-response")(
        h3("Response: "),
        bind(response)
      )
    )
  }
}
