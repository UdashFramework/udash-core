package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.rpc.demos._
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object RpcClientServerViewFactory extends StaticViewFactory[RpcClientServerState.type](() => new RpcClientServerView)

class RpcClientServerView extends View {
  import Context._
  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Client ➔ server communication"),
    p(
      "Every dynamic web application needs to communicate with a server, every modern application should do it asynchronously. " +
      "In Udash all you have to do to make asynchronous server calls is:"
    ),
    ul(GuideStyles.defaultList)(
      li("Prepare RPC interfaces as described in the ", a(href := RpcInterfacesState.url)("RPC interfaces"), " chapter."),
      li("Implement prepared interface in your backend code."),
      li("Use ", i("DefaultServerRPC"), " in the frontend code to make a server connection.")
    ),
    h2("Server connection"),
    p("Let's start with creating a client-server connection in the client code:"),
    CodeBlock(
      """val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](
        |  new FrontendRPCService
        |  // you can also pass a server URL, an exceptions registry or
        |  // RPC failure interceptors (global handlers of exceptions thrown by server) here
        |)""".stripMargin
    )(GuideStyles),
    p(
      i("MainClientRPC"), " and ", i("MainServerRPC"), " are the root RPC interfaces of the application. ",
      i("FrontendRPCService"), " is a ", i("MainClientRPC"), " implementation. Ignore it for now, this topic will be covered in the ",
      a(href := RpcServerClientState.url)("Server ➔ Client communication"), " chapter."
    ),
    p(
      i("DefaultServerRPC"), " is a convenient method for client-server connection creation, assuming that you want to ",
      "use the default RPC framework: ", i("DefaultServerUdashRPCFramework"), " and ", i("DefaultClientUdashRPCFramework"), ". ",
      "These frameworks use ", i("GenCodec"), " for serialization, so you have to define it for every type used ",
      "in your RPC interfaces. ", i("GenCodec"), " is already defined for basic types like Int, String, collections, etc."
    ),
    p("Now you can use ", i("serverRpc"), " to make RPC calls from the client to the server application."),
    CodeBlock("""serverRpc.remoteCall("Test") onComplete { ... }""".stripMargin)(GuideStyles),
    h2("Backend endpoints implementation"),
    p("There are many ways of implementing the backend RPC interface. Below you can find description of three possible solutions:"),
    ul(GuideStyles.defaultList)(
      li(
        "Basic implementation - the easiest way which is useful when your service layer does not need to know anything ",
        "about a client (i.e. it does not use the ", a(href := RpcServerClientState.url)("server ➔ client communication"), "). "
      ),
      li(
        "Client-aware implementation - the most common implementation for services without authentication, the service layer ",
        "has access to the ", i("ClientId"), " and can use it in ",
        a(href := RpcServerClientState.url)("server ➔ client communication"), "."
      ),
      li(
        "User-aware implementation - the most common implementation for services with authentication, the service layer ",
        "has access to ", i("ClientId"), " and ", i("UserContext"), "."
      )
    ),
    p("Things to consider when implementing the backend RPC interface:"),
    ul(GuideStyles.defaultList)(
      li("How to create and cache the RPC endpoints?"),
      li("How to handle authentication and authorization in an endpoint?"),
      li("How to pass a required data (like ", i("ClientId"), " and ", i("UserContext"), ") to the RPC endpoints?")
    ),
    p("The way the endpoints and ", i("AtmosphereServiceConfig"), " should be implemented depends on answer to these questions."),
    h3("Basic implementation"),
    p(
      "Let's assume that an interface implementation does not need to know anything about a client. In such case ",
      "every endpoint can be a Scala object, so it does not need any caching."
    ),
    CodeBlock(
      """object MainRpcEndpoint extends MainServerRpc {
        |  /** Methods implementation... */
        |}""".stripMargin
    )(GuideStyles),
    p("The RPC interface implementation is very simple. Let's prepare an ", i("AtmosphereServiceConfig"), "."),
    CodeBlock(
      """import io.udash.rpc._
        |
        |class BasicAtmosphereServiceConfig[ServerRPCType](
        |  localRpc: ExposesServerRPC[ServerRPCType]
        |) extends AtmosphereServiceConfig[ServerRPCType] {
        |
        |  override def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType] =
        |    localRpc
        |
        |  override def initRpc(resource: AtmosphereResource): Unit = {}
        |  override def filters: Seq[(AtmosphereResource) => Try[Any]] = List()
        |  override def onClose(resource: AtmosphereResource): Unit = {}
        |}""".stripMargin
    )(GuideStyles),
    p(
      i("ExposesServerRPC"), " is a wrapper for your RPC interface implementation. It defines the RPC framework ",
      "used by your application. The default implementation (", i("DefaultExposesServerRPC"), ") uses ",
      i("DefaultServerUdashRPCFramework"), " to expose your interface."
    ),
    p("Now you can use it in the following way:"),
    CodeBlock(
      """import io.udash.rpc._
        |
        |val config = new BasicAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](MainRpcEndpoint)
        |)
        |val framework = new DefaultAtmosphereFramework(config)
        |// register this servlet in your servlets container
        |new RpcServlet(framework) """.stripMargin
    )(GuideStyles),
    p(
      i("DefaultAtmosphereFramework"), " is a wrapper for the ", i("AtmosphereFramework"), " class with some ",
      "default configuration. It is responsible for the WebSocket communication with the client application. "
    ),
    p("This is a very simple example of backend implementation. Unfortunately, it is only sufficient for very small and simple applications."),
    h3("Client-aware implementation"),
    p(
      "Usually knowing the method caller ", i("ClientId"), " is useful, especially when you want to use the ",
      a(href := RpcServerClientState.url)("server ➔ client communication"), " for a specific client. "
    ),
    CodeBlock(
      """import io.udash.rpc._
        |
        |class MainRpcEndpoint(implicit val clientId: ClientId)
        |  extends MainServerRpc {
        |  /** Methods implementation... */
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Now the RPC interface is implemented as a class, with the constructor taking a ", i("ClientId"), ". This is an implicit argument ",
      " in order to make passing the client id to nested interfaces implementations easier. Now the ", i("AtmosphereServiceConfig"),
      " implementation will be more complicated."
    ),
    CodeBlock(
      """class DefaultAtmosphereServiceConfig[ServerRPCType]
        |  (localRpc: (ClientId) => ExposesServerRPC[ServerRPCType])
        |  extends AtmosphereServiceConfig[ServerRPCType] {
        |
        |  private val RPCName = "RPC"
        |  private val connections = new DefaultAtmosphereResourceSessionFactory
        |
        |  override def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType] =
        |    connections.getSession(resource).getAttribute(RPCName)
        |      .asInstanceOf[ExposesServerRPC[ServerRPCType]]
        |
        |  override def initRpc(resource: AtmosphereResource): Unit = synchronized {
        |    val session = connections.getSession(resource)
        |
        |    if (session.getAttribute(RPCName) == null) {
        |      session.setAttribute(RPCName, localRpc(ClientId(resource.uuid())))
        |    }
        |  }
        |
        |  override def filters: Seq[(AtmosphereResource) => Try[Any]] = List()
        |
        |  override def onClose(resource: AtmosphereResource): Unit = {}
        |}""".stripMargin
    )(GuideStyles),
    p(
      "This is the default ", i("AtmosphereServiceConfig"), " implementation from Udash. It creates a new RPC endpoint for each ",
      "connection and stores it in the session attribute. Usage is as simple as earlier:"
    ),
    CodeBlock(
      """import io.udash.rpc._
        |
        |val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    clientId => MainRpcEndpoint()(clientId)
        |  )
        |)
        |val framework = new DefaultAtmosphereFramework(config)
        |// register this servlet in your servlets container
        |new RpcServlet(framework) """.stripMargin
    )(GuideStyles),
    h4("Example"),
    p("Click the below button to get your ", i("ClientId"), ":"),
    ForceBootstrap(new ClientIdDemoComponent),
    h4("Handling heavy endpoints"),
    p(
      "Sometimes your services cannot be created per client connection. For example, when initialization takes too much time ",
      "or needs to share data between the clients. In such cases, you can split your backend into a service and an endpoints layer. ",
      "The endpoint should be a lightweight implementation of the RPC interface which will be created per client and pass calls to ",
      "the service layer. "
    ),
    CodeBlock(
      """import io.udash.rpc._
        |import scala.collection.mutable
        |
        |class MainRpcEndpoint(
        |  primeService: PrimeService
        |)(implicit val clientId: ClientId) extends MainServerRpc {
        |
        |  def isPrime(n: Long): Future[Boolean] = {
        |    /* Here you can handle for example server ➔ client
        |       calls with clientId or authorization.
        |       You can also pass clientId to service method, if it needs it. */
        |    primeService.isPrime(n)
        |  }
        |}
        |
        |class PrimeService {
        |  private val responses = mutable.Map[Long, Boolean]()
        |
        |  /** Heavy init for integers up to 2^25 */
        |  for (i <- 1 to Math.pow(2, 25).toInt) {
        |    val r = checkIfPrime(n)
        |    responses(n) = r
        |    r
        |  }
        |
        |  def isPrime(n: Long): Future[Boolean] = {
        |    if (responses.contains(n)) Future.successful(responses(n))
        |    else Future {
        |      val r = checkIfPrime(n)
        |      responses.synchronized { responses(n) = r }
        |      r
        |    }
        |  }
        |
        |  private def checkIfPrime(n: Long): Boolean = ???
        |}""".stripMargin
    )(GuideStyles),
    p("In such implementation you can create a single service instance and a lightweight endpoint per client connection in the following way:"),
    CodeBlock(
      """val service = new PrimeService
        |val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    clientId => new MainRpcEndpoint(service)(clientId)
        |  )
        |)
        |val framework = new DefaultAtmosphereFramework(config)
        |// register this servlet in your servlets container
        |new RpcServlet(framework)""".stripMargin
    )(GuideStyles),
    h3("User-aware implementation"),
    p("More complex services might need the ", i("UserContext"), " of the method call. Look at one of possible ways to provide it:"),
    ul(GuideStyles.defaultList)(
      li(i("MainServerRpc"), " will expose two subinterfaces: ", i("PrimeRPC"), " and ", i("AuthRPC"), "."),
      li(i("AuthRPC"), " will resolve the ", i("UserToken"), " based on a username and a password."),
      li(i("MainServerRpc"), " will resolve the ", i("UserContext"), " based on a ", i("UserToken"), "."),
      li("The RPC endpoint will authorize method access for the provided user."),
      li("The service will do the job.")
    ),
    p("Let's declare ", i("UserContext"), " and ", i("UserToken"), " first."),
    CodeBlock(
      """trait UserContext {
        |  def id: UserId
        |  def hasPermission(id: PermissionId): Boolean
        |}
        |
        |case class UserToken(id: String)
        |// UserToken has to be serializable
        |object UserToken extends HasGenCodec[UserToken]""".stripMargin
    )(GuideStyles),
    p("Now we need our interfaces hierarchy:"),
    CodeBlock(
      """trait MainServerRpc {
        |  def auth: AuthRPC
        |  def primes(token: UserToken): PrimeRPC
        |}
        |
        |trait AuthRPC {
        |  def login(username: String, password: String): Future[UserToken]
        |}
        |
        |trait PrimeRPC {
        |  def isPrime(n: Long): Future[Boolean]
        |}
        |
        |// RPCCompanions skipped""".stripMargin
    )(GuideStyles),
    p("Take a look at the endpoints and services implementation."),
    CodeBlock(
      """class MainRpcEndpoint(primeService: PrimeService, authService: AuthService)
        |  extends MainServerRpc {
        |
        |  def auth: AuthRPC = new AuthEndpoint(authService)
        |  def primes(token: UserToken): PrimeRPC = {
        |    authService.loadContext(token).map { ctx =>
        |      new PrimeEndpoint(primeService, ctx)
        |    }.getOrElse(throw new InvalidTokenException)
        |  }
        |}
        |
        |class AuthEndpoint(authService: AuthService) extends AuthRPC {
        |  def login(username: String, password: String): Future[UserToken] =
        |    authService.login(username, password)
        |}
        |
        |class AuthService {
        |  def login(username: String, password: String): Future[UserToken] = ???
        |  def loadContext(token: UserToken): Future[UserContext] = ???
        |}
        |
        |class PrimeEndpoint(primeService: PrimeService, ctx: UserContext)
        |  extends PrimeRPC {
        |  private val primeServicePermission: PermissionId = ???
        |
        |  def isPrime(n: Long): Future[Boolean] = {
        |    if (!ctx.hasPermission(primeServicePermission)) {
        |      Future.failure(new UnauthorizedException)
        |    } else primeService.isPrime(n)
        |  }
        |}
        |
        |class PrimeService {
        |  private val quotaService: QuotaService = ???
        |  private val responses = mutable.Map[Long, Boolean]()
        |
        |  def isPrime(n: Long)(implicit val user: UserContext): Future[Boolean] = {
        |    if (quotaService.isExceeded(user.id))
        |      Future.failure(new QuotaException)
        |    else if (responses.contains(n))
        |      Future.successful(responses(n))
        |    else Future {
        |      val r = checkIfPrime(n)
        |      responses.synchronized { responses(n) = r }
        |      r
        |    }
        |  }
        |
        |  private def checkIfPrime(n: Long): Boolean = ???
        |}""".stripMargin
    )(GuideStyles),
    p(
      "The interfaces hierarchy is a convenient way to handle authentication. ", i("MainRpcEndpoint"), " exposes two subinterfaces. ",
      "The first one provides a method for user authentication. The second one verifies the client token. ",
      "It is not possible to access the ", i("PrimeService"), " without a valid ", i("UserToken"), ". The ",
      i("UserContext"), " checks if the user has permission required to call the service method. The ",
      i("isPrime"), " method from the ", i("PrimeService"), " takes a ", i("UserContext"), " and passes the user ID to the ", i("QuotaService"),
      " for a quota check. As you can see, the endpoints are well suited for authorizing GUI users. ",
      "The services are not aware of GUI permissions and can be easily reused in other application endpoints like REST API."
    ),
    p("Now it is ready to use in the following way:"),
    CodeBlock(
      """val primes = new PrimeService
        |val auth = new AuthService
        |val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    _ => new MainRpcEndpoint(primes, auth)
        |  )
        |)
        |val framework = new DefaultAtmosphereFramework(config)
        |// register this servlet in your servlets container
        |new RpcServlet(framework)""".stripMargin
    )(GuideStyles),
    h2("Exceptions handling"),
    p(
      "The exceptions thrown by the backend application are passed to the frontend application. In a general case ",
      "they are passed as the ", i("RPCFailure"), " exception contaning basic data related to the error, but it is also ",
      "possible to serialize the original exception with assigned ", i("GenCodec"), ". "
    ),
    p(
      "First of all you have to create an instance of the ", i("ExceptionCodecRegistry"), " in the cross-compiled module and register ",
      "the codecs for your exceptions. You can also use a default implementation named ", i("DefaultExceptionCodecRegistry"),
      " - it performs serialization of basic exceptions like ", i("NullPointerException"), "."
    ),
    CodeBlock(
      """import io.udash.rpc.serialization.ExceptionCodecRegistry
        |import io.udash.rpc.serialization.DefaultExceptionCodecRegistry
        |
        |object SharedExceptions {
        |  case class ExampleException(msg: String) extends Exception(msg)
        |
        |  val registry: ExceptionCodecRegistry = {
        |    val registry = new DefaultExceptionCodecRegistry
        |    registry.register(GenCodec.materialize[ExampleException])
        |    registry
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Then you have to pass the registry to the server connector (usually: ", i("DefaultServerRPC"),
      ") in the frontend application and to the Atmosphere service (usually: ", i("DefaultAtmosphereFramework"),
      ") on the server side. Now the registered exceptions will be passed from the server to the client. Take a look at ",
      "the following demo: "
    ),
    ForceBootstrap(new ExceptionsDemoComponent().getTemplate),
    p(
      "In some cases you may want to handle exceptions globally. ", i("DefaultServerRPC"), " constructor takes ",
      i("rpcFailureInterceptors"), " argument and every passed callback will be executed on all RPC call failures. ",
      "You can also register a callback in the ", i("DefaultServerRPC"), " instance with ",
      i("registerCallFailureCallback"), "."
    ),
    h2("What's next?"),
    p(
      "You may find the ", a(href := RpcServerClientState.url)("server ➔ client communication"),
      " chapter interesting later on. "
    )
  )
}
