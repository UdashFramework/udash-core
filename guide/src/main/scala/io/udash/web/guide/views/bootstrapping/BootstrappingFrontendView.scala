package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.GlobalStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import org.scalajs.dom

import scalatags.JsDom

case object BootstrappingFrontendViewPresenter extends DefaultViewPresenterFactory[BootstrappingFrontendState.type](() => new BootstrappingFrontendView)

class BootstrappingFrontendView extends View {
  import Context._

  import JsDom.all._
  import scalacss.Defaults._
  import scalacss.ScalatagsCss._

  override def getTemplate: dom.Element = div(
    h2("Frontend application structure"),
    p("The frontend application consists of:"),
    ul(GuideStyles.defaultList)(
      li("Routing system - bidirectional mapping between URLs and states"),
      li(
        span("ViewPresenters - a logical pairing between a view and a presenter"),
        ul(GuideStyles.innerList)(
          li("Mapping from states to ViewPresenters"),
          li("Views & Presenters")
      )),
      li("Client RPC")
    ),
    h3("States"),
    p(
      "A Udash application is based on states. The application state determines the created ViewPresenters structure and is determined " +
      "by a URL. The application states structure is your decision, Udash requires only that all states must extend ",
      i("State"), ". States usually will create a nested hierarchy. This hierarchy describes nesting of views. " +
      "For example:"
    ),
    CodeBlock(
      """sealed abstract class RoutingState(val parentState: RoutingState) extends State
        |
        |case object RootState extends RoutingState(null)
        |
        |case object LandingPageState extends RoutingState(RootState)
        |
        |case object NewsletterState extends RoutingState(RootState)
        |
        |case object SubscribeState extends RoutingState(NewsletterState)
        |
        |case object UnsubscribeState extends RoutingState(NewsletterState)""".stripMargin
    )(GuideStyles),
    BootstrappingImage("states.png", "Example of application states.", GuideStyles.imgMedium, GlobalStyles.noMargin),
    h3("Routing system"),
    p(
      "The routing system reacts on URL changes and updates the application state. It requires only mappings from the URL to the state " +
      "and back from the state to the URL."
    ),
    p("Take a look at example ", i("RoutingRegistry"), " implementation:"),
    CodeBlock(
      """class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
        |  def matchUrl(url: Url): RoutingState =
        |    url2State.applyOrElse(
        |      url.value.stripSuffix("/"),
        |      (x: String) => LandingPageState
        |    )
        |
        |  def matchState(state: RoutingState): Url =
        |    Url(state2Url.apply(state))
        |
        |  private val (url2State, state2Url) = Bidirectional[String, RoutingState] {
        |    case "/" => LandingPageState
        |    case "/newsletter" => SubscribeState
        |    case "/newsletter/unsubscribe" => UnsubscribeState
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p(
      i("Bidirectional"), " returns tuple ",
      i("(PartialFunction[String, RoutingState], PartialFunction[RoutingState, String])"),
      ". It is useful, when given mapping is an one to one relation."
    ),
    h3("View, Presenter & ViewPresenter"),
    p(
      "ViewPersenter is a pair of View and Presenter. ", i("ViewPresenterRegistry"), " is responsible " +
      "for matching a current application state to ViewPresenter. Below you can find an example implementation of ",
      i("ViewPresenterRegistry"), ""
    ),
    CodeBlock("""class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
                |  def matchStateToResolver(state: RoutingState): ViewPresenter[_ <: RoutingState] =
                |    state match {
                |      case RootState => RootViewPresenter
                |      case LandingPageState => LandingPageViewPresenter
                |      case NewsletterState => NewsletterViewPresenter
                |      case SubscribeState => NewsletterSubscribeViewPresenter
                |      case UnsubscribeState => NewsletterUnsubscribeViewPresenter
                |      case _ => ErrorViewPresenter
                |    }
                |}""".stripMargin
    )(GuideStyles),
    p("Each ViewPresenter is expected to initialize a View and a Presenter. At this point you can create the shared model for them."),
    CodeBlock(
      """trait SubscribeModel {
        |  def email: String
        |}
        |
        |case object NewsletterSubscribeViewPresenter
        |  extends ViewPresenter[SubscribeState.type] {
        |
        |  import scalajs.concurrent.JSExecutionContext.Implicits.queue
        |
        |  override def create(): (View, Presenter[SubscribeState.type]) = {
        |    val model = ModelProperty[SubscribeModel]
        |    val presenter = new NewsletterSubscribePresenter(model)
        |    val view = new NewsletterSubscribeView(model, presenter)
        |
        |    (view, presenter)
        |  }
        |}
        |
        |class NewsletterSubscribePresenter(model: ModelProperty[SubscribeModel])
        |  extends Presenter[SubscribeState.type] {
        |
        |  import scalajs.concurrent.JSExecutionContext.Implicits.queue
        |
        |  /** Called before view starts rendering. */
        |  override def handleState(state: SubscribeState.type): Unit = {
        |    // Clear email
        |    model.subProp(_.email).set("")
        |  }
        |
        |  def subscribe(): Future[Boolean] = Future {
        |    // Send RPC request to server
        |  }
        |}
        |
        |class NewsletterSubscribeView(model: ModelProperty[SubscribeModel],
        |                              presenter: NewsletterSubscribePresenter)
        |  extends View {
        |  import scalatags.JsDom.all._
        |
        |  /** Renders view HTML code */
        |  override def getTemplate: Element = div(
        |    // automatic two way binding with html input
        |    TextInput(model.subProp(_.email)),
        |    // :+= operator allows to add more than one callback for one event
        |    button(onclick :+= ((_: Event) => {
        |      presenter.subscribe()
        |      true // prevent default
        |    }))("Subscribe")
        |  ).render
        |
        |  /** Called if there is some child view.
        |    * This method should place view template inside DOM. */
        |  override def renderChild(view: View): Unit = ()
        |}""".stripMargin
    )(GuideStyles),
    p(
      "The above example shows simple View, Presenter and ViewPresenter implementations. ",
      ul(GuideStyles.defaultList)(
        li(
          i("SubscribeModel"), " is a model trait witch is used to create shared ", i("ModelProperty"), " ",
          a(href := FrontendPropertiesState.url)("Properties in Udash"), " are described in other part of this guide. "
        ),
        li(
          i("NewsletterSubscribeViewPresenter"), " creates a model, view, presenter and connects them together "
        ),
        li(
          i("NewsletterSubscribePresenter"), " initializes an email in the model and exposes the ", i("subscribe"), " method. "
        ),
        li(
          i("NewsletterSubscribeView"), " creates an input and button template using the ",
          a(href := References.scalatagsHomepage)("Scalatags"), " project with some Udash extensions."
        ),
        li("View ignores child views, because it is the final view.")
      )
    ),
    p("If the view does not need a presenter, you can use ", i("DefaultViewPresenterFactory"), ""),
    CodeBlock(
      """case object NewsletterViewPresenter
        |  extends DefaultViewPresenterFactory[NewsletterState.type](() => new NewsletterView)
        |
        |class NewsletterView extends View {
        |  import Context._
        |  import JsDom.all._
        |
        |  private val child = div().render // Placeholder for child view
        |
        |  override def getTemplate: dom.Element = div(
        |    h1("Newsletter"),
        |    p("Subscribe for news..."),
        |    child
        |  ).render
        |
        |  /** Puts child view inside itself using jQuery. */
        |  override def renderChild(view: View): Unit = {
        |    import io.udash.wrappers.jquery.jQ
        |    jQ(child).html(view.getTemplate)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    h3("Udash Application"),
    p(
      "Everything is ready to create ", i("Application"), ". It can be done in some object, " +
      "which will be useful to handle server RPC connector later. "
    ),
    CodeBlock(
      """object Context {
        |  import scalajs.concurrent.JSExecutionContext
        |
        |  private implicit val executionContext = JSExecutionContext.Implicits.queue
        |  private lazy val routingRegistry = new RoutingRegistryDef
        |  private lazy val viewPresenterRegistry = new StatesToViewPresenterDef
        |
        |  implicit val applicationInstance = new Application[RoutingState](
        |    routingRegistry, viewPresenterRegistry, RootState
        |  )
        |}""".stripMargin
    )(GuideStyles),
    p("ScalaJS needs the main function that will initialize the whole application. For example: "),
    CodeBlock(
      """object Init extends JSApp {
        |  @JSExport
        |  override def main(): Unit = {
        |    jQ(document).ready((jThis: Element) => {
        |      // Select #application element from index.html as root of whole app
        |      val appRoot = jQ("#application").get(0).get
        |      Context.applicationInstance.run(appRoot)
        |    })
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("The application should compile and it is ready to start."),
    h3("Frontend RPC"),
    p("The last thing that should be bootstrapped is the client RPC implementation."),
    p("First of all, the application requires implementing ", i("MainClientRPC"), ". It will be a simple class extending the RPC interface:"),
    CodeBlock(
      """class RPCService extends MainClientRPC {
        |  override def pong(id: Int): Unit =
        |    println(s"pong $id")
        |}""".stripMargin
    )(GuideStyles),
    p("Now, in the Context object, create ", i("DefaultServerRPC"), ":"),
    CodeBlock("""val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService)""".stripMargin)(GuideStyles),
    p("You can use serverRpc like that:"),
    CodeBlock("""serverRpc.hello("World") onComplete {
                |  case Success(r) => println(r)
                |  case Failure(ex) => println(ex)
                |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Now all parts of the Udash application are ready. You can find a complete demo application in ",
      a(href := BootstrappingGeneratorsState.url)("Udash generators"), ""
    ),

    p(
      "You can also learn more about ",
      a(href := FrontendIntroState.url)("Frontend application development"),
      " or ",
      a(href := RpcIntroState.url)("RPC in Udash"), ""
    )
  ).render

  override def renderChild(view: View): Unit = ()
}