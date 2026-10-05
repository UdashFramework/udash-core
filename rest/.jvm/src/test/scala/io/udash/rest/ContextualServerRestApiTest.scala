package io.udash
package rest

import io.udash.rest.raw._
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// Server-only contextual API defined as a trait + separate implementation.
trait CtxGreetApi {
  @GET def greet(@Query tag: Tag): CtxRestApis.CtxTask[String]
}
object CtxGreetApi extends CtxRestApis.ServerApiCompanion[CtxGreetApi]

class CtxGreetApiImpl extends CtxGreetApi {
  def greet(tag: Tag): CtxRestApis.CtxTask[String] =
    CtxRestApis.CtxTask { ctx => Task.now(s"${ctx.user}:${tag.value}") }
}

// Server-only contextual API defined directly as an implementation class (no separate trait).
class CtxPingImpl {
  @GET def ping(@Query tag: Tag): CtxRestApis.CtxTask[String] =
    CtxRestApis.CtxTask { ctx => Task.now(s"pong-${ctx.user}-${tag.value}") }
}
object CtxPingImpl extends CtxRestApis.ServerApiImplCompanion[CtxPingImpl]

// Server-only contextual API without OpenAPI generation.
trait CtxGreetNoDocApi {
  @GET def greet(@Query tag: Tag): CtxRestApis.CtxTask[String]
}
object CtxGreetNoDocApi extends CtxRestApis.ServerNoDocApiCompanion[CtxGreetNoDocApi]

class CtxGreetNoDocApiImpl extends CtxGreetNoDocApi {
  def greet(tag: Tag): CtxRestApis.CtxTask[String] =
    CtxRestApis.CtxTask { ctx => Task.now(s"nodoc-${ctx.user}:${tag.value}") }
}

// Server-only contextual API with a streamed result.
trait CtxStreamApi {
  @GET def numbers(@Query n: Int): CtxRestApis.CtxTask[Observable[String]]
}
object CtxStreamApi extends CtxRestApis.ServerApiCompanion[CtxStreamApi]

class CtxStreamApiImpl extends CtxStreamApi {
  def numbers(n: Int): CtxRestApis.CtxTask[Observable[String]] =
    CtxRestApis.CtxTask.sync(ctx => Observable.range(0, n).map(i => s"${ctx.user}-$i"))
}

class ContextualServerRestApiTest extends AnyFunSuite with ScalaFutures with Matchers {
  implicit def scheduler: Scheduler = Scheduler.global

  private def getRequest(path: String, query: (String, String)*): RestRequest =
    RestRequest(
      HttpMethod.GET,
      RestParameters(List(PlainValue(path)), query = Mapping.create(query.map { case (k, v) => k -> PlainValue(v) }: _*)),
      HttpBody.Empty,
    )

  private def getTag(handle: RawRest.HandleRequest, path: String, tagQuery: String): RestResponse =
    handle(getRequest(path, "tag" -> tagQuery)).runToFuture.futureValue

  test("ServerApiCompanion: injected Tag serialization is used and context is applied") {
    implicit val ctx: UserCtx = UserCtx("alice")
    val handle = RawRest.asHandleRequest[CtxGreetApi](new CtxGreetApiImpl)
    val resp = getTag(handle, "greet", "tag:hello")
    resp.code shouldBe 200
    resp.body.textualContentOpt.get shouldBe "\"alice:hello\""
  }

  test("ServerApiImplCompanion: same behavior for a bare implementation class") {
    implicit val ctx: UserCtx = UserCtx("alice")
    val handle = RawRest.asHandleRequest[CtxPingImpl](new CtxPingImpl)
    val resp = getTag(handle, "ping", "tag:world")
    resp.code shouldBe 200
    resp.body.textualContentOpt.get shouldBe "\"pong-alice-world\""
  }

  test("ServerNoDocApiCompanion: same behavior without OpenAPI generation") {
    implicit val ctx: UserCtx = UserCtx("alice")
    val handle = RawRest.asHandleRequest[CtxGreetNoDocApi](new CtxGreetNoDocApiImpl)
    val resp = getTag(handle, "greet", "tag:hello")
    resp.code shouldBe 200
    resp.body.textualContentOpt.get shouldBe "\"nodoc-alice:hello\""
  }

  test("handler built per request applies the context of each request") {
    val handle: RawRest.HandleRequest = request => {
      implicit val ctx: UserCtx = UserCtx(request.parameters.headers.lift("X-User").fold("anonymous")(_.value))
      RawRest.asHandleRequest[CtxGreetApi](new CtxGreetApiImpl).apply(request)
    }

    def greetAs(user: String): String =
      handle(getRequest("greet", "tag" -> "tag:hi").header("X-User", user))
        .runToFuture.futureValue.body.textualContentOpt.get

    greetAs("alice") shouldBe "\"alice:hi\""
    greetAs("bob") shouldBe "\"bob:hi\""
  }

  test("ServerApiCompanion: contextual method can stream its result") {
    implicit val ctx: UserCtx = UserCtx("alice")
    val handle = RawRest.asHandleRequestWithStreaming[CtxStreamApi](new CtxStreamApiImpl)
    val resp = handle(getRequest("numbers", "n" -> "3")).runToFuture.futureValue
    val elements = resp match {
      case StreamedRestResponse(200, _, StreamedBody.JsonList(elements, _, _)) =>
        elements.map(_.value).toListL.runToFuture.futureValue
      case other => fail(s"expected a streamed JSON list response, got $other")
    }
    elements shouldBe List("\"alice-0\"", "\"alice-1\"", "\"alice-2\"")
  }
}
