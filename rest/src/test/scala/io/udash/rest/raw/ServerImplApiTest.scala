package io.udash.rest.raw

import com.avsystem.commons.Promise
import io.udash.rest.SomeServerApiImpl
import org.scalactic.source.Position
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

class ServerImplApiTest extends AnyFunSuite with ScalaFutures {
  private val apiImpl = new SomeServerApiImpl
  private val serverHandle = SomeServerApiImpl.asHandleRequest(apiImpl)

  def assertRawExchange(request: RestRequest, response: RestResponse)(implicit pos: Position): Unit = {
    val promise = Promise[RestResponse]()
    serverHandle(request).apply(promise.complete)
    assert(promise.future.futureValue == response)
  }

  test("simple call") {
    val params = RestParameters(
      path = PlainValue.decodePath("/thingy"),
      query = Mapping.create("param" -> PlainValue("42"))
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("\"41\"")))
    assertRawExchange(request, response)
  }

}
