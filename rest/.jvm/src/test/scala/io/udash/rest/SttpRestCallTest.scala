package io.udash
package rest

import io.udash.rest.raw.HttpErrorException
import io.udash.rest.raw.RawRest.HandleRequest
import sttp.client.SttpBackend

import scala.concurrent.Future
import scala.concurrent.duration._

trait SttpClientRestTest extends ServletBasedRestApiTest {
  implicit val backend: SttpBackend[Future, Nothing, Nothing] = SttpRestClient.defaultBackend()

  def clientHandle: HandleRequest =
    SttpRestClient.asHandleRequest(s"$baseUrl/api")

  override protected def afterAll(): Unit = {
    backend.close()
    super.afterAll()
  }
}

class SttpRestCallTest extends SttpClientRestTest with RestApiTestScenarios {
  test("too large binary request") {
    val future = proxy.binaryEcho(Array.fill[Byte](maxPayloadSize + 1)(5))
    val exception = future.failed.futureValue
    assert(exception == HttpErrorException(413, "Payload is larger than maximum 1048576 bytes (1048577)"))
  }
}

class ServletTimeoutTest extends SttpClientRestTest {
  override def serverTimeout: FiniteDuration = 500.millis

  test("rest method timeout") {
    val exception = proxy.neverGet.failed.futureValue
    assert(exception == HttpErrorException(500, "server operation timed out"))
  }
}
