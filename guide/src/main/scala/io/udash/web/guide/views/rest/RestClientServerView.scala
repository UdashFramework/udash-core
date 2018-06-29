package io.udash.web.guide.views.rest

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.rest.demos.EchoRestDemoComponent
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object RestClientServerViewFactory extends StaticViewFactory[RestClientServerState.type](() => new RestClientServerView)

class RestClientServerView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("REST interface usage"),
    p(
      "Once the REST API wrapper interface is prepared, you can create a REST connector based on it. ",
      "The code below presents creation of an example connector: "
    ),
    CodeBlock(
      """val restServer = DefaultServerREST[MainServerREST](
        |  dom.window.location.hostname,
        |  Try(dom.window.location.port.toInt).getOrElse(80),
        |  "/rest"
        |)""".stripMargin
    )(GuideStyles),
    p(
      "First of all, it requires a root REST interface as a generic attribute. ",
      i("DefaultServerREST"), " takes three arguments in the constructor: hostname, port and URL prefix. ",
      "It uses them to create a full URL for HTTP requests as follows: ", i("<hostname>:<port><url_prefix><method_mapping>"), "."
    ),
    p("The following demo uses a REST interface to call the echo server API: "),
    CodeBlock(
      """trait EchoServerREST {
        |  def withQuery(@Query @RESTParamName("param") arg: String): Future[String]
        |  def withHeader(@Header @RESTParamName("X-test") arg: String): Future[String]
        |  def withUrlPart(@URLPart arg: String): Future[String]
        |  def withBody(@Body arg: String): Future[String]
        |}""".stripMargin
    )(GuideStyles),
    p("Text from the input below will be used as the call argument. Click the button to send a request."),
    ForceBootstrap(new EchoRestDemoComponent),
    h2("What's next?"),
    p(
      "That wraps all the knowledge needed to start working with wrapped REST interfaces. As mentioned before it is possible ",
      "to expose prepared interfaces with ", a(href := RestServerState.url)("Udash server-side endpoint"), ". ",
      "You may also find the ", a(href := RpcIntroState.url)("RPC communication"), " chapter interesting later on. "
    )
  )
}
