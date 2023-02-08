package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.css.CssView
import io.udash.properties.ModelPropertyCreator
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.views.{ClickableImageFactory, ImageFactoryPrefixSet}
import io.udash.web.guide._
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import scalatags.JsDom.Modifier

case object BootstrappingFrontendViewFactory extends StaticViewFactory[BootstrappingFrontendState.type](() => new BootstrappingFrontendView)

class BootstrappingFrontendView extends View with CssView {

  import com.avsystem.commons.SharedExtensions.universalOps

  private val statesSource = {
    import io.udash._

    sealed abstract class RoutingState(
      val parentState: Option[RoutingState]
    ) extends State {
      override type HierarchyRoot = RoutingState
    }

    case object RootState extends RoutingState(None)
    case object LandingPageState extends RoutingState(Some(RootState))
    case object NewsletterState extends RoutingState(Some(RootState))
    case object SubscribeState extends RoutingState(Some(NewsletterState))
    case object UnsubscribeState extends RoutingState(Some(NewsletterState))
  }.sourceCode

  private val (registrySource, factorySource) = {
    sealed abstract class RoutingState(
      val parentState: Option[RoutingState]
    ) extends State {
      override type HierarchyRoot = RoutingState
    }

    case object RootState extends RoutingState(None)
    case object LandingPageState extends RoutingState(Some(RootState))
    case object NewsletterState extends RoutingState(Some(RootState))
    case object SubscribeState extends RoutingState(Some(NewsletterState))
    case object UnsubscribeState extends RoutingState(Some(NewsletterState))

    val registrySource = {
      import io.udash._

      class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
        def matchUrl(url: Url): RoutingState =
          url2State.applyOrElse(
            url.value.stripSuffix("/"),
            (x: String) => LandingPageState
          )

        def matchState(state: RoutingState): Url =
          Url(state2Url.apply(state))

        private val (url2State, state2Url) = bidirectional {
          case "/" => LandingPageState
          case "/newsletter" => SubscribeState
          case "/newsletter/unsubscribe" => UnsubscribeState
        }
      }
    }.sourceCode

    val RootViewFactory, LandingPageViewFactory, NewsletterViewFactory, NewsletterSubscribeViewFactory, NewsletterUnsubscribeViewFactory, ErrorViewFactory = null

