package io.udash.web.guide.views.rest

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, _}

import scalatags.JsDom


case object RestInterfacesViewFactory extends StaticViewFactory[RestInterfacesState.type](() => new RestInterfacesView)

class RestInterfacesView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Interfaces"),
    p(
      "Interfaces are the most important part of the Udash REST support. They make usage of existing APIs clean and maintainable. ",
      "Methods in REST interfaces can be divided into two groups:"
    ),
    ul(GuideStyles.defaultList)(
      li(i("Calls"), " - methods returning ", i("Future[T]"), " where ", i("T"), " is a serializable type."),
      li(i("Getters"), " - methods returning another REST interface, calling this method does not send anything over the network.")
    ),
    p("This is similar to the ", a(href := RpcInterfacesState.url)("RPC interface"), " types."),
    p("Take a look at the example from the previous chapter:"),
    simpleExample(),
    p(
      "Every part of the REST API is described by a trait annotated with ", i("@REST"), ". The method ",
      i("simple()"), " from the ", i("MainServerREST"), " interface returns ", i("SimpleServerREST"), ". It contains three methods ",
      "annotated with the ", i("@GET"), " annotation - this means that this method call will be mapped to HTTP request using the ",
      i("GET"), " method."
    ),
    p(
      "There are three REST annotations groups:"
    ),
    ul(GuideStyles.defaultList)(
      li(i("HTTP request methods"), " - these determine the HTTP method used for this method request."),
      li(i("Names"), " - these describe how to map methods into request URLs."),
      li(i("Argument types"), " - these describe how to map the method call argument into HTTP request data.")
    ),
    h3("HTTP request methods"),
    p("Every method making server calls should be annotated with an annotation determining used HTTP method. Take a look at the following example:"),
    CodeBlock(
      """import io.udash.rest._
        |
        |@REST
        |trait RESTInterface {
        |  @GET def getMethod(): Future[String]
        |  @POST def postMethod(): Future[String]
        |  @PUT def putMethod(): Future[String]
        |  @PATCH def patchMethod(): Future[String]
        |  @DELETE def deleteMethod(): Future[String]
        |}""".stripMargin
    )(GuideStyles),
    p(
      "If you skip this annotation ", i("@GET"), " or ", i("@POST"), " will be used by default. ",
      i("@POST"), " is selected when you use ", i("@Body"), " or ", i("@BodyValue"), " on any argument. "
    ),
    h3("REST methods names"),
    p(
      "All methods on the path from the root REST interface to the method making HTTP request is used to create a request URL. ",
      "By default each method name is appended after a ", i("/"), " sign. You can use the ", i("@RESTName"), " annotation in order to ",
      "override the name or ", i("@SkipRESTName"), " to skip the method name altogether."
    ),
    p("Take a look at the below example interfaces..."),
    CodeBlock(
      """import io.udash.rest._
        |
        |@REST
        |trait MainServerREST {
        |  def nested(): NestedREST
        |
        |  @SkipRESTName
        |  def skipped(): NestedREST
        |
        |  @RESTName("renamed")
        |  def name(): NestedREST
        |}
        |
        |@REST
        |trait NestedREST {
        |  @GET
        |  def string(): Future[String]
        |
        |  @POST @SkipRESTName
        |  def int(): Future[Int]
        |
        |  @PUT @RESTName("class")
        |  def cls(): Future[RestExampleClass]
        |}""".stripMargin
    )(GuideStyles),
    p("...and the corresponding mapping from method calls to URLs:"),
    ul(GuideStyles.defaultList)(
      li(i("restServer.nested().string()"), " -> ", i("GET /nested/string")),
      li(i("restServer.nested().int()"), " -> ", i("POST /nested")),
      li(i("restServer.nested().cls()"), " -> ", i("PUT /nested/class")),
      li(i("restServer.skipped().string()"), " -> ", i("GET /string")),
      li(i("restServer.skipped().int()"), " -> ", i("POST /")),
      li(i("restServer.skipped().cls()"), " -> ", i("PUT /class")),
      li(i("restServer.name().string()"), " -> ", i("GET /renamed/string")),
      li(i("restServer.name().int()"), " -> ", i("POST /renamed")),
      li(i("restServer.name().cls()"), " -> ", i("PUT /renamed/class"))
    ),
    p(
      "These annotations can be used only in Udash REST client. You cannot skip method name in exposed interface. ",
      "If you want to rename exposed method, use ", i("@RPCName"), " and remember that it has to be unique."
    ),
    h3("Argument types"),
    p(
      "Methods in a REST interface can take arguments. Every argument should be decorated with an annotation ",
      "describing how to map this argument to an HTTP request: "
    ),
    ul(GuideStyles.defaultList)(
      li(i("@URLPart"), " - the argument will be appended to the request URL after a method name."),
      li(i("@Query"), " - the argument will be sent as a query parameter - default method."),
      li(i("@Header"), " - the argument will be sent as a request header."),
      li(i("@Body"), " - the argument will be sent in a request body, only one argument can be sent this way."),
      li(i("@BodyValue"), " - the argument will be sent as a part of request body.")
    ),
    CodeBlock(
      """import io.udash.rest._
        |
        |@REST
        |trait RESTInterface {
        |  @GET def url(@URLPart arg: String): Future[String]
        |  @GET def query(@Query arg: String): Future[String]
        |  @GET def header(@Header arg: String): Future[String]
        |  @POST def body(@Body arg: String): Future[String]
        |  def bodyVals(@BodyValue arg: String, @BodyValue arg2: String): Future[Int]
        |}""".stripMargin
    )(GuideStyles),
    p("The above methods map to:"),
    ul(GuideStyles.defaultList)(
      li(i("restServer.url(\"value\")"), " -> ", i("GET /url/value"), "."),
      li(i("restServer.query(\"value\")"), " -> ", i("GET /query?arg=value"), "."),
      li(i("restServer.header(\"value\")"), " -> ", i("GET /header"), " with header ", i("arg: \"value\""), "."),
      li(i("restServer.body(\"value\")"), " -> ", i("POST /body"), " with body ", i("\"value\""), ".")
    ),
    p("It is possible to annotate an argument with ", i("@RESTParamName"), " to override the argument name in the query and header methods."),
    h2("What's next?"),
    p(
      "Now you know more about Udash REST interfaces. You might also want to take a look at the ",
      a(href := RestClientServerState.url)("Client ➔ Server"), " communication."
    )
  )
}