package io.udash
package rest
package raw

import com.avsystem.commons._
import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.serialization.{transientDefault, whenAbsent}
import io.udash.rest.util.WithHeaders
import monix.eval.Task
import monix.execution.Scheduler
import org.scalactic.source.Position
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

case class UserId(id: String) extends AnyVal {
  override def toString: String = id
}
object UserId extends RestDataWrapperCompanion[String, UserId]

case class User(id: UserId, name: String)
object User extends RestDataCompanion[User]

class omit[T](value: => T) extends AnnotationAggregate {
  @transientDefault @whenAbsent(value)
  final def aggregated: List[StaticAnnotation] = reifyAggregated
}

trait UserApi {
  @GET def user(userId: UserId): Future[User]
  @PUT def user(user: User): Future[Unit]

  @CustomBody
  @POST("user/save") def user(
    @Path("moar/path") paf: String,
    @Header("X-Awesome") awesome: Boolean,
    @Query("f") foo: Int,
    @Cookie("co") coo: Double,
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

  @CustomBody def binaryEcho(bytes: Array[Byte]): Future[Array[Byte]]
}
object UserApi extends DefaultRestApiCompanion[UserApi]

trait RootApi {
  @Prefix("") def self: UserApi
  def subApi(id: Int, @Query query: String): UserApi
  def fail: Future[Unit]
  def failMore: Future[Unit]

  @POST @CustomBody def echoHeaders(headers: Map[String, String]): Future[WithHeaders[Unit]]
}
object RootApi extends DefaultRestApiCompanion[RootApi]

class RawRestTest extends AnyFunSuite with ScalaFutures {
  implicit def scheduler: Scheduler = Scheduler.global

  def repr(body: HttpBody, inNewLine: Boolean = true): String = body match {
    case HttpBody.Empty => ""
    case tb@HttpBody.Textual(content, _, _) =>
      s"${if (inNewLine) "" else " "}${tb.contentType}\n$content"
    case bb@HttpBody.Binary(content, _) =>
      s"${if (inNewLine) "" else " "}${bb.contentType}\n" +
        s"${content.iterator.map(b => f"$b%02X").mkString}"
  }

  def repr(req: RestRequest): String = {
    val pathRepr = req.parameters.path.map(_.value).mkString("/", "/", "")
    val queryRepr = req.parameters.query.entries.iterator
      .map({ case (k, PlainValue(v)) => s"$k=$v" }).mkStringOrEmpty("?", "&", "")
    val hasHeaders = req.parameters.headers.nonEmpty || req.parameters.cookies.nonEmpty
    val cookieHeader = Opt(req.parameters.cookies).filter(_.nonEmpty)
      .map(cs => "Cookie" -> PlainValue(cs.iterator.map({ case (n, PlainValue(v)) => s"$n=$v" }).mkString("; ")))
    val headersRepr = (req.parameters.headers.iterator ++ cookieHeader.iterator)
      .map({ case (n, PlainValue(v)) => s"$n: $v" }).mkStringOrEmpty("\n", "\n", "\n")
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
    def user(userId: UserId): Future[User] = Future.successful(User(userId, s"$userId-$id-$query"))
    def user(user: User): Future[Unit] = Future.unit
    def user(paf: String, awesome: Boolean, f: Int, c: Double, user: User): Future[Unit] = Future.unit
    def defaults(awesome: Boolean, foo: Int, kek: String): Future[Unit] = Future.unit
    def autopost(bodyarg: String): Future[String] = Future.successful(bodyarg.toUpperCase)
    def singleBodyAutopost(body: String): Future[String] = Future.successful(body.toUpperCase)
    def formpost(qarg: String, sarg: String, iarg: Int): Future[String] = Future.successful(s"$qarg-$sarg-$iarg")
    def fail: Future[Unit] = Future.failed(HttpErrorException(400, "zuo"))
    def failMore: Future[Unit] = throw HttpErrorException(400, "ZUO")
    def eatHeader(stuff: String): Future[String] = Future.successful(stuff.toLowerCase)
    def adjusted: Future[Unit] = Future.unit
    def binaryEcho(bytes: Array[Byte]): Future[Array[Byte]] = Future.successful(bytes)
    def echoHeaders(headers: Map[String, String]): Future[WithHeaders[Unit]] =
      Future.successful(WithHeaders((), headers.toList))
  }

  var trafficLog: String = _

  val real: RootApi = new RootApiImpl(0, "")
  val serverHandle: RawRest.HandleRequest = request =>
    RawRest.asHandleRequest(real).apply(request).tapEval { response =>
      Task {
        trafficLog = s"${repr(request)}\n${repr(response)}\n"
      }
    }

  val realProxy: RootApi = RawRest.fromHandleRequest[RootApi](serverHandle)

  def testRestCall[T](call: RootApi => Future[T], expectedTraffic: String)(implicit pos: Position): Unit = {
    assert(call(realProxy).wrapToTry.futureValue.map(mkDeep) == call(real).catchFailures.wrapToTry.futureValue.map(mkDeep))
    assert(trafficLog == expectedTraffic)
  }

  def mkDeep(value: Any): Any = value match {
    case arr: Array[_] => IArraySeq.empty[AnyRef] ++ arr.iterator.map(mkDeep)
    case _ => value
  }

  def assertRawExchange(request: RestRequest, response: RestResponse)(implicit pos: Position): Unit = {
    val future = serverHandle(request).runToFuture
    assert(future.futureValue == response)
  }

