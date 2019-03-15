package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.meta.Mapping
import com.avsystem.commons.serialization.{transientDefault, whenAbsent}
import org.scalactic.source.Position
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures

case class User(id: String, name: String)
object User extends RestDataCompanion[User]

class omit[T](value: => T) extends AnnotationAggregate {
  @transientDefault @whenAbsent(value) type Implied
}

trait UserApi {
  @GET def user(userId: String): Future[User]
  @PUT def user(user: User): Future[Unit]

  @CustomBody
  @POST("user/save") def user(
    @Path("moar/path") paf: String,
    @Header("X-Awesome") awesome: Boolean,
    @Query("f") foo: Int,
    user: User
  ): Future[Unit]

  @POST def defaults(
    @omit(false) @Header("X-Awesome") awesome: Boolean = whenAbsent.value,
    @transientDefault @Query("f") foo: Int = 42,
    @omit("lel") kek: String = whenAbsent.value
  ): Future[Unit]

  def autopost(bodyarg: String): Future[String]
  @CustomBody def singleBodyAutopost(body: String): Future[String]
  @FormBody def formpost(@Query qarg: String, sarg: String, iarg: Int): Future[String]

  def eatHeader(@Header("X-Stuff") stuff: String): Future[String]

  @addRequestHeader("X-Req-Custom", "custom-req")
  @addResponseHeader("X-Res-Custom", "custom-res")
  def adjusted: Future[Unit]
}
object UserApi extends DefaultRestApiCompanion[UserApi]

trait RootApi {
  @Prefix("") def self: UserApi
  def subApi(id: Int, @Query query: String): UserApi
  def fail: Future[Unit]
  def failMore: Future[Unit]
}
object RootApi extends DefaultRestApiCompanion[RootApi]

class RawRestTest extends FunSuite with ScalaFutures {
  def repr(body: HttpBody, inNewLine: Boolean = true): String = body match {
    case HttpBody.Empty => ""
    case HttpBody(content, mimeType) => s"${if (inNewLine) "" else " "}$mimeType\n$content"
  }

  def repr(req: RestRequest): String = {
    val pathRepr = req.parameters.path.map(_.value).mkString("/", "/", "")
    val queryRepr = req.parameters.query.iterator
      .map({ case (k, v) => s"$k=${v.value}" }).mkStringOrEmpty("?", "&", "")
    val hasHeaders = req.parameters.headers.nonEmpty
    val headersRepr = req.parameters.headers.iterator
      .map({ case (n, v) => s"$n: ${v.value}" }).mkStringOrEmpty("\n", "\n", "\n")
    s"-> ${req.method} $pathRepr$queryRepr$headersRepr${repr(req.body, hasHeaders)}".trim
  }

  def repr(resp: RestResponse): String = {
    val hasHeaders = resp.headers.nonEmpty
    val headersRepr = resp.headers.iterator
      .map({ case (n, v) => s"$n: ${v.value}" }).mkStringOrEmpty("\n", "\n", "\n")
    s"<- ${resp.code}$headersRepr${repr(resp.body, hasHeaders)}".trim
  }

  class RootApiImpl(id: Int, query: String) extends RootApi with UserApi {
    def self: UserApi = this
    def subApi(newId: Int, newQuery: String): UserApi = new RootApiImpl(newId, query + newQuery)
    def user(userId: String): Future[User] = Future.successful(User(userId, s"$userId-$id-$query"))
    def user(user: User): Future[Unit] = Future.unit
    def user(paf: String, awesome: Boolean, f: Int, user: User): Future[Unit] = Future.unit
    def defaults(awesome: Boolean, foo: Int, kek: String): Future[Unit] = Future.unit
    def autopost(bodyarg: String): Future[String] = Future.successful(bodyarg.toUpperCase)
    def singleBodyAutopost(body: String): Future[String] = Future.successful(body.toUpperCase)
    def formpost(qarg: String, sarg: String, iarg: Int): Future[String] = Future.successful(s"$qarg-$sarg-$iarg")
    def fail: Future[Unit] = Future.failed(HttpErrorException(400, "zuo"))
    def failMore: Future[Unit] = throw HttpErrorException(400, "ZUO")
    def eatHeader(stuff: String): Future[String] = Future.successful(stuff.toLowerCase)
    def adjusted: Future[Unit] = Future.unit
  }

  var trafficLog: String = _

  val real: RootApi = new RootApiImpl(0, "")
  val serverHandle: RawRest.HandleRequest = request => callback => {
    RawRest.asHandleRequest(real).apply(request) { result =>
      callback(result)
      result match {
        case Success(response) =>
          trafficLog = s"${repr(request)}\n${repr(response)}\n"
        case _ =>
      }
    }
  }

  val realProxy: RootApi = RawRest.fromHandleRequest[RootApi](serverHandle)

