package io.udash
package rest

import io.udash.rest.raw.{RawRest, RestRequest, RestResponse, StreamedRestResponse}
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// Contextual API used on both sides: parameterized by context, client fixes it to NoCtx.
trait GreetSharedApi[Ctx] extends CtxSharedRestApis.Api[Ctx] {
  @GET def greet(@Query tag: Tag): CtxTask[String]
}
object GreetSharedApi extends CtxSharedRestApis.ApiCompanion[GreetSharedApi]

class GreetSharedApiImpl extends GreetSharedApi[UserCtx] {
  def greet(tag: Tag): CtxTask[String] = CtxTask { ctx => Task.now(s"${ctx.user}:${tag.value}") }
}

// Same, but without OpenAPI generation.
trait GreetSharedNoDocApi[Ctx] extends CtxSharedRestApis.Api[Ctx] {
  @GET def greet(@Query tag: Tag): CtxTask[String]
}
object GreetSharedNoDocApi extends CtxSharedRestApis.NoDocApiCompanion[GreetSharedNoDocApi]

class GreetSharedNoDocApiImpl extends GreetSharedNoDocApi[UserCtx] {
  def greet(tag: Tag): CtxTask[String] = CtxTask { ctx => Task.now(s"nodoc-${ctx.user}:${tag.value}") }
}

// Shared contextual API with a streamed result.
trait StreamSharedApi[Ctx] extends CtxSharedRestApis.Api[Ctx] {
  @GET def numbers(@Query n: Int): CtxTask[Observable[String]]
}
object StreamSharedApi extends CtxSharedRestApis.ApiCompanion[StreamSharedApi]

class StreamSharedApiImpl extends StreamSharedApi[UserCtx] {
  def numbers(n: Int): CtxTask[Observable[String]] =
    CtxTask.sync(ctx => Observable.range(0, n).map(i => s"${ctx.user}-$i"))
}

class ContextualServerAndClientRestApiTest extends AnyFunSuite with ScalaFutures with Matchers {
  implicit def scheduler: Scheduler = Scheduler.global

  test("server (with context) and client (NoCtx) round-trip over the custom Tag type") {
    implicit val ctx: UserCtx = UserCtx("bob")
    val serverHandle: RawRest.HandleRequest =
      RawRest.asHandleRequest[GreetSharedApi[UserCtx]](new GreetSharedApiImpl)

    // client type is GreetSharedApi[NoCtx]
    val client: GreetSharedApi.Client = RawRest.fromHandleRequest[GreetSharedApi.Client](serverHandle)

    // .result resolves the NoCtx context implicitly, then the task fires the request through serverHandle
    client.greet(Tag("hi")).result.runToFuture.futureValue shouldBe "bob:hi"
  }

  test("NoDocApiCompanion: same round-trip without OpenAPI generation") {
    implicit val ctx: UserCtx = UserCtx("bob")
    val serverHandle: RawRest.HandleRequest =
      RawRest.asHandleRequest[GreetSharedNoDocApi[UserCtx]](new GreetSharedNoDocApiImpl)

    val client: GreetSharedNoDocApi.Client =
      RawRest.fromHandleRequest[GreetSharedNoDocApi.Client](serverHandle)

    client.greet(Tag("hi")).result.runToFuture.futureValue shouldBe "nodoc-bob:hi"
  }

  test("contextual method can stream its result to the client") {
    implicit val ctx: UserCtx = UserCtx("bob")
    val serverHandle: RawRest.HandleRequestWithStreaming =
      RawRest.asHandleRequestWithStreaming[StreamSharedApi[UserCtx]](new StreamSharedApiImpl)

    val client: StreamSharedApi.Client =
      RawRest.fromHandleRequestWithStreaming[StreamSharedApi.Client](new RawRest.RestRequestHandler {
        def handleRequest(request: RestRequest): Task[RestResponse] =
          serverHandle(request).map(_.asInstanceOf[RestResponse])
        def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
          serverHandle(request).map(_.asInstanceOf[StreamedRestResponse])
      })

    client.numbers(3).result.flatMap(_.toListL).runToFuture.futureValue shouldBe List("bob-0", "bob-1", "bob-2")
  }
}