  test("simple GET") {
    testRestCall(_.self.user(UserId("ID")),
      """-> GET /user?userId=ID
        |<- 200 application/json;charset=utf-8
        |{"id":"ID","name":"ID-0-"}
        |""".stripMargin
    )
  }

  test("simple POST with path, header, query and cookie") {
    testRestCall(_.self.user("paf", awesome = true, 42, 3.14, User(UserId("ID"), "Fred")),
      """-> POST /user/save/paf/moar/path?f=42
        |X-Awesome: true
        |Cookie: co=3.14
        |application/json;charset=utf-8
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
      """-> POST /autopost application/json;charset=utf-8
        |{"bodyarg":"bod"}
        |<- 200 application/json;charset=utf-8
        |"BOD"
        |""".stripMargin)
  }

  test("single body auto POST") {
    testRestCall(_.self.singleBodyAutopost("bod"),
      """-> POST /singleBodyAutopost application/json;charset=utf-8
        |"bod"
        |<- 200 application/json;charset=utf-8
        |"BOD"
        |""".stripMargin)
  }

  test("form POST") {
    testRestCall(_.self.formpost("qu", "a=b", 42),
      """-> POST /formpost?qarg=qu application/x-www-form-urlencoded;charset=utf-8
        |sarg=a%3Db&iarg=42
        |<- 200 application/json;charset=utf-8
        |"qu-a=b-42"
        |""".stripMargin)
  }

  test("simple GET after prefix call") {
    testRestCall(_.subApi(1, "query").user(UserId("ID")),
      """-> GET /subApi/1/user?query=query&userId=ID
        |<- 200 application/json;charset=utf-8
        |{"id":"ID","name":"ID-1-query"}
        |""".stripMargin
    )
  }

  test("failing POST") {
    testRestCall(_.fail,
      """-> POST /fail
        |<- 400 text/plain;charset=utf-8
        |zuo
        |""".stripMargin
    )
  }

  test("throwing POST") {
    testRestCall(_.failMore,
      """-> POST /failMore
        |<- 400 text/plain;charset=utf-8
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

  test("binary body") {
    testRestCall(_.self.binaryEcho(Array.fill[Byte](5)(5)),
      """-> POST /binaryEcho application/octet-stream
        |0505050505
        |<- 200 application/octet-stream
        |0505050505
        |""".stripMargin)
  }

  test("response with headers") {
    testRestCall(_.echoHeaders(IListMap("X-A" -> "a", "X-B" -> "b")),
      """-> POST /echoHeaders application/json;charset=utf-8
        |{"X-A":"a","X-B":"b"}
        |<- 204
        |X-A: a
        |X-B: b
        |""".stripMargin
    )
  }

  test("OPTIONS") {
    val request = RestRequest(HttpMethod.OPTIONS, RestParameters(List(PlainValue("user"))), HttpBody.Empty)
    val response = RestResponse(200, IMapping.create("Allow" -> PlainValue("GET,HEAD,PUT,OPTIONS")), HttpBody.Empty)
    assertRawExchange(request, response)
  }

  test("HEAD") {
    val params = RestParameters(List(PlainValue("user")), query = Mapping.create("userId" -> PlainValue("UID")))
    val request = RestRequest(HttpMethod.HEAD, params, HttpBody.Empty)
    val response = RestResponse(200, IMapping.empty, HttpBody.empty)
    assertRawExchange(request, response)
  }

  test("header case insensitivity") {
    val params = RestParameters(
      path = List(PlainValue("eatHeader")),
      headers = IMapping.create("x-sTuFf" -> PlainValue("StUfF"))
    )
    val request = RestRequest(HttpMethod.POST, params, HttpBody.Empty)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("\"stuff\"")))
    assertRawExchange(request, response)
  }

  test("bad body") {
    val request = RestRequest(HttpMethod.PUT, RestParameters(List(PlainValue("user"))), HttpBody.json(JsonValue(" \n  \n {")))
    val response = RestResponse(400, IMapping.empty, HttpBody.plain(
      "Invalid HTTP body: Unexpected EOF (line 3, column 3) (line content:  {)"))
    assertRawExchange(request, response)
  }

  test("bad argument") {
    val body = HttpBody.json(JsonValue("{\"user\": {}}"))
    val request = RestRequest(HttpMethod.PUT, RestParameters(List(PlainValue("user"))), body)
    val response = RestResponse(400, IMapping.empty, HttpBody.plain(
      "Argument user of RPC put_user is invalid: " +
        "Cannot read io.udash.rest.raw.User, field id is missing in decoded data"
    ))
    assertRawExchange(request, response)
  }

  test("missing argument") {
    val request = RestRequest(HttpMethod.GET, RestParameters(List(PlainValue("user"))), HttpBody.Empty)
    val response = RestResponse(400, IMapping.empty, HttpBody.plain("Argument userId of RPC user is missing"))
    assertRawExchange(request, response)
  }

  test("missing argument in prefix") {
    val request = RestRequest(HttpMethod.GET, RestParameters(PlainValue.decodePath("subApi/42/user")), HttpBody.Empty)
    val response = RestResponse(400, IMapping.empty, HttpBody.plain("Argument query of RPC subApi is missing"))
    assertRawExchange(request, response)
  }

  test("parsing textual content without charset") {
    val body = HttpBody.binary("""{"bodyarg":"value"}""".getBytes(HttpBody.Utf8Charset), HttpBody.JsonType)
    val request = RestRequest(HttpMethod.POST, RestParameters(List(PlainValue("autopost"))), body)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("\"VALUE\"")))
    assertRawExchange(request, response)
  }
}
