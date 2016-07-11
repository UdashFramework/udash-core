package io.udash.web.guide.views.rest.demos

import io.udash._
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rest.RestExampleClass
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom

class EchoRestDemoComponent extends Component {
  import Context._

  override def getTemplate: Element = SimpleRestDemoViewPresenter()

  object SimpleRestDemoViewPresenter {
    def apply(): Element = {
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

    // TODO migrate to bootstrap components
    def render: Element = span(GuideStyles.frame, id := "echo-rest-demo")(
      TextInput.debounced(content, id := "echo-rest-demo-input"),
      button(id := "echo-rest-demo-query-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendWithQueryRequest(content.get)
          true
        })
      )("Query"),
      button(id := "echo-rest-demo-header-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendWithHeaderRequest(content.get)
          true
        })
      )("Header"),
      button(id := "echo-rest-demo-url-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendWithURLRequest(content.get)
          true
        })
      )("URL"),
      button(id := "echo-rest-demo-body-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendWithBodyRequest(content.get)
          true
        })
      )("Body"),
      div("Response: ", bind(response), id := "echo-rest-demo-response")
    ).render
  }
}
