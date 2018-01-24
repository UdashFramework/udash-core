package io.udash.web.guide.views.rest

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.rest.demos.SimpleRestDemoComponent

import scalatags.JsDom

case object RestIntroViewFactory extends StaticViewFactory[RestIntroState.type](() => new RestIntroView)

class RestIntroView extends FinalView with CssView {
  import io.udash.web.guide.Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Introduction"),
    p(
      "Communication via WebSockets is not always attainable. If your application needs to use a REST API, then the Udash REST ",
      "module can help you to keep it clean, maintainable and type-safe."
    ),
    p("The Udash REST module provides: "),
    ul(GuideStyles.defaultList)(
      li("REST APIs wrapped with typed interfaces."),
      li("Type-safe API usage with results returned as Futures."),
      li("Sending method arguments as URL part, body, query or header."),
      li("Exposing RPC interfaces as REST-like endpoints."),
      li("Customizable serialization.")
    ),
    h2("Simple REST client example"),
    p("Take a look at the simple REST usage example. Click the buttons below and wait for a response."),
    ForceBootstrap(new SimpleRestDemoComponent),
    p("The Developer's Guide server publishes a REST API with the following methods:"),
    ul(GuideStyles.defaultList)(
      li("GET /simple/string,"),
      li("GET /simple/int,"),
      li("GET /simple/class.")
    ),
    p("This API is wrapped by an interface:"),
    simpleExample(),
    p(
      "The details of wrapping REST APIs will be discussed in the next chapter. In the ",
      i("Context"), " object, the frontend application initializes a REST connector."
    ),
    CodeBlock(
      """val restServer = DefaultServerREST[MainServerREST](
        |  dom.window.location.hostname,
        |  Try(dom.window.location.port.toInt).getOrElse(80),
        |  "/rest"
        |)""".stripMargin
    )(GuideStyles),
    p("Now it is possible to call REST API methods:"),
    CodeBlock(
      """Context.restServer.simple().cls() onComplete {
        |  case Success(response) =>
        |    // Handle received value
        |  case Failure(ex) =>
        |    // Handle error
        |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "You learnt the basics of the Udash REST support. You should also take a closer look at the ",
      a(href := RestInterfacesState.url)("REST interfaces"), " description. "
    )
  )
}
