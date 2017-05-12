package io.udash.web.guide.views.rpc

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.rpc.demos._
import io.udash.web.guide.{Context, _}
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object RpcClientServerViewPresenter extends DefaultViewPresenterFactory[RpcClientServerState.type](() => new RpcClientServerView)

class RpcClientServerView extends FinalView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Client ➔ server communication"),
    p(
      "Every dynamic web application needs to communicate with a server, every modern application should do it asynchronously. " +
      "In Udash all you have to do to make asynchronous server calls is:"
    ),
    ul(GuideStyles.get.defaultList)(
      li("Prepare RPC interfaces as described in the ", a(href := RpcInterfacesState.url)("RPC interfaces"), " chapter."),
      li("Implement prepared interface in your backend code."),
      li("Use ", i("DefaultServerRPC"), " in the frontend code to make a server connection.")
    ),
    h2("Server connection"),
    p("Let's start with creating a client-server connection in the client code:"),
    CodeBlock(
      """val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](
        |  new FrontendRPCService
        |)""".stripMargin
    )(GuideStyles),
    p(
      i("MainClientRPC"), " and ", i("MainServerRPC"), " are root the RPC interfaces of the application. ",
      i("FrontendRPCService"), " is a ", i("MainClientRPC"), " implementation. Ignore it for now, this topic will be covered in the ",
      a(href := RpcServerClientState.url)("Server ➔ Client communication"), " chapter."
    )(),
    p("Now you can use ", i("serverRpc"), " to make RPC calls from the client to the server application."),
    CodeBlock("""serverRpc.remoteCall("Test") onComplete { ... }""".stripMargin)(GuideStyles),
    h2("Backend endpoints implementation"),
    p("There are many ways of implementing the backend RPC interface. Below you can find description of three possible solutions:"),
    ul(GuideStyles.get.defaultList)(
      li(
        "Basic implementation - the easiest way which is useful when your service layer does not need to know anything ",
        "about a client (i.e. it does not use the ", a(href := RpcServerClientState.url)("server ➔ client communication"), ") "
      ),
      li(
        "Client-aware implementation - the most common implementation for services without authentication, the service layer ",
        "has access to the ", i("ClientId"), " and can use it in ", a(href := RpcServerClientState.url)("server ➔ client communication")
      ),
      li(
        "User-aware implementation - the most common implementation for services with authentication, the service layer ",
        "has access to ", i("ClientId"), " and ", i("UserContext")
      )
    ),
    p("Things to consider when implementing the backend RPC interface:"),
    ul(GuideStyles.get.defaultList)(
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
    p("The RPC interface implementation is very simple. Let's prepare an ", i("AtmosphereServiceConfig"), ""),
    CodeBlock(
      """class BasicAtmosphereServiceConfig[ServerRPCType]
        |  (localRpc: ExposesServerRPC[ServerRPCType])
        |  extends AtmosphereServiceConfig[ServerRPCType] {
        |
        |  override def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType] =
        |    localRpc
        |
        |  override def initRpc(resource: AtmosphereResource): Unit = {}
        |
        |  override def filters: Seq[(AtmosphereResource) => Try[Any]] = List()
        |
        |  override def onClose(resource: AtmosphereResource): Unit = {}
        |}""".stripMargin
    )(GuideStyles),
    p("Now you can use it in the following way:"),
    CodeBlock(
      """val config = new BasicAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](MainRpcEndpoint)
        |)
        |val framework = new DefaultAtmosphereFramework(config)""".stripMargin
    )(GuideStyles),
    p("This is a very simple example of backend implementation. Unfortunately, it is only sufficient for very small and simple applications."),
    h3("Client-aware implementation"),
    p(
      "Usually knowing the method caller ", i("ClientId"), " is useful, especially when you want to use the ",
      a(href := RpcServerClientState.url)("server ➔ client communication"), " for a specific client. "
    ),
    CodeBlock(
      """class MainRpcEndpoint(implicit val clientId: ClientId) extends MainServerRpc {
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
      """val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    clientId => MainRpcEndpoint()(clientId)
        |  )
        |)
        |val framework = new DefaultAtmosphereFramework(config)""".stripMargin
    )(GuideStyles),
    h4("Example"),
    p("Click the below button to get your ", i("ClientId"), ":"),
    new ClientIdDemoComponent,
    h4("Handling heavy endpoints"),
    p(
      "Sometimes your services cannot be created per client connection. For example, when initialization takes too much time ",
      "or needs to share data between the clients. In such cases, you can split your backend into a service and an endpoints layer. ",
      "The endpoint should be a lightweight implementation of the RPC interface which will be created per client and pass calls to ",
      "the service layer. "
    ),
    CodeBlock(
      """class MainRpcEndpoint
        |  (primeService: PrimeService)(implicit val clientId: ClientId)
        |  extends MainServerRpc {
        |
        |  def isPrime(n: BigInt): Future[Boolean] = {
        |    /* Here you can handle for example server ➔ client
        |       calls with clientId or authorization.
        |       You can also pass clientId to service method, if it needs it. */
        |    primeService.isPrime(n)
        |  }
        |}
        |
        |class PrimeService {
        |  private val responses = mutable.Map[BigInt, Boolean]()
        |
        |  /** Heavy init for integers up to 2^25 */
        |  for (i <- 1 to Math.pow(2, 25).toInt) {
        |    val r = checkIfPrime(n)
        |    responses(n) = r
        |    r
        |  }
        |
        |  def isPrime(n: BigInt): Future[Boolean] = {
        |    if (responses.contains(n)) Future.successful(responses(n))
        |    else Future {
        |      val r = checkIfPrime(n)
        |      responses.synchronized { responses(n) = r }
        |      r
        |    }
        |  }
        |
        |  private def checkIfPrime(n: BigInt): Boolean = ???
        |}""".stripMargin
    )(GuideStyles),
    p("In such implementation you can create a single service instance and a lightweight endpoint per client connection in the following way:"),
    CodeBlock(
      """val service = new PrimeService
        |val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    clientId => MainRpcEndpoint(service)(clientId)
        |  )
        |)
        |val framework = new DefaultAtmosphereFramework(config)""".stripMargin
    )(GuideStyles),
    h3("User-aware implementation"),
    p("More complex services might need the ", i("UserContext"), " of the method call. Look at one of possible ways to provide it:"),
    ul(GuideStyles.get.defaultList)(
      li(i("AtmosphereServiceConfig"), " will resolve the ", i("UserContext"), " based on a HttpServlet request and create the RPC endpoint with it"),
      li("The RPC endpoint will authorize method access"),
      li("The service will do the job")
    ),
    p("This time let's start from the endpoint and service implementation."),
    CodeBlock(
      """trait UserContext {
        |  def id: UserId
        |  def hasPermission(id: PermissionId): Boolean
        |}
        |
        |class MainRpcEndpoint
        |  (primeService: PrimeService)
        |  (implicit val clientId: ClientId, val user: UserContext)
        |  extends MainServerRpc {
        |
        |  val primeServicePermission: PermissionId = ???
        |
        |  def isPrime(n: BigInt): Future[Boolean] = {
        |    if (!user.hasPermission(primeServicePermission)) Future.failure(/** An unauthorized exception */)
        |    else primeService.isPrime(n)
        |  }
        |}
        |
        |class PrimeService {
        |  private val quotaService: QuotaService = ???
        |  private val responses = mutable.Map[BigInt, Boolean]()
        |
        |  def isPrime(n: BigInt)(implicit val user: UserContext): Future[Boolean] = {
        |    if (quotaService.isExceeded(user.id)) Future.failure(/** Quota exception */)
        |    else if (responses.contains(n)) Future.successful(responses(n))
        |    else Future {
        |      val r = checkIfPrime(n)
        |      responses.synchronized { responses(n) = r }
        |      r
        |    }
        |  }
        |
        |  private def checkIfPrime(n: BigInt): Boolean = ???
        |}""".stripMargin
    )(GuideStyles),
    p(
      "The above example is similar to the previous one. Now ", i("MainRpcEndpoint"), " receives ", i("UserContext"),
      " and checks if the user has permission required to call the service method. The ", i("isPrime"), " method from the ",
      i("PrimeService"), " takes ", i("UserContext"), " and passes the user ID to ", i("QuotaService"), " for a quota check. ",
      "As you can see, the endpoints are well suited to authorizing GUI users. The services are not aware of GUI permissions and ",
      "can be easily reused in other application endpoints like REST API."
    ),
    p("Now it is time to prepare ", i("AtmosphereServiceConfig"), ":"),
    CodeBlock(
      """class AuthAtmosphereServiceConfig[ServerRPCType](
        |    localRpc: (ClientId, UserContext) => DefaultExposesServerRPC[ServerRPCType],
        |    auth: AuthService
        |  ) extends AtmosphereServiceConfig[ServerRPCType] {
        |
        |  private val RPCName = "RPC"
        |  private val UserContextName = "UserContext"
        |  private val connections = new DefaultAtmosphereResourceSessionFactory
        |
        |  override def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType] =
        |    connections.getSession(resource).getAttribute(RPCName)
        |      .asInstanceOf[ExposesServerRPC[ServerRPCType]]
        |
        |  override def initRpc(resource: AtmosphereResource): Unit = synchronized {
        |    val session = connections.getSession(resource)
        |    val userContext = resolveUserContext(resource)
        |
        |    if (session.getAttribute(RPCName) == null) {
        |      val rpc = localRpc(ClientId(resource.uuid()), userContext)
        |      session.setAttribute(RPCName, rpc)
        |    }
        |  }
        |
        |  /** Ignore all unauthenticated calls */
        |  override def filters: Seq[(AtmosphereResource) => Try[Any]] =
        |    List(authenticationFilter)
        |
        |  override def onClose(resource: AtmosphereResource): Unit = {}
        |
        |  private def authenticationFilter(resource: AtmosphereResource): Try[Unit] = {
        |    val session = connections.getSession(resource)
        |    session.getAttribute(UserContextName) match {
        |      case context: UserContext if context != null => Success(())
        |      case _ => Failure(())
        |    }
        |  }
        |
        |  private def resolveUserContext(resource: AtmosphereResource): UserContext = {
        |    val session = connections.getSession(resource)
        |    session.getAttribute(UserContextName) match {
        |      case context: UserContext if context != null => context
        |      case _ =>
        |        val context = auth.authenticateRequest(resource)
        |        session.setAttribute(UserContextName, context)
        |        context
        |    }
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p(
      "This time the ", i("AtmosphereServiceConfig"), " is expected to authenticate calls before passing them to the RPC endpoints. ",
      "The ", i("UserContext"), " is cached per connection, just like the RPC endpoints in the previous examples. The ", i("resolveUserContext"), " method ",
      "uses the ", i("AuthService"), " which somehow creates ", i("UserContext"), " basing on a resource. Notice that there is one ",
      i("filter"), " method which ignores all unauthenticated calls."
    ),
    p("Now it is ready to use in the following way:"),
    CodeBlock(
      """val service = new PrimeService
        |val auth = new AuthService
        |val config = new DefaultAtmosphereServiceConfig(
        |  new DefaultExposesServerRPC[MainServerRPC](
        |    (clientId, user) => MainRpcEndpoint(service)(clientId, user)
        |  ), auth
        |)
        |val framework = new DefaultAtmosphereFramework(config)""".stripMargin
    )(GuideStyles),
    h2("Exceptions handling"),
    p(
      "The exceptions thrown by the backend application are passed to the frontend application. In a general case ",
      "they are passed as the ", i("RPCFailure"), " exception contaning basic data related to the error, but it is also ",
      "possible to serialize the original exception with assigned ", i("GenCodec"), ". "
    ),
    p(
      "First of all you have to create an instance of ", i("ExceptionCodecRegistry"), " in cross-compiled module and register ",
      "codecs of your exceptions. You can use a default implementation named ", i("DefaultExceptionCodecRegistry"), "."
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
      "Then you have to provide the registry to server connector in the frontend application and to the atmosphere service ",
      "on the server side. Now the registered exceptions will be passed from the server to the client. Take a look at ",
      "the following demo: "
    ),
    new ExceptionsDemoComponent().getTemplate,
    h2("What's next?"),
    p(
      "You may find the ", a(href := RpcServerClientState.url)("server ➔ client communication"), " chapter interesting later on. "
    )
  )
}
