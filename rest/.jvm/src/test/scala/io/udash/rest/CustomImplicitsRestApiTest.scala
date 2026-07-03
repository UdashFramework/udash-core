package io.udash
package rest

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.openapi.Info
import io.udash.rest.raw.{PlainValue, RawRest, RestRequest}
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// Non-contextual API whose companion is derived through the custom-implicits bundle.
trait EchoApi {
  @GET def echo(@Query tag: Tag): Task[String]
}
object EchoApi extends CustomRestApis.ApiCompanion[EchoApi]

class EchoApiImpl extends EchoApi {
  def echo(tag: Tag): Task[String] = Task.now(s"got:${tag.value}")
}

class CustomImplicitsRestApiTest extends AnyFunSuite with ScalaFutures with Matchers {
  implicit def scheduler: Scheduler = Scheduler.global

  test("round-trips using the injected Tag serialization on both sides") {
    @volatile var lastRequest: RestRequest = null
    val serverHandle: RawRest.HandleRequest = { req =>
      lastRequest = req
      RawRest.asHandleRequest[EchoApi](new EchoApiImpl).apply(req)
    }
    val client: EchoApi = RawRest.fromHandleRequest[EchoApi](serverHandle)

    // server-side decode ("got:") and successful result prove the injected AsReal[PlainValue, Tag] was used
    client.echo(Tag("hi")).runToFuture.futureValue shouldBe "got:hi"

    // client-side encode: the outgoing query uses the custom `tag:` format => injected AsRaw was collected
    val queryValue = lastRequest.parameters.query.entries.collectFirst {
      case (k, PlainValue(v)) if k == "tag" => v
    }
    queryValue shouldBe Some("tag:hi")
  }

  test("OpenAPI is generated from the injected Tag schema") {
    // `object EchoApi extends CustomRestApis.ApiCompanion[EchoApi]` already requires a RestSchema[Tag]
    // from the injected bundle to compile; here we just confirm the document renders.
    val openapi = EchoApi.openapiMetadata.openapi(Info("Echo", "1.0"))
    val json = JsonStringOutput.writePretty(openapi)
    json should include("/echo")
    json should include("tag")
  }
}
