package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.views.rpc.demos.{PingPongCallDemoComponent, PingPongPushDemoComponent}
import scalatags.JsDom

case object RpcIntroViewFactory extends StaticViewFactory[RpcIntroState.type](() => new RpcIntroView)

class RpcIntroView extends View {
  import JsDom.all._
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = div(
    h2("Introduction"),
    p(
      "In frontend applications developers usually struggle with client-server communication. REST APIs which were popular " +
      "in last few years, are hard to maintain. They are not typed, so IDEs cannot help you with refactoring. " +
      "Usually it is also impossible to share data models between the frontend and backend code. "
    ),
    p(
      "The Udash project tries to make frontend applications as type-safe as possible. Thanks to the ",
      a(href := References.ScalaJsHomepage, target := "_blank")("ScalaJS"), " cross-compilation system, it is possible to share the code between " +
      "the client and server applications. Udash RPC uses this feature to share: "
    ),
    ul(GuideStyles.defaultList)(
      li("RPC interfaces with typed arguments and returned value."),
      li("Data models which can be used in RPC communication."),
      li("Model validators which can be used both in frontend and backend.")
    ),
    p(
      "Udash RPC also provides a server for client communication that works out of the box. You only have to create the RPC interface " +
      "and implement it - that is all, you do not have to worry about connection handling."
    ),
    h2("Ping-pong example"),
    p("Take a look at the simple ping-pong example. Click the button below and wait for a response."),
    ForceBootstrap(new PingPongCallDemoComponent),
    p("The implementation is really simple. In the server RPC interface, add the following method:"),
    CodeBlock(
      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |trait PingPongServerRPC {
        |  def ping(id: Int): Future[Int]
        |}
        |
        |object PingPongServerRPC
        |  extends DefaultServerRpcCompanion[PingPongServerRPC]""".stripMargin
    )(GuideStyles),
    p("and implement this method in your server code:"),
    CodeBlock(
      """import io.udash.rpc._
        |import java.util.concurrent.TimeUnit
        |import scala.concurrent.Future
        |
        |class PingPongEndpoint extends PingPongServerRPC {
        |  override def ping(id: Int): Future[Int] = Future {
        |    TimeUnit.SECONDS.sleep(1)
        |    id
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("Now you can call it from the client code:"),
    CodeBlock(
      """serverRpc.ping(5) onComplete {
        |  case Success(response) => println(s"Pong($response)")
        |  case Failure(ex) => println(s"PongError($ex)")
        |}""".stripMargin
    )(GuideStyles),
    p(
      "The RPC system uses some macro-generated code. To keep the JavaScript code as small as possible ",
      "and make compilation faster, for each RPC interface create companion object extending ",
      i("RPCCompanion"), " class from the RPC framework you use. The RPC framework describes supported RPC methods ",
      "and serialization methods. Usually you will probably use ", i("DefaultClientUdashRPCFramework"),
      " for the client interfaces and ", i("DefaultServerUdashRPCFramework"), " for the server API."
    ),
    h2("Server push ping-pong example"),
    p("It is also possible to implement the above example using the server push mechanism."),
    ForceBootstrap(new PingPongPushDemoComponent),
    p("This implementation is only a little more complicated. In the server RPC interface, add the following method:"),
    CodeBlock(
      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |trait PingPongServerRPC {
        |  def ping(id: Int): Unit
        |}
        |
        |object PingPongServerRPC
        |  extends DefaultServerRpcCompanion[PingPongServerRPC]""".stripMargin
    )(GuideStyles),
    p("In the client RPC interface:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |trait PingPongClientRPC {
        |  def pong(id: Int): Unit
        |}
        |
        |object PingPongServerRPC
        |  extends DefaultClientRpcCompanion[PingPongClientRPC]""".stripMargin
    )(GuideStyles),
    p("As you can see, now the server-side method does not return any value. We want it to call the client-side method."),
    CodeBlock(
      """import io.udash.rpc._
        |import java.util.concurrent.TimeUnit
        |
        |class PingPongEndpoint(implicit clientId: ClientId)
        |  extends PingPongServerRPC {
        |
        |  override def ping(id: Int): Unit = {
        |    TimeUnit.SECONDS.sleep(1)
        |    ClientRPC(target).pong(id)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("To make usage of client RPC more friendly, it is recommended to create a wrapper object like the one below:"),
    CodeBlock(
      """import io.udash.rpc._
        |import scala.concurrent.ExecutionContext
        |
        |object ClientRPC {
        |  def apply(target: ClientRPCTarget)
        |           (implicit ec: ExecutionContext): PingPongClientRPC =
        |    new DefaultClientRPC[PingPongClientRPC](target).get
        |}""".stripMargin)(GuideStyles),
    p(i("target"), " is an identity of the client connection passed to the server RPC endpoint."),
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
      "Now you know the basics of the Udash RPC system. You should take a closer look at ",
      a(href := RpcInterfacesState.url)("RPC interfaces"), " and details of ",
      a(href := RpcClientServerState.url)("backend"), " and ",
      a(href := RpcServerClientState.url)("frontend"), " implementation."
    )
  )
}
