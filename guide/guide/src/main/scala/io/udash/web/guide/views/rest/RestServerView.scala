package io.udash.web.guide.views.rest

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object RestServerViewFactory extends StaticViewFactory[RestServerState.type](() => new RestServerView)

class RestServerView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Exposing REST interface"),
    p(
      "In order to reuse RPC interfaces in HTTP communication or to create a REST-like API ",
      "you can expose an Udash REST interface as a HTTP endpoint. "
    ),
    p(
      "Notice: Udash support for exposing REST-like interfaces was not designed for production REST APIs. ",
      "The goal is to reuse RPC interfaces in communication without websockets or exposing them as internal API, which ",
      "should be consumed with Udash REST client. It might be useful in black-box testing of application, ",
      "but if you want to expose real REST API you should use a dedicated tool like ",
      a(href := References.AkkaHttpHomepage)("Akka HTTP"), "."
    ),
    p("First of all, you need to implement your interfaces. Take a look at this simple example from the guide: "),
    CodeBlock(
      """class ExposedRestInterfaces extends MainServerREST {
        |  override def simple(): SimpleServerREST = new SimpleServerREST {
        |    override def string(): Future[String] =
        |      Future.successful("OK")
        |    override def cls(): Future[RestExampleClass] =
        |      Future.successful(RestExampleClass(42, "Udash", (321.123, "REST")))
        |    override def int(): Future[Int] =
        |      Future.successful(123)
        |  }
        |  override def echo(): EchoServerREST = new EchoServerREST {
        |    override def withUrlPart(arg: String): Future[String] =
        |      Future.successful(s"URL:$arg")
        |    override def withQuery(arg: String): Future[String] =
        |      Future.successful(s"Query:$arg")
        |    override def withHeader(arg: String): Future[String] =
        |      Future.successful(s"Header:$arg")
        |    override def withBody(arg: String): Future[String] =
        |      Future.successful(s"Body:$arg")
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("Now create a servlet to expose the implementation:"),
    CodeBlock(
      """import io.udash.rest.server._
        |val restImpl = new ExposedRestInterfaces
        |new RestServlet(new DefaultExposesREST[MainServerREST](restImpl))""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "That wraps all the knowledge needed to start working with wrapped REST interfaces. ",
      "You may find the ", a(href := RpcIntroState.url)("RPC communication"), " chapter interesting later on. "
    )
  )
}