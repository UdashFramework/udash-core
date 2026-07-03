package io.udash
package rest

import io.udash.rest.raw._
import monix.eval.Task
import monix.execution.Scheduler
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

class ContextualServerRestApiTest extends AnyFunSuite with ScalaFutures with Matchers {
  implicit def scheduler: Scheduler = Scheduler.global
  implicit val ctx: UserCtx = UserCtx("alice")

  private def getTag(handle: RawRest.HandleRequest, path: String, tagQuery: String): RestResponse =
    handle(RestRequest(
      HttpMethod.GET,
      RestParameters(List(PlainValue(path)), query = Mapping.create("tag" -> PlainValue(tagQuery))),
      HttpBody.Empty,
    )).runToFuture.futureValue

  test("ServerApiCompanion: injected Tag serialization is used and context is applied") {
    val handle = RawRest.asHandleRequest[CtxGreetApi](new CtxGreetApiImpl)
    val resp = getTag(handle, "greet", "tag:hello")
    resp.code shouldBe 200
    resp.body.textualContentOpt.get shouldBe "\"alice:hello\""
  }

  test("ServerApiImplCompanion: same behavior for a bare implementation class") {
    val handle = RawRest.asHandleRequest[CtxPingImpl](new CtxPingImpl)
    val resp = getTag(handle, "ping", "tag:world")
    resp.code shouldBe 200
    resp.body.textualContentOpt.get shouldBe "\"pong-alice-world\""
  }
}
