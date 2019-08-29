package io.udash.web.guide.views.ext

import io.udash._
import io.udash.logging.CrossLogging
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide._
import io.udash.web.guide.demos.activity.{Call, CallServerRPC}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.{RpcLoggingDemo, UrlLoggingDemo}
import io.udash.web.guide.views.rpc.demos.PingPongCallDemoComponent
import scalatags.JsDom

import scala.util.{Failure, Success}

class UserActivityExtPresenter(model: SeqProperty[Call]) extends Presenter[UserActivityExtState.type] with CrossLogging {

  import io.udash.web.guide.Context._

  val rpc: CallServerRPC = Context.serverRpc.demos.call

  override def handleState(state: UserActivityExtState.type): Unit = ()

  def reload(): Unit = {
    rpc.calls.onComplete {
      case Success(calls) => model.set(calls)
      case Failure(t) => logger.error(t.getMessage)
    }
  }
}


case object UserActivityExtViewFactory extends ViewFactory[UserActivityExtState.type] {

  override def create(): (View, Presenter[UserActivityExtState.type]) = {
    val model = SeqProperty.blank[Call]
    val presenter = new UserActivityExtPresenter(model)
    (new UserActivityExtView(model, presenter), presenter)
  }
}

class UserActivityExtView(model: SeqProperty[Call], presenter: UserActivityExtPresenter) extends FinalView {
  import Context._
  import JsDom.all._

  val (urlLoggingDemo, urlLoggingSnippet) = UrlLoggingDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h1("Udash user activity monitoring"),
    p(
      """When it comes to website tracking there are a plethora of metrics at our disposal.
        |If we’re talking user engagement, we might look at the bounce rate, average time on site, or average page views per visit.
        |For organic search, we might look at the number of organic visits, top organic keywords, and others.
        |Then there’s content — the king of all data. Which web pages are the most popular?
        |What are the most common navigation flows? Which features are most commonly used?
        |Udash user activity extenstions enable you to gather the data you need to provide the best user experience for your website.""".stripMargin
    ),
    h2("Browser navigation"),
    p("To enable browser navigation tracking, simply mixin UrlLogging into your frontend application. ",
      "The ", i("log(url, referrer)"), " method will be called whenever the user changes app state."
    ),
    p("You can see this mechanism in action here in the guide. We've already provided the implementation: "),
    CodeBlock(
      s"""val application = new Application[RoutingState](
          |  routingRegistry, viewFactoryRegistry, RootState
          |) with UrlLogging[RoutingState] {
          |  override protected def log(url: String, referrer: Option[String]): Unit =
          |    UrlLoggingDemo.log(url, referrer)
          |}""".stripMargin
    )(GuideStyles),
    urlLoggingSnippet,
    p("to see it in action just enable logging below, switch to another chapter and come back here."), br,
    ForceBootstrap(urlLoggingDemo),
    h2("RPC call logging"),
    p("Enabling backend call logging is also quite simple. In order to define logging behaviour, you have to mix ",
      i("CallLogging"), " into your ", i("ExposesServerRPC"), ", e.g.: "),
    CodeBlock(
      """new DefaultExposesServerRPC[MainServerRPC](
        |  new ExposedRpcInterfaces(clientId)
        |) with CallLogging[MainServerRPC] {
        |  override protected val metadata: RPCMetadata[MainServerRPC] =
        |    MainServerRPC.metadata
        |
        |  override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
        |    println(s"$rpcName $methodName $args")
        |} """.stripMargin)(GuideStyles),
    p("The methods you want log calls on have to be annotated with ", i("@Logged"), ". For this example we reused the ping example from RPC guide introduction: "),
    CodeBlock(
      """import io.udash.rpc._
        |import io.udash.rpc.utils.Logged
        |
        |@RPC
        |trait PingPongServerRPC {
        |  @Logged
        |  def fPing(id: Int): Future[Int]
        |}""".stripMargin)(GuideStyles),
    ForceBootstrap(
      new PingPongCallDemoComponent,
      RpcLoggingDemo(model, () => presenter.reload())
    ),
    h2("What's next?"),
    p(
      "Take a look at another extensions like ", a(href := BootstrapExtState.url)("Bootstrap Components"), " or ",
      a(href := AuthorizationExtState.url)("Authorization utilities"), "."
    )
  )
}