    val factorySource = {
      import io.udash._

      class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
        def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] =
          state match {
            case RootState => RootViewFactory
            case LandingPageState => LandingPageViewFactory
            case NewsletterState => NewsletterViewFactory
            case SubscribeState => NewsletterSubscribeViewFactory
            case UnsubscribeState => NewsletterUnsubscribeViewFactory
            case _ => ErrorViewFactory
          }
      }
    }.sourceCode

    (registrySource, factorySource)
  }

  private val subscribeSource = {
    case object SubscribeState extends State {
      override type HierarchyRoot = this.type
      override def parentState: Option[SubscribeState.type] = None
    }

    {
      import io.udash._
      import org.scalajs.dom.Event
      import scala.concurrent.Future

      case class SubscribeModel(email: String)
      // HasModelPropertyCreator indicates that
      // you can create ModelProperty for SubscribeModel
      object SubscribeModel {
        implicit val mpc: ModelPropertyCreator[SubscribeModel] = ModelPropertyCreator.materialize
      }

      case object NewsletterSubscribeViewFactory
        extends ViewFactory[SubscribeState.type] {

        override def create(): (View, Presenter[SubscribeState.type]) = {
          val model = ModelProperty(new SubscribeModel(""))
          val presenter = new NewsletterSubscribePresenter(model)
          val view = new NewsletterSubscribeView(model, presenter)

          (view, presenter)
        }
      }

      class NewsletterSubscribePresenter(model: ModelProperty[SubscribeModel])
        extends Presenter[SubscribeState.type] {

        /** Called before view starts rendering. */
        override def handleState(state: SubscribeState.type): Unit = {
          model.subProp(_.email).set("") // Clear email
        }

        // Send RPC request to server
        def subscribe(): Future[Boolean] = ???
      }

      class NewsletterSubscribeView(
        model: ModelProperty[SubscribeModel],
        presenter: NewsletterSubscribePresenter
      ) extends View {

        import scalatags.JsDom.all._

        /** Renders view HTML code */
        override def getTemplate: Modifier = div(
          // automatic two way binding with html input
          TextInput(model.subProp(_.email))().render,
          // :+= operator allows to add more than one callback for one event
          button(onclick :+= ((_: Event) => {
            presenter.subscribe()
            true // prevent default
          }))("Subscribe")
        )
      }
    }.sourceCode
  }

  override def getTemplate: Modifier = {
    import Context._
    import scalatags.JsDom.all._

    div(
      h2("Frontend application structure"),
      p("The frontend application consists of:"),
      ul(GuideStyles.defaultList)(
        li("Routing system - bidirectional mapping between URLs and states."),
        li(
          span("ViewFactories - a logical pairing between a view and a presenter:"),
          ul(GuideStyles.innerList)(
            li("Mapping from states to ViewFactories,"),
            li("Views & Presenters.")
          )),
        li("Client RPC.")
      ),
      h3("States"),
      p(
        "A Udash application is based on states. The application state determines the created ViewFactories structure and is determined ",
        "by a URL. The application states structure is your decision, Udash requires only that all states must extend ",
        i("State"), ". States usually will create a nested hierarchy. This hierarchy describes nesting of views. ",
        "A state can contain other ", i("State"), "s. ",
        "For example:"
      ),
      AutoDemo.snippet(statesSource),
      ClickableImageFactory(ImageFactoryPrefixSet.Boostrapping, "states.png", "Example of application states.", GuideStyles.imgMedium, GlobalStyles.noMargin),
      h3("Routing system"),
      p(
        "The routing system reacts on URL changes and updates the application state. It requires only mappings from the URL to the state " +
          "and back from the state to the URL."
      ),
      p("Take a look at example ", i("RoutingRegistry"), " implementation:"),
      AutoDemo.snippet(registrySource),
      p(
        i("Bidirectional"), " returns tuple ",
        i("(PartialFunction[String, RoutingState], PartialFunction[RoutingState, String])"),
        ". It is useful, when given mapping is an one to one relation."
      ),
      h3("View, Presenter & ViewFactory"),
      p(
        "ViewFactory creates a pair of View and Presenter. ", i("ViewFactoryRegistry"), " is responsible " +
          "for matching a current application state to ViewFactory. Below you can find an example implementation of ",
        i("ViewFactoryRegistry"), "."
      ),
      AutoDemo.snippet(factorySource),
      p(
        "Each ViewFactory is expected to initialize a View and a Presenter. At this point you can ",
        "create the shared model for them. Take a look at following view implementation."
      ),
      AutoDemo.snippet(subscribeSource),
      p(
        "The above example shows simple View, Presenter and ViewFactory implementations. ",
        ul(GuideStyles.defaultList)(
          li(
            i("SubscribeModel"), " is a model trait which is used to create shared ", i("ModelProperty"), ". ",
            a(href := FrontendPropertiesState.url)("Properties in Udash"), " are described in other part of this guide. "
          ),
          li(
            i("NewsletterSubscribeViewFactory"), " creates a model, view, presenter and connects them together."
          ),
          li(
            i("NewsletterSubscribePresenter"), " initializes an email in the model and exposes the ", i("subscribe"), " method. "
          ),
          li(
            i("NewsletterSubscribeView"), " creates an input and button template using the ",
            a(href := References.ScalatagsHomepage, target := "_blank")("Scalatags"), " project with some Udash extensions."
          ),
          li("View ignores child views, because it is the final view.")
        )
      ),
      p("If the view does not need a presenter, you can use ", i("StaticViewFactory"), "."),
      CodeBlock(
        """import io.udash._
          |
          |case object NewsletterViewFactory
          |  extends StaticViewFactory[NewsletterState.type](() => new NewsletterView)
          |
          |class NewsletterView extends ContainerView {
          |  import scalatags.JsDom.all._
          |
          |  // ContainerView contains default implementation of child view rendering
          |  // It puts child view into `childViewContainer`
          |  override def getTemplate: Modifier = div(
          |    h1("Newsletter"),
          |    p("Subscribe for news..."),
          |    childViewContainer
          |  )
          |}""".stripMargin
      )(GuideStyles),
      p("Now you should implement the rest of ", i("ViewFactories"), " from ", i("StatesToViewFactoryDef"), " class."),
      h3("Udash Application"),
      p(
        "Everything is ready to create ", i("Application"), ". It can be done in some object, " +
          "which will be useful to handle server RPC connector later. "
      ),
      CodeBlock(
        """import io.udash._
          |
          |object Context {
          |  private val routingRegistry = new RoutingRegistryDef
          |  private val viewFactoryRegistry = new StatesToViewFactoryDef
          |
          |  val applicationInstance = new Application[RoutingState](
          |    routingRegistry, viewFactoryRegistry
          |  )
          |}""".stripMargin
      )(GuideStyles),
      p("Scala.js needs the main function that will initialize the whole application. For example: "),
      CodeBlock(
        """import scala.scalajs.js.annotation.JSExport
          |
          |object JSLauncher extends CrossLogging {
          |  @JSExport
          |  def main(args: Array[String]): Unit = {
          |    Context.applicationInstance.run("#application")
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
      CodeBlock(
        """import io.udash.rpc._
          |
          |val serverRpc: MainServerRPC = DefaultServerRPC[MainClientRPC, MainServerRPC](
          |  new RPCService
          |  // You can also provide custom ExceptionCodecRegistry
          |  // and  RPC failure interceptors.
          |  //exceptionsRegistry = ???
          |  //rpcFailureInterceptors = ???
          |)""".stripMargin
      )(GuideStyles),
      p("You can use serverRpc like that:"),
      CodeBlock(
        """serverRpc.hello("World") onComplete {
          |  case Success(r) => println(r)
          |  case Failure(ex) => println(ex)
          |}""".stripMargin
      )(GuideStyles),
      h2("What's next?"),
      p(
        "Now all parts of the Udash application are ready. You can find a complete demo application in the ",
        a(href := IntroState.url)("Udash applications generator"), "."
      ),

      p(
        "You can also learn more about ",
        a(href := FrontendIntroState.url)("Frontend application development"),
        " or ",
        a(href := RpcIntroState.url)("RPC in Udash"), "."
      )
    )
  }
}