  def testRestCall[T](call: RootApi => Future[T], expectedTraffic: String)(implicit pos: Position): Unit = {
    assert(call(realProxy).wrapToTry.futureValue == call(real).catchFailures.wrapToTry.futureValue)
    assert(trafficLog == expectedTraffic)
  }

  test("simple GET") {
    testRestCall(_.self.user("ID"),
      """-> GET /user?userId=ID
        |<- 200 application/json
        |{"id":"ID","name":"ID-0-"}
        |""".stripMargin
    )
  }

  test("simple POST with path, header and query") {
    testRestCall(_.self.user("paf", awesome = true, 42, User("ID", "Fred")),
      """-> POST /user/save/paf/moar/path?f=42
        |X-Awesome: true
        |application/json
        |{"id":"ID","name":"Fred"}
        |<- 204
        |""".stripMargin)
  }

  test("simple POST using default transient values") {
    testRestCall(_.self.defaults(),
      """-> POST /defaults
        |<- 204
        |""".stripMargin)
  }

  test("auto POST") {
    testRestCall(_.self.autopost("bod"),
      """-> POST /autopost application/json
        |{"bodyarg":"bod"}
        |<- 200 application/json
        |"BOD"
        |""".stripMargin)
  }

  test("single body auto POST") {
    testRestCall(_.self.singleBodyAutopost("bod"),
      """-> POST /singleBodyAutopost application/json
        |"bod"
        |<- 200 application/json
        |"BOD"
        |""".stripMargin)
  }

  test("form POST") {
    testRestCall(_.self.formpost("qu", "a=b", 42),
      """-> POST /formpost?qarg=qu application/x-www-form-urlencoded
        |sarg=a%3Db&iarg=42
        |<- 200 application/json
        |"qu-a=b-42"
        |""".stripMargin)
  }

  test("simple GET after prefix call") {
    testRestCall(_.subApi(1, "query").user("ID"),
      """-> GET /subApi/1/user?query=query&userId=ID
        |<- 200 application/json
        |{"id":"ID","name":"ID-1-query"}
        |""".stripMargin
    )
  }

  test("failing POST") {
    testRestCall(_.fail,
      """-> POST /fail
        |<- 400 text/plain
        |zuo
        |""".stripMargin
    )
  }

  test("throwing POST") {
    testRestCall(_.failMore,
      """-> POST /failMore
        |<- 400 text/plain
        |ZUO
        |""".stripMargin
    )
  }

  test("request and response adjusting") {
    testRestCall(_.self.adjusted,
      """-> POST /adjusted
        |X-Req-Custom: custom-req
        |<- 204
        |X-Res-Custom: custom-res
        |""".stripMargin
    )
  }

  test("OPTIONS") {
    val request = RestRequest(HttpMethod.OPTIONS, RestParameters(List(PathValue("user"))), HttpBody.Empty)
    val response = RestResponse(200, Mapping("Allow" -> HeaderValue("GET,HEAD,PUT,OPTIONS")), HttpBody.Empty)

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("HEAD") {
    val params = RestParameters(List(PathValue("user")), query = Mapping("userId" -> QueryValue("UID")))
    val request = RestRequest(HttpMethod.HEAD, params, HttpBody.Empty)
    val response = RestResponse(200, Mapping.empty, HttpBody.empty)

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("header case insensitivity") {
    val params = RestParameters(
      path = List(PathValue("eatHeader")),
      headers = Mapping(List("x-sTuFf" -> HeaderValue("StUfF")), caseInsensitive = true)
    )
    val request = RestRequest(HttpMethod.POST, params, HttpBody.Empty)
    val response = RestResponse(200, Mapping.empty, HttpBody.json(JsonValue("\"stuff\"")))
    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("bad body") {
    val request = RestRequest(HttpMethod.PUT, RestParameters(List(PathValue("user"))), HttpBody.json(JsonValue(" \n  \n {")))
    val response = RestResponse(400, Mapping.empty, HttpBody.plain(
      "Invalid HTTP body: Unexpected EOF (line 3, column 3) (line content:  {)"))

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("bad argument") {
    val body = HttpBody.json(JsonValue("{\"user\": {}}"))
    val request = RestRequest(HttpMethod.PUT, RestParameters(List(PathValue("user"))), body)
    val response = RestResponse(400, Mapping.empty, HttpBody.plain(
      "Argument user of RPC put_user is invalid: " +
        "Cannot read io.udash.rest.raw.User, field id is missing in decoded data"
    ))

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("missing argument") {
    val request = RestRequest(HttpMethod.GET, RestParameters(List(PathValue("user"))), HttpBody.Empty)
    val response = RestResponse(400, Mapping.empty, HttpBody.plain("Argument userId of RPC user is missing"))

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("missing argument in prefix") {
    val request = RestRequest(HttpMethod.GET, RestParameters(PathValue.splitDecode("subApi/42/user")), HttpBody.Empty)
    val response = RestResponse(400, Mapping.empty, HttpBody.plain("Argument query of RPC subApi is missing"))

    val promise = Promise[RestResponse]
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }
}
