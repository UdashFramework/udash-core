package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.rpc.demos.NotificationsDemoComponent
import scalatags.JsDom

case object RpcServerClientViewFactory extends StaticViewFactory[RpcServerClientState.type](() => new RpcServerClientView)

class RpcServerClientView extends View with CssView {
  import JsDom.all._
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = div(
    h2("Server ➔ client communication"),
    p(
      "Modern web applications often notify users about some events, for example about finishing a background task. ",
      "In a Udash application it is really easy to create such notifications. All you have to do is: "
    ),
    ul(GuideStyles.defaultList)(
      li("Prepare RPC interfaces as described in the ", a(href := RpcInterfacesState.url)("RPC interfaces"), " chapter."),
      li("Implement such interface in your frontend code."),
      li("Use ", i("DefaultServerRPC"), " in the frontend code to create a server connection."),
      li("Use ", i("DefaultClientRPC"), " in the backend code to push notifications from the server to the client.")
    ),
    h2("Server connection"),
    p(
      "As you might remember from the ", a(href := RpcClientServerState.url)("Client ➔ server communication"),
      " chapter, in frontend code we can create the server connection in the following way:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](
        |  new FrontendRPCService
        |  // you can also pass a server URL, an exceptions registry or
        |  // RPC failure interceptors (global handlers of exceptions thrown by server) here
        |)""".stripMargin
    )(GuideStyles),
    p("The ", i("FrontendRPCService"), " is a ", i("MainClientRPC"), " implementation. For example:"),
    CodeBlock(
      """class FrontendRPCService extends MainClientRPC {
        |  /** Methods implementation... */
        |}""".stripMargin
    )(GuideStyles),
    p("That is all you have to do in the frontend code."),
    h2("Push notifications"),
    p(
      "Now it is time for the most interesting thing - sending messages from the server to the client. In your backend code, ",
      "creating a wrapper around the ", i("DefaultClientRPC"), " class can be useful."
    ),
    CodeBlock(
      """import io.udash.rpc._
        |
        |object ClientRPC {
        |  def apply(target: ClientRPCTarget)
        |           (implicit ec: ExecutionContext): MainClientRPC =
        |    new DefaultClientRPC[MainClientRPC](target).get
        |}""".stripMargin
    )(GuideStyles),
    h3("Broadcasting messages"),
    p("With the above ", i("ClientRPC"), " wrapper, it is easy to broadcast the method call to all active connections:"),
    CodeBlock("""ClientRPC(AllClients).methodFromMainClientRPC()""")(GuideStyles),
    h3("Message to concrete client"),
    p("You can also select a specific connection by passing ", i("ClientId"), ":"),
    CodeBlock("""ClientRPC(ClientId(???)).methodFromMainClientRPC()""")(GuideStyles),
    p(
      "You can find out how to obtain ", i("ClientId"), " in the ",
      a(href := RpcClientServerState.url)("Client ➔ Server"), " chapter."
    ),
    h2("Notifications example"),
    ForceBootstrap(new NotificationsDemoComponent),
    p("The code of the example above:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |trait NotificationsClientRPC {
        |  def notify(msg: String): Unit
        |}
        |
        |// generate RPC metadata for DefaultClientUdashRPCFramework
        |object NotificationsClientRPC
        |  extends DefaultClientRpcCompanion[NotificationsClientRPC]
        |
        |trait NotificationsServerRPC {
        |  def register(): Future[Unit]
        |  def unregister(): Future[Unit]
        |}
        |
        |// generate RPC metadata for DefaultServerUdashRPCFramework
        |object NotificationsServerRPC
        |  extends DefaultServerRpcCompanion[NotificationsServerRPC]""".stripMargin
    )(GuideStyles),
    CodeBlock (
      """/** Client implementation */
        |object NotificationsClient extends NotificationsClientRPC {
        |  import Context._
        |  private val listeners = scala.collection.mutable.ArrayBuffer[(String) => Any]()
        |
        |  def registerListener(listener: (String) => Any): Future[Unit] = {
        |    listeners += listener
        |    // register for server notifications
        |    if (listeners.size == 1) serverRpc.notificationsDemo().register()
        |    else Future.unit
        |  }
        |
        |  def unregisterListener(listener: (String) => Any): Future[Unit] = {
        |    listeners -= listener
        |    // unregister
        |    if (listeners.isEmpty) serverRpc.notificationsDemo().unregister()
        |    else Future.unit
        |  }
        |
        |  override def notify(msg: String): Unit = {
        |    listeners.foreach(_(msg))
        |  }
        |}""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """/** Server implementation */
        |class NotificationsServer(implicit clientId: ClientId)
        |  extends NotificationsServerRPC {
        |
        |  override def register(): Future[Unit] =
        |    Future.successful(NotificationsService.register)
        |
        |  override def unregister(): Future[Unit] =
        |    Future.successful(NotificationsService.unregister)
        |}
        |
        |object NotificationsService {
        |  import Implicits.backendExecutionContext
        |
        |  private val clients = scala.collection.mutable.ArrayBuffer[ClientId]()
        |
        |  def register(implicit clientId: ClientId) = clients.synchronized {
        |    clients += clientId
        |  }
        |
        |  def unregister(implicit clientId: ClientId) = clients.synchronized {
        |    clients -= clientId
        |  }
        |
        |  backendExecutionContext.execute(new Runnable {
        |    override def run(): Unit = {
        |      while (true) {
        |        val msg = jt.LocalDateTime.now().toString
        |        clients.synchronized {
        |          clients.foreach(clientId => {
        |            // notification push
        |            ClientRPC(clientId).notificationsDemo().notify(msg)
        |          })
        |        }
        |        TimeUnit.SECONDS.sleep(1)
        |      }
        |    }
        |  })
        |}""".stripMargin
    )(GuideStyles),
    p("To start receiving messages from the server just call:"),
    CodeBlock("NotificationsClient.registerListener((msg: String) => println(msg))")(GuideStyles),
    h2("What's next?"),
    p(
      "You may find the ", a(href := RpcSerializationState.url)("Udash serialization"), " chapter interesting later on. "
    )
  )
}