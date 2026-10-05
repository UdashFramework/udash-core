package io.udash
package rest

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.openapi.{Info, RefOr}
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

// Same, but without OpenAPI generation.
trait EchoNoDocApi {
  @GET def echo(@Query tag: Tag): Task[String]
}
object EchoNoDocApi extends CustomRestApis.NoDocApiCompanion[EchoNoDocApi]

class EchoNoDocApiImpl extends EchoNoDocApi {
  def echo(tag: Tag): Task[String] = Task.now(s"nodoc:${tag.value}")
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
    val openapi = EchoApi.openapiMetadata.openapi(Info("Echo", "1.0"))
    val parameters = openapi.paths.paths("/echo") match {
      case RefOr.Value(pathItem) => pathItem.get.get.parameters
      case ref => fail(s"expected an inline path item, got $ref")
    }
    // the parameter schema is the injected RestSchema[Tag] (a plain string)
    JsonStringOutput.writePretty(parameters) shouldBe
      """[
        |  {
        |    "name": "tag",
        |    "in": "query",
        |    "required": true,
        |    "explode": false,
        |    "schema": {
        |      "type": "string"
        |    }
        |  }
        |]""".stripMargin
  }

  test("NoDocApiCompanion round-trips (client + server, no OpenAPI)") {
    @volatile var lastRequest: RestRequest = null
    val serverHandle: RawRest.HandleRequest = { req =>
      lastRequest = req
      RawRest.asHandleRequest[EchoNoDocApi](new EchoNoDocApiImpl).apply(req)
    }
    val client: EchoNoDocApi = RawRest.fromHandleRequest[EchoNoDocApi](serverHandle)

    client.echo(Tag("hi")).runToFuture.futureValue shouldBe "nodoc:hi"

    lastRequest.parameters.query.entries.collectFirst {
      case (k, PlainValue(v)) if k == "tag" => v
    } shouldBe Some("tag:hi")
  }
}
