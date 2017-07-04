package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, _}

import scalatags.JsDom


case object RpcInterfacesViewFactory extends StaticViewFactory[RpcInterfacesState.type](() => new RpcInterfacesView)

class RpcInterfacesView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Interfaces"),
    p(
      "Interfaces are the most important part of the Udash RPC system. Thanks to the cross compilation, they make client-server " +
      "communication easy to develop and maintain. You can find two types of RPC interfaces in Udash: "
    ),
    ul(GuideStyles.defaultList)(
      li(i("RPC"), " - the RPC interface exposed by the server-side"),
      li(i("Client RPC"), " - the RPC interface exposed by the client-side")
    ),
    p("Methods exposed by the RPC interface can be divided into three groups:"),
    ul(GuideStyles.defaultList)(
      li(i("Calls"), " - methods returning ", i("Future[T]"), " where ", i("T"), " is a serializable type (a client RPC interface cannot expose these methods)"),
      li(i("Fires"), " - methods with a return type ", i("Unit"), ", there is no guarantee that your request will be received by a recipient"),
      li(i("Getters"), " - methods returning another RPC interface, calling this method does not send anything over network")
    ),
    p(
      "Both call and fire methods are asynchronous. Call will complete a returned Future when response is received. Fire is a ",
      "\"fire&forget\" method, there is no acknowledgement that the request reached its recipient."
    ),
    h3("Server-side RPC interface"),
    p("Let's take a look at the following example of the server-side RPC interface:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |case class Record(i: Int, fuu: String)
        |
        |@RPC
        |trait ServerRPC {
        |  def fire(): Unit
        |  def fireWithArgs(num: Int): Unit
        |  @RPCName("fireWithManyArgsLists")
        |  def fireWithArgs(i: Int, s: String)(o: Option[Boolean]): Unit
        |  def fireWithCaseClass(r: Record): Unit
        |  def call(yes: Boolean): Future[String]
        |  def innerRpc(name: String): InnerRPC
        |}
        |
        |@RPC
        |trait InnerRPC {
        |  def innerFire(): Unit
        |  def innerCall(arg: Int): Future[String]
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Inside ", i("ServerRPC"), " you can find all mentioned method types. The RPC system also supports multiple arguments lists. ",
      i("@RPCName"), " allows you to change a method name for serialization purposes, it is useful for overloaded methods in the RPC interface. ",
      "Take a look at the example of RPC usage:"
    ),
    CodeBlock(
      """def useRpc(rpc: ServerRPC): Unit = {
        |  rpc.fire()
        |  rpc.fireWithArgs(42, "Udash is the best!")(None)
        |  rpc.call(true) onComplete {
        |    case Success(response) => println(response)
        |    case Failure(ex) => println(ex)
        |  }
        |
        |  // innerRpc gets argument which can be passed to the InnerRPC implementation
        |  rpc.innerRpc("some string argument").innerFire()
        |
        |  // this line does not send anything over network
        |  val innerRpc = rpc.innerRpc("Udash")
        |  innerRpc.innerCall(42) onComplete {
        |    case Success(response) => println(response)
        |    case Failure(ex) => println(ex)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("Important: method call returning another RPC interface does not send anything over the network."),
    h3("Client-side RPC interface"),
    p(
      "Client RPC interfaces are similar to the server ones, with one important difference - they cannot contain any ",
      i("call"), " methods. For example: "
    ),
    CodeBlock(
      """import io.udash.rpc._
        |
        |case class Record(i: Int, fuu: String)
        |
        |@RPC
        |trait ClientRPC {
        |  def fire(): Unit
        |  def fireWithArgs(num: Int): Unit
        |  @RPCName("fireWithManyArgsLists")
        |  def fireWithArgs(i: Int, s: String)(o: Option[Boolean]): Unit
        |  def fireWithCaseClass(r: Record): Unit
        |  def innerRpc(name: String): ClientInnerRPC
        |}
        |
        |@RPC
        |trait ClientInnerRPC {
        |  def innerFire(): Unit
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Notice that ", i("ClientInnerRPC"), " is also a client RPC interface. It is impossible to return a standard RPC ",
      "interface inside client RPC."
    ),
    p(
      "The client RPC cannot contain ", i("call"), " methods, because it can broadcast to many clients. It is hard to decide ",
      "when a broadcasted call can be assumed as finished without any use case context."
    ),
    h2("RPC interfaces hierarchy"),
    p(
      "The Udash RPC system makes creating RPC interfaces hierarchy easy. Inside the application you can create one ",
      i("MainServerRPC"), " and one ", i("MainClientRPC"), " which will give access to other service RPC interfaces."
    ),
    CodeBlock(
      """import io.udash.rpc._
        |
        |@RPC
        |trait MainServerRPC {
        |  def auth(): AuthenticationRPC
        |  def newsletter(): NewsletterRPC
        |}
        |
        |@RPC
        |trait AuthenticationRPC {
        |  def login(username: String, password: String): Future[AuthToken]
        |}
        |
        |@RPC
        |trait NewsletterRPC {
        |  def loadNews(limit: Int, skip: Int): Future[Seq[News]]
        |  def subscriptions(): NewsletterSubscriptionRPC
        |}
        |
        |@RPC
        |trait NewsletterSubscriptionRPC {
        |  def subscribe(): Unit
        |  def unsubscribe(reason: String): Unit
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Thanks to such interface arrangement, you have only one entry point to RPC communication. Do not worry that your hierarchy " +
      "may be too deep - you can obtain a nested interface, without any performance impact in the following way: "
    ),
    CodeBlock(
      """def doSomething(rpc: MainServerRPC) = {
        |  val subscriptions: NewsletterSubscriptionRPC = rpc.newsletter().subscriptions()
        |  // operations using NewsletterSubscriptionRPC...
        |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Now you know more about Udash RPC interfaces. You might also want to take a look at ",
      a(href := RpcClientServerState.url)("Client ➔ Server"), " & ", a(href := RpcClientServerState.url)("Server ➔ Client"), " communication or ",
      "the ", a(href := RpcSerializationState.url)("Udash serialization"), " mechanism."
    )
  )
}
