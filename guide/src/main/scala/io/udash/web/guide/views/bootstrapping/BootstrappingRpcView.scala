package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object BootstrappingRpcViewFactory extends StaticViewFactory[BootstrappingRpcState.type](() => new BootstrappingRpcView)

class BootstrappingRpcView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Bootstrapping RPC interfaces"),
    p(
      "Creating RPC interfaces for the Udash application is pretty simple. Inside the ", i("shared"),
      " module, define two traits annotated with ", i("com.avsystem.commons.rpc.RPC"), ": "
    ),
    ul(GuideStyles.defaultList)(
      li("MainClientRPC - contains methods which can be called by a server on a client application"),
      li("MainServerRPC - contains methods which can be called by a client on a server.")
    ),
    p(
      "That is all you have to do in the ", i("shared"), " module. Implementation of those interfaces will be covered in ",
      a(href := BootstrappingBackendState.url)("backend"), " and ", a(href := BootstrappingFrontendState.url)("frontend"),
      " bootstrapping chapters."
    ),
    h3("RPC vs Client RPC"),
    p(
      "An ", i("com.avsystem.commons.rpc.RPC"), " is an annotation marking all RPC interfaces. A RPC interface is a trait or class " +
      "whose abstract methods will be interpreted as remote methods by the RPC framework. Remote methods must be defined " +
      "according to following rules:"
    ),
    ul(GuideStyles.defaultList)(
      li("Types of arguments must be ", a(href := RpcSerializationState.url)("serializable")),
      li("Return type must be either Unit or Future[T] where T is a type serializable or another RPC interface"),
      li("Method must not have type parameters.")
    ),
    p(
      "RPC interfaces may also have non-abstract members - these will be invoked locally. However, they may invoke " +
      "remote members in their implementations."
    ),
    p(
      "Client RPC is basically the same as standard RPC interface, but it cannot contain abstract methods returning Future[T]. ",
      "The reason for this is that those methods can be called on many clients and there is no standard way of collecting the results. ",
      "Collecting the results can be implemented with one call from the server to the clients and another call from the clients to the server. ",
      "Notice that both a standard RPC and a Client RPC are annotated with ", i("@RPC"), ". Client RPC can be used in every place where a RPC is expected, ",
      "but you can not put a standard RPC where a Client RPC is expected."
    ),
    h4("Examples"),
    p("Example of RPC interfaces:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |@RPC
        |trait MainClientRPC {
        |  def pong(id: Int): Unit
        |}""".stripMargin)(GuideStyles),
    CodeBlock(
      """import io.udash.rpc._
        |
        |@RPC
        |trait MainServerRPC {
        |  def ping(id: Int): Unit
        |  def hello(name: String): Future[String]
        |}""".stripMargin)(GuideStyles),
    h2("What's next?"),
    p(
      "When RPC interfaces are ready, it is time to bootstrap the ", a(href := BootstrappingBackendState.url)("server-side"),
      " of the application. You can also read more about ", a(href := RpcIntroState.url)("RPC in Udash"), ""
    )
  )
}