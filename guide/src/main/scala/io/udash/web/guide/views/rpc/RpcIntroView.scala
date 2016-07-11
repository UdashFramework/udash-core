package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.views.rpc.demos.{PingPongCallDemoComponent, PingPongPushDemoComponent}
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom

case object RpcIntroViewPresenter extends DefaultViewPresenterFactory[RpcIntroState.type](() => new RpcIntroView)

class RpcIntroView extends View {
  import io.udash.web.guide.Context._
  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h2("Introduction"),
    p(
      "In frontend applications developers usually struggle with client-server communication. REST APIs witch were popular " +
      "in last few years, are hard to maintain. They are not typed, so IDEs cannot help you with refactoring. " +
      "Usually it is also impossible to share data models between the frontend and backend code. "
    ),
    p(
      "The Udash project tries to make frontend applications as type safe as possible. Thanks to the ",
      a(href := References.ScalaJsHomepage)("ScalaJS"), " cross-compilation system, it is possible to share the code between " +
      "the client and server applications. Udash RPC uses this feature to share: "
    ),
    ul(GuideStyles.defaultList)(
      li("RPC interfaces with typed arguments and returned value"),
      li("Data models which can be used in RPC communication"),
      li("Model validators witch can be used both in frontend and backend")
    ),
    p(
      "Udash RPC also provides a server for client communication that works out of the box. You only have to create the RPC interface " +
      "and implement it - that is all, you do not have to worry about connection handling."
    ),
    h2("Ping-pong example"),
    p("Take a look at the simple ping-pong example. Click the button below and wait for a response."),
    new PingPongCallDemoComponent,
    p("The implementation is really simple. In the server RPC interface, add the following method:"),
    CodeBlock(
      """import com.avsystem.commons.rpc.RPC
        |
        |@RPC
        |trait PingPongServerRPC {
        |  def fPing(id: Int): Future[Int]
        |}""".stripMargin
    )(GuideStyles),
    p("and implement this method in your server code:"),
    CodeBlock(
      """class PingPongEndpoint extends PingPongServerRPC {
        |  override def fPing(id: Int): Future[Int] = {
        |    TimeUnit.SECONDS.sleep(1)
        |    Future.successful(id)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("Now you can call it from the client code:"),
    CodeBlock(
      """serverRpc.fPing(fPingId) onComplete {
        |  case Success(response) => println(s"Pong($response)")
        |  case Failure(ex) => println(s"PongError($ex)")
        |}""".stripMargin
    )(GuideStyles),
    h2("Server push ping-pong example"),
    p("It is also possible to implement the above example using the server push mechanism."),
    new PingPongPushDemoComponent,
    p("This implementation is only a little more complicated. In the server RPC interface, add the following method:"),
    CodeBlock(
      """import com.avsystem.commons.rpc.RPC
        |
        |@RPC
        |trait PingPongServerRPC {
        |  def ping(id: Int): Unit
        |}""".stripMargin
    )(GuideStyles),
    p("In the client RPC interface:"),
    CodeBlock(
      """import com.avsystem.commons.rpc.RPC
        |
        |@RPC
        |trait PingPongClientRPC {
        |  def pong(id: Int): Unit
        |}""".stripMargin
    )(GuideStyles),
    p("As you can see, now the server-side method does not return any value. We want it to call the client-side method."),
    CodeBlock(
      """class PingPongEndpoint extends PingPongServerRPC {
        |  override def ping(id: Int): Unit = {
        |    TimeUnit.SECONDS.sleep(1)
        |    ClientRPC(clientId).pong(id)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p(i("clientId"), " is an identity of the client connection passed to the server RPC endpoint."),
    p("There is only the client-side method implementation left:"),
    CodeBlock(
      """class PingPongPushEndpoint extends PingPongClientRPC {
        |  override def pong(id: Int): Unit =
        |    println(s"Pong($id)")
        |}""".stripMargin
    )(GuideStyles),
    p("Now you can call it:"),
    CodeBlock("""serverRpc.ping(pingId)""".stripMargin)(GuideStyles),
    h2("What's next?"),
    p(
      "Now you know the basics of the Udash RPC system. You should also take a closer look at ",
      a(href := RpcInterfacesState.url)("RPC interfaces"), "."
    )
  ).render

  override def renderChild(view: View): Unit = ()
}