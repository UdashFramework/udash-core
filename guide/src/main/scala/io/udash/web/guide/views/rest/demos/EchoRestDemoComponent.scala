package io.udash.web.guide.views.rest.demos

import io.udash._
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rest.RestExampleClass
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom
import scalatags.JsDom.all._
import io.udash.web.commons.views.Component

class EchoRestDemoComponent extends Component {
  import Context._

  override def getTemplate: Modifier = SimpleRestDemoViewPresenter()

  object SimpleRestDemoViewPresenter {
    def apply(): Modifier = {
      val responseProperty = Property[String]
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
    import scalacss.ScalatagsCss._

    val content = Property("a b !@#$%^&*()_+")

    val queryButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("echo-rest-demo-query-btn")
    )("Query")
    val headerButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("echo-rest-demo-header-btn")
    )("Header")
    val urlButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("echo-rest-demo-url-btn")
    )("URL")
    val bodyButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("echo-rest-demo-body-btn")
    )("Body")

    queryButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.sendWithQueryRequest(content.get)
    }
    headerButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.sendWithHeaderRequest(content.get)
    }
    urlButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.sendWithURLRequest(content.get)
    }
    bodyButton.listen {
      case UdashButton.ButtonClickEvent(btn) =>
        presenter.sendWithBodyRequest(content.get)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap, id := "echo-rest-demo")(
      UdashInputGroup()(
        UdashInputGroup.input(
          TextInput.debounced(content, id := "echo-rest-demo-input").render
        ),
        UdashInputGroup.buttons(
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
